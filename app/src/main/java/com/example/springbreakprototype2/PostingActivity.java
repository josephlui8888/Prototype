package com.example.springbreakprototype2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


public class PostingActivity extends AppCompatActivity {
    private TextView good_service;
    private EditText title, description, price;
    private Spinner categories;
    private String type, title_value, description_value, category_value;
    private Double price_value;
    private FirebaseFirestore db;
    private String[] list_categories;
    private String[] list_categories_good = {"Furniture", "Textbooks", "Clothes", "Misc."};
    private String[] list_categories_service = {"Tutoring", "Moving", "Haircuts"};
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        good_service = findViewById(R.id.good_service);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);
        categories = findViewById(R.id.categories);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        extras = intent.getExtras();
        type = extras.getString("GOOD_SERVICE");
        good_service.setText(type);


        if (type.equals("service")) {
            list_categories = list_categories_service;
        } else {
            list_categories = list_categories_good;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list_categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(adapter);

    }

    //Publish button, go back to home activity page
    public void publish(View view) {
        title_value = title.getText().toString();
        description_value = description.getText().toString();
        category_value = categories.getSelectedItem().toString();
        String s = price.getText().toString();
        if (title_value.equals("") || description_value.equals("") || category_value.equals("") || s.equals("")) {
            Toast.makeText(getApplicationContext(), "Must fill out all fields", Toast.LENGTH_LONG).show();
        } else {
            price_value = Double.parseDouble(s);

            //NEED TO CHANGE JOSEPH TO NAME OF USER
            addToDatabase(title_value, description_value, category_value, "Joseph", price_value);
            Toast.makeText(getApplicationContext(), "Title: " + title_value + " , Description: " +
                            description_value + " , Category: " + category_value + " , Price: " + price_value
                    , Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, HomeActivity.class);

            extras.putString("PREVIOUS", "posting");
            intent.putExtras(extras);

            startActivity(intent);
        }

    }


    private void addToDatabase (String title, String description, String category, String seller, Double price) {

        Product p = new Product(price, seller, title, description, FieldValue.serverTimestamp(), category);
        if (type.toLowerCase().equals("good")) {
            db.collection("products").document(type.toLowerCase()).collection(category.toLowerCase())
                    .document("temp").collection("good_price").add(p);
        } else {
            db.collection("products").document(type.toLowerCase()).collection(category.toLowerCase())
                    .document("temp").collection("service_price").add(p);
        }

//        DocumentReference dr = db.collection("products").document(type.toLowerCase()).collection(category.toLowerCase()).document();
//        dr.set(p);
//        if (type.toLowerCase().equals("good")) {
//            dr.collection("good_price").add(price);
//        } else {
//            dr.collection("service_price").add(price);
//        }
    }


}
