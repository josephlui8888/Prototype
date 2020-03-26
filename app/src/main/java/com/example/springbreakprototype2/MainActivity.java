package com.example.springbreakprototype2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startPrototype (View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        Bundle extras = new Bundle();
        extras.putString("PREVIOUS", "MAIN");
        extras.putDouble("LOWER_PRICE", -1);
        extras.putDouble("UPPER_PRICE", -1);
        extras.putString("CATEGORIES", "All items");
        extras.putString("SORT_BY", "Recent First");
        extras.putString("GOOD_SERVICE", "good");
        intent.putExtras(extras);
        startActivity(intent);
    }
}
