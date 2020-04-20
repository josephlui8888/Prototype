package com.example.springbreakprototype2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class PostingActivity extends AppCompatActivity {
    private TextView good_service;
    private Spinner categories;
    private TextInputLayout title_text_field, description_text_field, price_text_field;
    private String type, title_value, description_value, category_value, user_name;
    private Double price_value;
    private FirebaseFirestore db;
    private String[] list_categories;
    private String[] list_categories_good = {"Furniture", "Textbooks", "Clothes", "Misc."};
    private String[] list_categories_service = {"Tutoring", "Moving", "Haircuts"};
    private static final int RESULT_LOAD_IMAGES = 1;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        good_service = findViewById(R.id.good_service);
        title_text_field = findViewById(R.id.title);
        description_text_field = findViewById(R.id.description);
        price_text_field = findViewById(R.id.price);
        categories = findViewById(R.id.categories);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        extras = intent.getExtras();
        type = extras.getString("GOOD_SERVICE");
        user_name = extras.getString("USERNAME");
        if (type.equals("good")) {
            good_service.setText("New Posting for Goods");
        } else
            good_service.setText("New Posting for Services");

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
        title_value = title_text_field.getEditText().getText().toString();
        description_value = description_text_field.getEditText().getText().toString();
        String s = price_text_field.getEditText().getText().toString();
        category_value = categories.getSelectedItem().toString();

        if (title_value.equals("") || description_value.equals("") || category_value.equals("") || s.equals("")) {
            Toast.makeText(getApplicationContext(), "Must fill out all fields", Toast.LENGTH_LONG).show();
        } else {
            price_value = Double.parseDouble(s);

            addToDatabase(title_value, description_value, category_value, user_name, price_value);
            Toast.makeText(getApplicationContext(), "Title: " + title_value + " , Description: " +
                            description_value + " , Category: " + category_value + " , Price: " + price_value
                    , Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, HomeActivity.class);

            extras.putString("PREVIOUS", "posting");
            intent.putExtras(extras);

            startActivity(intent);
        }

    }

    public void uploadImages(View view) {
        // check for gallery access permissions
        if(ActivityCompat.checkSelfPermission(PostingActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PostingActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            return;
        }

        // access gallery
        Intent uploadIntent = new Intent(Intent.ACTION_GET_CONTENT);
        uploadIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        uploadIntent.setType("image/*");
        startActivityForResult(uploadIntent, RESULT_LOAD_IMAGES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == RESULT_LOAD_IMAGES && resultCode == RESULT_OK && data != null) {

            List<Uri> imageUris = new ArrayList<>();
            ClipData clipData = data.getClipData();

            // clipData is null if only one picture selected
            if(clipData != null) {
                for(int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    imageUris.add(imageUri);
                }
            } else {
                imageUris.add(data.getData());
            }

            // displaying all selected pictures in the linear layout (with horizontal scroll)
            LinearLayout layout = findViewById(R.id.imagesLinear);
            for (int i = 0; i < imageUris.size(); i++) {
                ImageView imageView = new ImageView(this);
                imageView.setId(i);
                imageView.setPadding(10, 2, 10, 2);
                imageView.setImageURI(imageUris.get(i));
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                layout.addView(imageView);
            }

            // make original "add pictures" view invisible
            ImageView uploadImages = findViewById(R.id.uploadImages);
            uploadImages.setVisibility(View.GONE);
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
