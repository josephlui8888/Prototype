package com.example.springbreakprototype2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewPostingActivity extends AppCompatActivity {
    private Bundle extras;
    private ImageView imageView;
    private TextView priceView, titleView, descriptionView, categoryView, sellerView;
    private Button contact;

    private Double lower_price, upper_price;
    private String sort_by, categories_value, good_service_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posting);

        //Need imageView/contact?
        priceView = findViewById(R.id.viewPostingPrice);
        titleView = findViewById(R.id.viewPostingTitle);
        descriptionView = findViewById(R.id.viewPostingDescription);
        categoryView = findViewById(R.id.viewPostingCategory);
        sellerView = findViewById(R.id.viewPostingSeller);

        Intent intent = getIntent();
        extras = intent.getExtras();
        lower_price = extras.getDouble("LOWER_PRICE");
        upper_price = extras.getDouble("UPPER_PRICE");
        sort_by = extras.getString("SORT_BY");
        categories_value = extras.getString("CATEGORIES");
        good_service_value = extras.getString("GOOD_SERVICE");

        //need to change to currency
        priceView.setText("$" + (extras.getDouble("PRODUCT_PRICE")));
        titleView.setText(extras.getString("PRODUCT_TITLE"));
        descriptionView.setText(extras.getString("PRODUCT_DESCRIPTION"));
        categoryView.setText("CATEGORY: " + extras.getString("PRODUCT_CATEGORY"));
        sellerView.setText("SELLER: " + extras.getString("PRODUCT_SELLER"));
        //timestamp? PRODUCT_TIMESTAMP

    }

    public void contactSeller(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        Bundle extras = new Bundle();

        extras.putDouble("LOWER_PRICE", lower_price);
        extras.putDouble("UPPER_PRICE", upper_price);
        extras.putString("SORT_BY", sort_by);
        extras.putString("CATEGORIES", categories_value);
        extras.putString("GOOD_SERVICE", good_service_value);


        intent.putExtras(extras);
        startActivity(intent);
    }
}
