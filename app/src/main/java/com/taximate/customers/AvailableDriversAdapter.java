package com.taximate.customers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taximate.R;
import com.taximate.entities.Driver;

import java.util.List;

public class AvailableDriversAdapter extends RecyclerView.Adapter<AvailableDriversAdapter.RecyclerViewHolder> {
    private final Context context;
    private final List<Driver> drivers;
    private final String customerId;
    public AvailableDriversAdapter(Context context, List<Driver> drivers, String customerId) {
        this.context = context;
        this.drivers = drivers;
        this.customerId = customerId;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_driver_item, parent, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        if (!drivers.get(position).isAvailable()) {
            return;
        }
        String nameTVText = drivers.get(position).getName();
        String numOfPassengers = "Passengers: " + drivers.get(position).getCar().getNumberOfPassengers();
        holder.recItemDriverName.setText(nameTVText);
        holder.recItemNumPassengers.setText(numOfPassengers);

        holder.recItem.setOnClickListener(view -> {
            Intent intent = new Intent(context, DriverDetailsActivity.class);
            intent.putExtra("name", drivers.get(holder.getAdapterPosition()).getName());
            intent.putExtra("phone", drivers.get(holder.getAdapterPosition()).getPhone());
            intent.putExtra("carModel",
                    drivers.get(holder.getAdapterPosition())
                            .getCar().getModel());
            intent.putExtra("numPassengers",
                    Integer.toString(drivers.get(holder.getAdapterPosition()).getCar().getNumberOfPassengers()));

            context.startActivity(intent);
        });

        holder.btnRequestRide.setOnClickListener(view -> {
            String driverId = drivers.get(position).getId();
            Intent intent = new Intent(context, RequestRideActivity.class);
            intent.putExtra("driverId", driverId);
            intent.putExtra("customerId", customerId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView recItemDriverName;
        TextView recItemNumPassengers;
        CardView recItem;
        Button btnRequestRide;

        RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            recItemDriverName = itemView.findViewById(R.id.tv0);
            recItemNumPassengers = itemView.findViewById(R.id.tv1);
            recItem = itemView.findViewById(R.id.recItem);
            btnRequestRide = itemView.findViewById(R.id.btnRequestRide);
        }
    }
}

