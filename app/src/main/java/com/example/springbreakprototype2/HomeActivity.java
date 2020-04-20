package com.example.springbreakprototype2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.Date;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, GoodServiceDialogFragment.NoticeDialogListener {
    private TextView test;
    private Spinner categories;
    private String[] list_categories_good = {"All goods", "Furniture", "Textbooks", "Clothes", "Misc."};
    private String[] list_categories_service = {"All services", "Tutoring", "Moving", "Haircuts"};
    private String[] list_categories;
    private Product[] myDataset;
    private ArrayList<Product> data = new ArrayList<Product>();

    private Double lower_price, upper_price;
    private String sort_by, categories_value, good_service_value, user_name;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        String previous = extras.getString("PREVIOUS");
        lower_price = extras.getDouble("LOWER_PRICE");
        upper_price = extras.getDouble("UPPER_PRICE");
        sort_by = extras.getString("SORT_BY");
        categories_value = extras.getString("CATEGORIES");
        good_service_value = extras.getString("GOOD_SERVICE");
        user_name = extras.getString("USERNAME");

        test = (TextView) findViewById(R.id.test);

        categories = (Spinner) findViewById(R.id.categories);
        updateSpinner();

        //if (previous.equals("FILTER")) {
        String buildingFilters = "";
        if (lower_price != -1) {
            buildingFilters = "Above: $" + lower_price + ", ";
        }
        if (upper_price != -1) {
            buildingFilters = buildingFilters + "Below: $" + upper_price + ", ";
        }
        buildingFilters = buildingFilters + "Sorting by: " + sort_by;
        test.setText(buildingFilters);
        //}

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a grid layout manager
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        refreshListing();
        //Toast.makeText(getApplicationContext(), "here", Toast.LENGTH_LONG).show();

        //it's time for a nav bar
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    //the nav bar listener
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch(menuItem.getItemId()){
                        case R.id.nav_goods:
                            good_service_value = "good";
                            updateSpinner();
                            break;
                        case R.id.nav_services:
                            good_service_value = "service";
                            updateSpinner();
                            break;
                        case R.id.nav_sell:
                            GoodServiceDialogFragment posting = new GoodServiceDialogFragment();
                            posting.show(getSupportFragmentManager(), "test");
                            break;
                    }
                    return true;
                }
            };

    //If selected good
    public void onDialogGoodClick(DialogFragment dialog) {
        goPostingEvent("good");
    }

    //If selected service
    public void onDialogServiceClick(DialogFragment dialog) {
        goPostingEvent("service");
    }

    //some leftover blank method idk
    public void clickItem(View view) {
        //Toast.makeText(getApplicationContext(),view.getId(),Toast.LENGTH_SHORT).show();
    }

    public void viewPosting(Product p) {
        Intent intent = new Intent(this, ViewPostingActivity.class);
        Bundle extras = new Bundle();

        extras.putDouble("LOWER_PRICE", lower_price);
        extras.putDouble("UPPER_PRICE", upper_price);
        extras.putString("SORT_BY", sort_by);
        extras.putString("CATEGORIES", categories_value);
        extras.putString("GOOD_SERVICE", good_service_value);
        extras.putString("USERNAME", user_name);
        extras.putString("BACK", "home");
        extras.putDouble("PRODUCT_PRICE", p.getPrice());
        extras.putString("PRODUCT_SELLER", p.getSeller());
        extras.putString("PRODUCT_TITLE", p.getTitle());
        extras.putString("PRODUCT_DESCRIPTION", p.getDescription());
        extras.putString("PRODUCT_TIMESTAMP", p.getTime().toString());
        extras.putString("PRODUCT_CATEGORY", p.getCategory());

        intent.putExtras(extras);
        startActivity(intent);
    }

    //Looks
    public void refreshListing2(Product[] data2) {
//        Product[] data = new ProductData[data2.length];
//        for (int i = 0; i < data2.length; i++) {
//            data[i] = new IconData(data2[i], android.R.drawable.ic_delete);
//        }
//        IconData[] data = new IconData[] {
//                new IconData("Delete", android.R.drawable.ic_delete),
//                new IconData("Alert", android.R.drawable.ic_dialog_alert)
//        };
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        ProductAdapter adapter = new ProductAdapter(data2, new ProductAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Product p) {
                viewPosting(p);
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
    }

    //Looks at all filters/good or service/category to then call displayData/displayDataForAll
    public void refreshListing() {
        CollectionReference list_items;
        //Use good_service_value for good/service, use categories_value for the category, use lower_price/upper_price, sort_by
        //Toast.makeText(getApplicationContext(), "here", Toast.LENGTH_SHORT);

        String collection_name;
        if (good_service_value.equals("good")) {
            collection_name = "good_price";
        } else {
            collection_name = "service_price";
        }

        if (categories_value.equals("All goods") || categories_value.equals("All services")) {

            Query q = db.collectionGroup(collection_name);

            q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        data.clear();
                        displayDataForAll(task, sort_by, lower_price, upper_price);
                    }
                }
            });

            return;
        } else {
            list_items = db.collection("products").document(good_service_value).collection(categories_value.toLowerCase())
                    .document("temp").collection(collection_name);
        }
        Query q;

        double upper_price2 = upper_price;
        if (upper_price2 == -1)
            upper_price2 = Double.MAX_VALUE;

        if (sort_by.equals("Recent First")) {
            //Need to add timestamp to sort
            q =list_items.whereGreaterThanOrEqualTo("price", lower_price).whereLessThanOrEqualTo("price", upper_price2);
        } else if (sort_by.equals("Price low -> high")) {
            q = list_items.whereGreaterThanOrEqualTo("price", lower_price).whereLessThanOrEqualTo("price", upper_price2)
                    .orderBy("price", Query.Direction.ASCENDING);
        } else if (sort_by.equals("Price high -> low")) {
            q = list_items.whereGreaterThanOrEqualTo("price", lower_price).whereLessThanOrEqualTo("price", upper_price2)
                    .orderBy("price", Query.Direction.DESCENDING);
        } else {
            q = list_items;
        }
        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    displayData(task, sort_by);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //Takes task, sorting method, and lower/higher price for filtering, then creates dataset to display (for sorting All items)
    private void displayDataForAll(Task<QuerySnapshot> task, String sorting, double lower, double higher) {
        data.clear();
        double upper_price2 = higher;
        if (upper_price2 == -1)
            upper_price2 = Double.MAX_VALUE;
        for (QueryDocumentSnapshot document : task.getResult()) {
            Double price = Double.parseDouble(document.getData().get("price").toString());
            //Toast.makeText(getApplicationContext(), price + " " + lower + " " + upper_price2, Toast.LENGTH_LONG).show();
            if (price >= lower && price <= upper_price2) {
                Product p = new Product(price,
                        document.getData().get("seller").toString(),
                        document.getData().get("title").toString(),
                        document.getData().get("description").toString(),
                        document.getData().get("time"),
                        document.getData().get("category").toString());
                data.add(p);
                //For some reason, document.getData().get("time") is null when the item is first created, but is no longer after going
                //to a different page. idk why lol
//                if (document.getData().get("time") == null) {
//                    Toast.makeText(getApplicationContext(), "this: " + document.getData().get("title").toString(), Toast.LENGTH_LONG).show();
//                    p.setTime("=123,");
//                }
            }
        }
        myDataset = new Product[data.size()];

        if (sorting.equals("Recent First")) {
            Product.setSortMethod("Recent");
        } else if (sorting.equals("Price high -> low")) {
            Product.setSortMethod("Price descending");
        } else {
            Product.setSortMethod("Price Ascending");
        }
        //Toast.makeText(getApplicationContext(), sorting + " " + Product.getSortMethod(), Toast.LENGTH_SHORT).show();
        Collections.sort(data);
        int counter = 0;
        for (Product p: data) {
            myDataset[counter] = p;
            counter++;
        }

            refreshListing2(myDataset);
    }

    //Takes task, sorting method, then creates dataset to display (for sorting when not All items)
    private void displayData(Task<QuerySnapshot> task, String sorting) {
        data.clear();
        for (QueryDocumentSnapshot document : task.getResult()) {
            Product p = new Product(Double.parseDouble(document.getData().get("price").toString()),
                    document.getData().get("seller").toString(),
                    document.getData().get("title").toString(),
                    document.getData().get("description").toString(),
                    document.getData().get("time"),
                    document.getData().get("category").toString());
            data.add(p);
//            String ssss = p.getTime().toString();
//            Toast.makeText(getApplicationContext(), p.getTime().toString() + " " +
//                    Long.parseLong(ssss.substring(ssss.indexOf("=") + 1, ssss.indexOf(","))), Toast.LENGTH_LONG).show();
//            break;
            //Log.d(TAG, document.getId() + " => " + document.getData());
        }
        myDataset = new Product[data.size()];
        if (sorting.equals("Recent First")) {
            Product.setSortMethod("Recent");
            Collections.sort(data);
        }

        int counter = 0;
        for (Product p: data) {
            myDataset[counter] = p;
            counter++;
        }
        refreshListing2(myDataset);
    }

    //For selecting category (furniture, textbook, etc)
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        categories_value = parent.getItemAtPosition(pos).toString();
        refreshListing();
        //Toast.makeText(parent.getContext(), "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //Filter button, go to filter event page
    public void filterButton (View view) {
        Intent intent = new Intent(this, FilterActivity.class);
        Bundle extras = new Bundle();

        extras.putDouble("LOWER_PRICE", lower_price);
        extras.putDouble("UPPER_PRICE", upper_price);
        extras.putString("SORT_BY", sort_by);
        extras.putString("CATEGORIES", categories_value);
        extras.putString("GOOD_SERVICE", good_service_value);
        extras.putString("USERNAME", user_name);

        intent.putExtras(extras);
        startActivity(intent);
    }

    //Go to posting event page
    public void goPostingEvent (String type) {
        Intent intent = new Intent(this, PostingActivity.class);
        Bundle extras = new Bundle();

        extras.putDouble("LOWER_PRICE", lower_price);
        extras.putDouble("UPPER_PRICE", upper_price);
        extras.putString("SORT_BY", sort_by);
        extras.putString("CATEGORIES", categories_value);

        extras.putString("GOOD_SERVICE", type);
        extras.putString("USERNAME", user_name);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void viewProfile (View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        Bundle extras = new Bundle();

        extras.putDouble("LOWER_PRICE", lower_price);
        extras.putDouble("UPPER_PRICE", upper_price);
        extras.putString("SORT_BY", sort_by);
        extras.putString("CATEGORIES", categories_value);

        extras.putString("GOOD_SERVICE", "good");
        extras.putString("USERNAME", user_name);
        intent.putExtras(extras);
        startActivity(intent);
    }



    //Update spinner based on good_service_value (either good/service listing), then refresh page
    public void updateSpinner() {
        if (good_service_value.equals("service")) {
            list_categories = list_categories_service;
        } else {
            list_categories = list_categories_good;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(adapter);
        categories.setOnItemSelectedListener(this);

        categories.setSelection(adapter.getPosition(categories_value));

        refreshListing();
    }
}
