package com.taximate.rides;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.taximate.R;
import com.google.android.play.core.integrity.IntegrityTokenRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taximate.drivers.DriverRequestsActivity;
import com.taximate.entities.Ride;

public class DriverRideActivity extends AppCompatActivity {

    Button btnFinishRide;
    TextView tvRideCustomerLocation;
    TextView tvRideCustomerDestination;
    TextView tvRideCustomerName;
    private Ride ride;
    private String rideId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_ride);

        btnFinishRide = findViewById(R.id.btnFinishRide);
        tvRideCustomerName = findViewById(R.id.tvRideCustomerName);
        tvRideCustomerLocation = findViewById(R.id.tvRideCustomerLocation);
        tvRideCustomerDestination = findViewById(R.id.tvRideCustomerDestination);
        rideId = getIntent().getStringExtra("rideId");
        setRide();

        btnFinishRide.setOnClickListener(view -> {
            setRideFinished();

            Intent intent = new Intent(this, DriverRequestsActivity.class);
            intent.putExtra("driverId", ride.getDriverId());
            startActivity(intent);
            finish();
        });
    }

    private void setRideFinished() {
        DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference()
                .child("Rides")
                .child(rideId);

        rideRef.child("status").setValue("finished");
    }

    private void setRide() {
        DatabaseReference rideRef = FirebaseDatabase.getInstance().getReference()
                .child("Rides")
                .child(rideId);

        rideRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ride = snapshot.getValue(Ride.class);
                    if (ride != null) {
                        setData();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(DriverRideActivity.class.toString(), error.getDetails());
            }
        });
    }

    private void setData() {
        tvRideCustomerName.setText(ride.getCustomerName());
        tvRideCustomerLocation.setText(ride.getCustomerLocation());
        tvRideCustomerDestination.setText(ride.getCustomerDestination());
    }
}