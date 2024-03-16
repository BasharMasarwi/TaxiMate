package com.taximate.drivers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taximate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.taximate.entities.Ride;
import com.taximate.rides.DriverRideActivity;

import java.util.List;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestsHolders> {

    private final Context context;
    private final List<Ride> rides;

    public RequestsAdapter(Context context, List<Ride> rides) {
        this.context = context;
        this.rides = rides;
    }

    @NonNull
    @Override
    public RequestsHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_request_item, parent, false);

        return new RequestsHolders(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestsHolders holder, int position) {
        if (rides.get(position).getStatus().equals("canceled"))
            return;

        String customerName = rides.get(position).getCustomerName();
        String location = rides.get(position).getCustomerLocation();
        String destination = rides.get(position).getCustomerDestination();

        holder.tvListItemCustomerName.setText(customerName);
        holder.tvListItemCustomerLocation.setText(location);
        holder.tvListItemCustomerDestination.setText(destination);

        String rideId = rides.get(position).getRideId();
        holder.btnAcceptRequest.setOnClickListener(view -> {
            if (holder.etCost.getText().toString().isEmpty())
                holder.etCost.setError("how much do you want for this ride?");
            else {
                changeStatus(rideId, "accepted");
                double cost = Double.parseDouble(holder.etCost.getText().toString());
                setCost(rideId, cost);
                setDriverUnavailable(rides.get(position).getDriverId());
                Intent intent = new Intent(context, DriverRideActivity.class);
                intent.putExtra("rideId", rideId);
                context.startActivity(intent);
            }
        });

        holder.btnRejectRequest.setOnClickListener(view -> changeStatus(rideId, "rejected"));
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    private void setCost(String rideId, double cost) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Rides")
                .child(rideId);
        reference.child("cost").setValue(cost);
    }

    private void changeStatus(String rideId, String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Rides")
                .child(rideId);
        reference.child("status").setValue(status);
    }

    private void setDriverUnavailable(String driverId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Drivers")
                .child(driverId);
        reference.child("available").setValue(false);
    }

    static class RequestsHolders extends RecyclerView.ViewHolder {
        TextView tvListItemCustomerName;
        TextView tvListItemCustomerLocation;
        TextView tvListItemCustomerDestination;
        EditText etCost;
        Button btnAcceptRequest;
        Button btnRejectRequest;

        public RequestsHolders(@NonNull View itemView) {
            super(itemView);

            tvListItemCustomerName = itemView.findViewById(R.id.tvListItemCustomerName);
            tvListItemCustomerLocation = itemView.findViewById(R.id.tvListItemCustomerLocation);
            tvListItemCustomerDestination = itemView.findViewById(R.id.tvListItemCustomerDestination);
            etCost = itemView.findViewById(R.id.etCost);
            btnAcceptRequest = itemView.findViewById(R.id.btnAcceptRequest);

            btnRejectRequest = itemView.findViewById(R.id.btnRejectRequest);
        }
    }
}
