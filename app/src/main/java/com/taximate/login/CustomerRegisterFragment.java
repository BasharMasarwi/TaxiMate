package com.taximate.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taximate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.taximate.entities.Customer;

import java.util.HashMap;
import java.util.Map;

public class CustomerRegisterFragment extends Fragment {

    private View currentView;

    private EditText etCustomerRegName;
    private EditText etRegEmail;
    private EditText etRegPhone;
    private EditText etRegPassword;
    private EditText etRegConfPassword;
    private Button btnRegister;
    private TextView tvLoginClickable;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseAuth auth;

    private Customer customer;

    public CustomerRegisterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (currentView == null)
            currentView = inflater.inflate(R.layout.fragment_customer_register, container, false);
        else
            container.removeView(currentView);

        return currentView;
    }

    @Override
    public void onViewCreated(@NonNull View createdView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(createdView, savedInstanceState);

        init();

        btnRegister.setOnClickListener(view -> {
            customer = createCustomer();
            if (customer != null)
                registerUser();
        });

        tvLoginClickable.setOnClickListener(view -> {
            openLoginFragment();
        });
    }

    private void registerUser() {
        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(customer.getEmail(), customer.getPassword())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("registerUser", "successful");
                    storeInDatabase();
                }
                else {
                    Toast.makeText(getActivity(),
                            "This email is connected to an account",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void storeInDatabase() {
        AlertDialog progressBar = initiateProgressBar();
        progressBar.show();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        assert firebaseUser != null;
        String userId = firebaseUser.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Customers");

        reference.child(userId).setValue(customer).addOnCompleteListener(
                new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(),
                            "Registration successful",
                            Toast.LENGTH_SHORT).show();
                    progressBar.dismiss();
                    openLoginFragment();
                }
                else {
                    Toast.makeText(getActivity(), "Registration failed", Toast.LENGTH_SHORT).show();
                    progressBar.dismiss();
                    Log.e("storeInDatabase", task.getException().toString());
                }
            }
        });
    }

    private Customer createCustomer() {
        if (validateData())
            return new Customer(
                    getETString(etCustomerRegName),
                    getETString(etRegEmail),
                    getETString(etRegPassword),
                    getETString(etRegPhone)
            );

        return null;
    }

    private void openLoginFragment() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        if (getActivity() != null)
            getActivity().overridePendingTransition(0, 0);
    }

    private boolean validateData() {
        if (getETString(etCustomerRegName).isEmpty()) {
            etCustomerRegName.setError("Please fill this field");
            etCustomerRegName.requestFocus();
            return false;
        }
        if (getETString(etRegEmail).isEmpty()) {
            etRegEmail.setError("Please fill this field");
            etRegEmail.requestFocus();
            return false;
        }
        if (getETString(etRegPhone).isEmpty()) {
            etRegPhone.setError("Please fill this field");
            etRegPhone.requestFocus();
            return false;
        }
        if (getETString(etRegPassword).isEmpty()) {
            etRegPassword.setError("Please fill this field");
            etRegPassword.requestFocus();
            return false;
        }
        if (getETString(etRegConfPassword).isEmpty()) {
            etRegConfPassword.setError("Please fill this field");
            etRegConfPassword.requestFocus();
            return false;
        }
        if (!getETString(etRegPassword).equals(getETString(etRegConfPassword))) {
            etRegPassword.setError("!");
            etRegConfPassword.setError("Passwords not matching");
            etRegConfPassword.requestFocus();
            return false;
        }

        return true;
    }

    private AlertDialog initiateProgressBar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);

        return builder.create();
    }

    private String getETString(EditText et) {
        return et.getText().toString();
    }

    private void init() {
        etCustomerRegName = currentView.findViewById(R.id.etCustomerRegName);
        etRegEmail = currentView.findViewById(R.id.etRegEmail);
        etRegPhone = currentView.findViewById(R.id.etRegPhone);
        etRegPassword = currentView.findViewById(R.id.etRegPassword);
        etRegConfPassword = currentView.findViewById(R.id.etRegConfPassword);
        tvLoginClickable = currentView.findViewById(R.id.tvLoginClickable);
        btnRegister = currentView.findViewById(R.id.btnRegister);
    }
}