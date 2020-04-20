package com.example.springbreakprototype2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Date;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class ViewPostingActivity extends AppCompatActivity {
    private Bundle extras;

    private FirebaseFirestore db;

    private Double lower_price, upper_price;
    private String sort_by, categories_value, good_service_value, user_name, back, id, good_service_thing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posting);

        TextView priceView, titleView, descriptionView, categoryView, sellerView, postingTimeView;
        Button deletePosting;

        priceView = findViewById(R.id.viewPostingPrice);
        titleView = findViewById(R.id.viewPostingTitle);
        descriptionView = findViewById(R.id.viewPostingDescription);
        categoryView = findViewById(R.id.viewPostingCategory);
        sellerView = findViewById(R.id.viewPostingSeller);
        postingTimeView = findViewById(R.id.viewPostingTime);

        deletePosting = findViewById(R.id.viewPostingDeleteButton);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        extras = intent.getExtras();

        //sets up viewpager for images
        ViewPager viewPager = findViewById(R.id.viewPager);
        ImageAdapter imageAdapter = new ImageAdapter(this,
                extras.getStringArrayList("IMAGE_STRINGS"));
        viewPager.setAdapter(imageAdapter);

        // dot indicators for image swiping
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager, true);

        lower_price = extras.getDouble("LOWER_PRICE");
        upper_price = extras.getDouble("UPPER_PRICE");
        sort_by = extras.getString("SORT_BY");
        categories_value = extras.getString("CATEGORIES");
        good_service_value = extras.getString("GOOD_SERVICE");
        user_name = extras.getString("USERNAME");
        back = extras.getString("BACK");
        if (back.equals("profile")) {
            id = extras.getString("PRODUCT_ID");
            good_service_thing = extras.getString("PRODUCT_GOOD_SERVICE");
        }

        if (!back.equals("profile")) {
            deletePosting.setVisibility(View.INVISIBLE);
        }

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
        priceView.setText(currencyFormatter.format(extras.getDouble("PRODUCT_PRICE")));
        titleView.setText(extras.getString("PRODUCT_TITLE"));
        descriptionView.setText(extras.getString("PRODUCT_DESCRIPTION"));
        categoryView.setText("CATEGORY: " + extras.getString("PRODUCT_CATEGORY"));
        sellerView.setText("SELLER: " + extras.getString("PRODUCT_SELLER"));
        postingTimeView.setText("Posted: " + getDatetime(extras.getString("PRODUCT_TIMESTAMP")));
    }

    // convert Firebase timestamp to human readable time
    private String getDatetime(String timestamp) {
        if(!timestamp.contains("Timestamp")) {
            return timestamp;
        }
        String seconds = timestamp.substring(timestamp.indexOf("=") + 1, timestamp.indexOf(","));
        String nano = "nanoseconds=";
        String nanoseconds = timestamp.substring(
                timestamp.indexOf(nano) + nano.length(), timestamp.indexOf(")"));
        long time = 1000*Long.parseLong(seconds) + Long.parseLong(nanoseconds)/1000000;

        DateFormat dataFormat = SimpleDateFormat.getDateInstance();
        return dataFormat.format(new Date(time));
    }

    public void deletePosting(View view) {
        if (good_service_thing.equals("good")) {
            db.collection("products").document("good").collection(extras.getString("PRODUCT_CATEGORY").toLowerCase())
                    .document("temp").collection("good_price").document(id).delete();

        } else if (good_service_thing.equals("service")) {
            db.collection("products").document("service").collection(extras.getString("PRODUCT_CATEGORY").toLowerCase())
                    .document("temp").collection("service_price").document(id).delete();
        }
        Toast.makeText(getApplicationContext(), good_service_thing + " " + extras.getString("PRODUCT_CATEGORY") + " " + id, Toast.LENGTH_LONG).show();


        Intent intent = new Intent(this, ProfileActivity.class);

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

    public void contactSeller(View view) {
        Intent intent;
        if (back.equals("profile")) {
            intent = new Intent(this, ProfileActivity.class);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }

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
}
