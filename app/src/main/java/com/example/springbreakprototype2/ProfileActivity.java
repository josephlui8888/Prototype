package com.example.springbreakprototype2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private Double lower_price, upper_price;
    private Bundle extras;
    private String sort_by, categories_value, good_service_value, user_name;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        extras = intent.getExtras();
        lower_price = extras.getDouble("LOWER_PRICE");
        upper_price = extras.getDouble("UPPER_PRICE");
        sort_by = extras.getString("SORT_BY");
        categories_value = extras.getString("CATEGORIES");
        good_service_value = extras.getString("GOOD_SERVICE");
        user_name = extras.getString("USERNAME");

    }

    private void displayData(Task<QuerySnapshot> task) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view2);
        ArrayList<Product> data = new ArrayList<Product>();
        for (QueryDocumentSnapshot document : task.getResult()) {
            Product p = new Product(Double.parseDouble(document.getData().get("price").toString()),
                    document.getData().get("seller").toString(),
                    document.getData().get("title").toString(),
                    document.getData().get("description").toString(),
                    document.getData().get("time"),
                    document.getData().get("category").toString());
            data.add(p);
            p.setId(document.getId());
            p.setGoodService(document.getReference().getParent().getParent().getParent().getParent().getId());
        }
        Product[] data2 = new Product[data.size()];
        for (int i = 0; i < data.size(); i++) {
            data2[i] = data.get(i);
        }

        ProductAdapter adapter = new ProductAdapter(data2, new ProductAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Product p) {
                viewPosting(p);
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void logout(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void viewPosting(Product p) {
        Intent intent = new Intent(this, ViewPostingActivity.class);
        Bundle extras = new Bundle();

        extras.putDouble("LOWER_PRICE", lower_price);
        extras.putDouble("UPPER_PRICE", upper_price);
        extras.putString("SORT_BY", sort_by);
        extras.putString("CATEGORIES", categories_value);
        extras.putString("GOOD_SERVICE", good_service_value);
        extras.putString("USERNAME", user_name);
        extras.putString("BACK", "profile");
        extras.putDouble("PRODUCT_PRICE", p.getPrice());
        extras.putString("PRODUCT_SELLER", p.getSeller());
        extras.putString("PRODUCT_TITLE", p.getTitle());
        extras.putString("PRODUCT_DESCRIPTION", p.getDescription());
        extras.putString("PRODUCT_TIMESTAMP", p.getTime().toString());
        extras.putString("PRODUCT_CATEGORY", p.getCategory());
        extras.putString("PRODUCT_ID", p.getId());
        extras.putString("PRODUCT_GOOD_SERVICE", p.getGoodService());

        intent.putExtras(extras);
        startActivity(intent);
    }

    public void viewLikes(View view) {

    }

    public void viewSelling(View view) {
        db.collectionGroup("good_price").whereEqualTo("seller", user_name).get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    displayData(task);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void viewOffering(View view) {
        db.collectionGroup("service_price").whereEqualTo("seller", user_name).get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    displayData(task);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void viewHistory(View view) {

    }

    public void setGoodButton(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        Bundle extras = new Bundle();
        extras.putDouble("LOWER_PRICE", lower_price);
        extras.putDouble("UPPER_PRICE", upper_price);
        extras.putString("SORT_BY", sort_by);
        //idk what to go back to, maybe previous one?
        extras.putString("CATEGORIES", "All items");
        extras.putString("GOOD_SERVICE", "good");
        extras.putString("USERNAME", user_name);

        intent.putExtras(extras);
        startActivity(intent);
    }

    public void setServiceButton(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        Bundle extras = new Bundle();
        extras.putDouble("LOWER_PRICE", lower_price);
        extras.putDouble("UPPER_PRICE", upper_price);
        extras.putString("SORT_BY", sort_by);
        //idk what to go back to, maybe previous one?
        extras.putString("CATEGORIES", "All items");
        extras.putString("GOOD_SERVICE", "service");
        extras.putString("USERNAME", user_name);

        intent.putExtras(extras);
        startActivity(intent);
    }

    public void addPosting(View view) {

    }

    //Need to add this function
    public void chat (View view) {

    }
}
