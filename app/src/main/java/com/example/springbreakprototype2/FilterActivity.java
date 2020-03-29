package com.example.springbreakprototype2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class FilterActivity extends AppCompatActivity {
    private EditText lower_price;
    private EditText upper_price;
    private String categories, good_service_value;

    private RadioGroup sort_by;

    private Double lower_price_value, upper_price_value;
    private String sort_by_value, user_name;
    private String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        lower_price = (EditText) findViewById(R.id.editTextLowerPrice);
        upper_price = (EditText) findViewById(R.id.editTextUpperPrice);
        sort_by = (RadioGroup) findViewById(R.id.sortBy);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        lower_price_value = extras.getDouble("LOWER_PRICE");
        upper_price_value = extras.getDouble("UPPER_PRICE");
        categories = extras.getString("CATEGORIES");
        sort_by_value = extras.getString("SORT_BY");
        good_service_value = extras.getString("GOOD_SERVICE");
        user_name = extras.getString("USERNAME");
        if (lower_price_value != -1) {
            lower_price.setText(lower_price_value.toString());
        }
        if (upper_price_value != -1) {
            upper_price.setText(upper_price_value.toString());
        }
        switch(sort_by_value) {
            case "Recent First": sort_by.check(R.id.radioButtonRecentFirst); break;
            case "Price low -> high": sort_by.check(R.id.radioButtonPriceLowHigh); break;
            case "Price high -> low": sort_by.check(R.id.radioButtonPriceHighLow); break;
            default: sort_by.check(R.id.radioButtonRecentFirst);
        }
    }

    public void applyFilter(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        Bundle extras = new Bundle();

        extras.putString("PREVIOUS", "FILTER");
        String s1 = lower_price.getText().toString(), s2 = upper_price.getText().toString();
        if (s1.equals("")) {
            s1 = "-1";
        }
        if (s2.equals("")) {
            s2 = "-1";
        }

        extras.putDouble("LOWER_PRICE", Double.parseDouble(s1));
        extras.putDouble("UPPER_PRICE", Double.parseDouble(s2));

        int selectedRadioButtonId = sort_by.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            s = ((RadioButton) findViewById(selectedRadioButtonId)).getText().toString();
        } else {
            s = "None";
        }
        extras.putString("CATEGORIES", categories);
        extras.putString("SORT_BY", s);
        extras.putString("GOOD_SERVICE", good_service_value);
        extras.putString("USERNAME", user_name);

        intent.putExtras(extras);
        startActivity (intent);
    }
}
