package com.taximate.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taximate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taximate.customers.AvailableDriverListActivity;
import com.taximate.drivers.DriverRequestsActivity;
import com.taximate.entities.Customer;
import com.taximate.entities.Driver;
import com.taximate.entities.Ride;
import com.taximate.rides.DriverRideActivity;

import java.util.EventListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginFragment extends Fragment {

    private View currentView;
    private EditText etLoginEmail;
    private EditText etLoginPassword;
    private TextView tvRegisterClickable;
    private Button btnLogin;
    private FirebaseAuth auth;

    public LoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (currentView == null)
            currentView = inflater.inflate(R.layout.fragment_login, container, false);
        else
            container.removeView(currentView);

        return currentView;
    }

    @Override
    public void onViewCreated(@NonNull View createdView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(createdView, savedInstanceState);
        init();
        tvRegisterClickable.setOnClickListener(view ->
                chooseRegistrationTypeDialog(new String[]{"Customer Account", "Driver Account"})
        );

        btnLogin.setOnClickListener(view -> {
            if (validateData())
                loginUser(getETString(etLoginEmail), getETString(etLoginPassword));
        });
    }

    private void loginUser(String email, String password) {

        auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser().getUid();
                        checkIfDriver(userId, isDriver -> {
                            if (isDriver) {
                                navigateToMainApp("Driver", userId);
                            } else {
                                navigateToMainApp("customer", userId);
                            }
                            Toast.makeText(getActivity(), "login successful", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        Toast.makeText(getActivity(),
                                "login failed check your password or email",
                                Toast.LENGTH_SHORT).show();
                        etLoginEmail.setError("check your email");
                        etLoginPassword.setError("check your password");
                    }
                });
    }

    private void navigateToMainApp(String user, String userId) {
        if (user.equals("customer")) {
            DatabaseReference customerRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Customers")
                    .child(userId);
            customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Intent intent = new Intent(getActivity(), AvailableDriverListActivity.class);
                        intent.putExtra("customerId", userId);
                        startActivity(intent);

                        getActivity().finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (user.equals("Driver")) {
            DatabaseReference driverRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Drivers")
                    .child(userId);

            driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Intent intent = new Intent(getActivity(), DriverRequestsActivity.class);
                        intent.putExtra("driverId", userId);
                        startActivity(intent);

                        getActivity().finish();
                    }
                    else
                        Log.d(this.getClass().toString(), "Faild");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void checkIfDriver(String userId, final OnCheckDriverCallback callback) {
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference()
                .child("Drivers").child(userId);
        driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isDriver = snapshot.exists();
                callback.onCheckDriver(isDriver);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCheckDriver(false);
            }
        });
    }

    interface OnCheckDriverCallback {
        void onCheckDriver(boolean isDriver);
    }

    private boolean validateData() {
        if (getETString(etLoginEmail).isEmpty()) {
            etLoginEmail.setError("You must provide your email");
            etLoginEmail.requestFocus();
            return false;
        }
        if (getETString(etLoginPassword).isEmpty()) {
            etLoginPassword.setError("You must provide your password");
            etLoginPassword.requestFocus();
            return false;
        }

        return true;
    }

    private String getETString(EditText et) {
        return et.getText().toString();
    }

    private void chooseRegistrationTypeDialog(String[] types) {
        AtomicInteger choice = new AtomicInteger(0);
        AlertDialog.Builder chooseTypeDialog = new AlertDialog.Builder(getActivity());
        chooseTypeDialog.setTitle("Account type:")
                .setIcon(R.drawable.ic_account)
                .setSingleChoiceItems(types, 0, (dialogInterface, i) -> choice.set(i))
                .setPositiveButton("Accept", (dialogInterface, i) -> {
                    if (choice.get() == 0)
                        launchCustomerRegistrationFragment();
                    else if (choice.get() == 1)
                        launchDriverRegistrationFragment();
                })
                .create();

        chooseTypeDialog.show();
    }

    private void launchCustomerRegistrationFragment() {
        Fragment registerFragment = new CustomerRegisterFragment();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flLoginFragment, registerFragment)
                .addToBackStack(null)
                .commit();
    }

    private void launchDriverRegistrationFragment() {
        Fragment registerFragment = new DriverRegisterFragment();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.flLoginFragment, registerFragment)
                .addToBackStack(null)
                .commit();
    }

    private void init() {
        tvRegisterClickable = currentView.findViewById(R.id.tvRegisterClickable);
        etLoginEmail = currentView.findViewById(R.id.etLoginEmail);
        etLoginPassword = currentView.findViewById(R.id.etLoginPassword);
        btnLogin = currentView.findViewById(R.id.btnLoginFrag);
    }
}