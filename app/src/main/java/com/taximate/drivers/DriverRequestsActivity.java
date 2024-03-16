package com.taximate.drivers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.taximate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.taximate.entities.Ride;
import com.taximate.rides.DriverRideActivity;

import java.util.ArrayList;
import java.util.List;

public class DriverRequestsActivity extends AppCompatActivity {

    SwitchCompat switchAvailability;
    RecyclerView recDriverRequests;
    ValueEventListener eventListener;
    List<Ride> rides;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_requests);

        String driverId = getIntent().getStringExtra("driverId");
        driverAlreadyInRide(driverId);

        initiateProgressBar();

        recDriverRequests = findViewById(R.id.recDriverRequests);
        GridLayoutManager manager = new GridLayoutManager(this, 1);
        recDriverRequests.setLayoutManager(manager);

        rides = new ArrayList<>();

        RequestsAdapter adapter = new RequestsAdapter(this, rides);
        recDriverRequests.setAdapter(adapter);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Rides");

        dialog.show();
        eventListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rides.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Ride ride = item.getValue(Ride.class);
                    if (ride == null)
                        continue;
                    if (ride.getDriverId().equals(driverId) && ride.getStatus().equals("waiting")) {
                        ride.setRideId(item.getKey());
                        rides.add(ride);
                    }

                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DriverRequestsActivity.this,
                        "database error",
                        Toast.LENGTH_SHORT).show();

                Log.e("FirebaseError", error.getDetails());
                dialog.dismiss();
            }
        });
        switchAvailability = findViewById(R.id.switchAvailability);
        switchAvailability.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateDriverAvailability(b, driverId);
            }
        });
    }

    private void driverAlreadyInRide(String driverId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Rides");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot rideSnapshot : dataSnapshot.getChildren()) {
                    Ride ride = rideSnapshot.getValue(Ride.class);
                    if (ride != null && ride.getDriverId().equals(driverId) && ride.getStatus().equals("accepted")) {
                        Toast.makeText(DriverRequestsActivity.this, "You already accepted a ride",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DriverRequestsActivity.this, DriverRideActivity.class);
                        intent.putExtra("rideId", rideSnapshot.getKey());
                        startActivity(intent);
                        DriverRequestsActivity.this.finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DriverRequestsActivity.this,
                        "database error",
                        Toast.LENGTH_SHORT).show();

                Log.e("FirebaseError", error.getDetails());
            }
        });
    }

    private void updateDriverAvailability(boolean available, String driverId) {
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference()
                .child("Drivers")
                .child(driverId);

        driverRef.child("available").setValue(available)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (!available)
                        Toast.makeText(DriverRequestsActivity.this,
                                "you're invisible now", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(DriverRequestsActivity.this,
                            "failed to update availability", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initiateProgressBar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
    }
}