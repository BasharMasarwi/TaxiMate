package com.taximate.customers;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.taximate.R;

public class DriverDetailsActivity extends AppCompatActivity {

    TextView tvDriverDetailsName;
    TextView tvDriverDetailsPhone;
    TextView tvCarModel;
    TextView tvNumPassengers;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);

        init();

        bundle = getIntent().getExtras();
        if (bundle != null)
            setTVsText();
    }

    private void setTVsText() {
        tvDriverDetailsName.setText(bundle.getString("name"));
        tvDriverDetailsPhone.setText(bundle.getString("phone"));
        tvCarModel.setText(bundle.getString("carModel"));
        tvNumPassengers.setText(bundle.getString("numPassengers"));
    }

    private void init() {
        tvDriverDetailsName = findViewById(R.id.tvDriversDetailsName);
        tvDriverDetailsPhone = findViewById(R.id.tvDriversDetailsPhone);
        tvCarModel = findViewById(R.id.tvCarModel);
        tvNumPassengers = findViewById(R.id.tvNumPassengers);
    }
}