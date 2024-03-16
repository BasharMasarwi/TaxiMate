package com.taximate.customers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.taximate.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taximate.entities.Driver;

import java.util.ArrayList;
import java.util.List;

public class AvailableDriverListActivity extends AppCompatActivity {

    RecyclerView recViewDriverList;
    DatabaseReference reference;
    ValueEventListener eventListener;
    List<Driver> drivers;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_driver_list);
        initiateProgressBar();
        recViewDriverList = findViewById(R.id.recViewDriverList);
        GridLayoutManager manager = new GridLayoutManager(this, 1);
        recViewDriverList.setLayoutManager(manager);

        drivers = new ArrayList<>();

        String customerId = getIntent().getStringExtra("customerId");

        AvailableDriversAdapter adapter = new AvailableDriversAdapter(this, drivers, customerId);
        recViewDriverList.setAdapter(adapter);
        reference = FirebaseDatabase.getInstance().getReference().child("Drivers");
        dialog.show();

        eventListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                drivers.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Driver driver = item.getValue(Driver.class);
                    if (driver != null && driver.isAvailable()) {
                        driver.setId(item.getKey());
                        drivers.add(driver);
                    }
                }

                adapter .notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AvailableDriverListActivity.this,
                    "database error",
                    Toast.LENGTH_SHORT).show();

                Log.e("FirebaseError", error.getDetails());
                dialog.dismiss();
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