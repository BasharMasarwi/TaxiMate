package com.taximate.customers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taximate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taximate.entities.Customer;
import com.taximate.entities.Driver;
import com.taximate.entities.Ride;

public class RequestRideActivity extends AppCompatActivity {

    private EditText etRequestLocation;
    private EditText etRequestDestination;
    private Button btnFinishRequest;
    private Button btnCancelRequest;
    private String driverId;
    private String customerId;
    private Driver driver;
    private Customer customer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_ride);

        init();

        btnCancelRequest.setOnClickListener(view -> finish());

        btnFinishRequest.setOnClickListener(view -> {
            if (!validateData())
                return;

            String location = etRequestLocation.getText().toString();
            String destination = etRequestDestination.getText().toString();
            String status = "waiting";
            Ride ride = new Ride(driverId,
                    customerId,
                    driver.getName(),
                    customer.getName(),
                    location,
                    destination,
                    status,
                    0.0);

            storeInDatabase(ride);
        });
    }

    private void storeInDatabase(Ride ride) {
        if (!driver.isAvailable()) {
            Toast.makeText(this, "driver is currently unavailable", Toast.LENGTH_SHORT).show();
            finish();
        }
        AlertDialog dialog = initiateProgressBar();
        dialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Rides");
        String rideId = reference.push().getKey();
        assert rideId != null;
        reference.child(rideId).setValue(ride).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RequestRideActivity.this,
                            "Ride requested successfully",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                else {
                    Toast.makeText(RequestRideActivity.this,
                            "error",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }

    private boolean validateData() {
        if (etRequestLocation.getText().toString().isEmpty()) {
            etRequestLocation.setError("Enter your current location, please.");
            return false;
        }
        if (etRequestDestination.getText().toString().isEmpty()) {
            etRequestDestination.setError("Enter where do you want to go, please");
            return false;
        }

        return true;
    }

    private void getCustomer() {
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference()
                .child("Customers")
                .child(customerId);
        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    customer = snapshot.getValue(Customer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequestRideActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDriver() {
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference()
                .child("Drivers")
                .child(driverId);
        driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    driver = snapshot.getValue(Driver.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RequestRideActivity.this,
                        "error",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private AlertDialog initiateProgressBar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);

        return builder.create();
    }

    private void init() {
        etRequestDestination = findViewById(R.id.etRequestDestination);
        etRequestLocation = findViewById(R.id.etRequestLocation);
        btnCancelRequest = findViewById(R.id.btnCancelRequest);
        btnFinishRequest = findViewById(R.id.btnFinishRequest);
        Intent intent = getIntent();
        driverId = intent.getStringExtra("driverId");
        customerId = intent.getStringExtra("customerId");
        getDriver();
        getCustomer();
    }
}