package com.taximate.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taximate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.taximate.entities.Car;
import com.taximate.entities.Driver;

public class DriverRegisterFragment extends Fragment {

    private ImageView ivUploadImage;

    private EditText etDriverRegName;
    private EditText etDriverRegEmail;
    private EditText etDriverRegPhone;
    private EditText etDriverRegPassword;
    private EditText etDriverRegConfPassword;

    private EditText etCarModel;
    private EditText etPassenger;

    private Button btnDriverRegister;

    private TextView tvDriverLoginClickable;
    
    private FirebaseAuth auth;

    private Uri uri;
    private String imageUrl;

    private Driver driver;

    private View currentView;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    public DriverRegisterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        Intent data = result.getData();
//                    }
//                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (currentView == null)
            currentView = inflater.inflate(R.layout.fragment_driver_register, container, false);
        else
            container.removeView(currentView);

        return currentView;
    }

    @Override
    public void onViewCreated(@NonNull View currentView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(currentView, savedInstanceState);

        init();

        getUploadImage();

        btnDriverRegister.setOnClickListener(view -> {
            storeImage();
            driver = createDriver();
            if (driver != null)
                registerDriver();
        });

        tvDriverLoginClickable.setOnClickListener(view -> {
            openLoginFragment();
        });
    }

    private void registerDriver() {
        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(driver.getEmail(), driver.getPassword())
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
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        String driverId = user.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Drivers");
        reference.child(driverId).setValue(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(),
                            "Registration successful",
                            Toast.LENGTH_SHORT).show();
                    openLoginFragment();
                }
                else {
                    Toast.makeText(getActivity(), "Registration failed", Toast.LENGTH_SHORT).show();
                    Log.e("storeInDatabase", task.getException().toString());
                }
            }
        });
    }

    private Driver createDriver() {
        if (validateDriverData() && validateCarData()) {
            Car car = new Car(getETString(etCarModel),
                    Integer.parseInt(getETString(etPassenger)));
            return driver = new Driver(getETString(etDriverRegName),
                    getETString(etDriverRegEmail),
                    getETString(etDriverRegPassword),
                    getETString(etDriverRegPhone),
                    imageUrl,
                    car,
                    false);
        }
        return null;
    }

    private void getUploadImage() {
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        uri = data.getData();
                        ivUploadImage.setImageURI(uri);
                    }
                    else {
                        Toast.makeText(getActivity(),
                                "No Image Selected",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        pickImage(activityResultLauncher);
    }

    private void pickImage(ActivityResultLauncher<Intent> activityResultLauncher) {
        ivUploadImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });
    }

    private void storeImage() {
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference()
                .child("License Images")
                .child(uri.getLastPathSegment());

        AlertDialog progressBar = initiateProgressBar();
        progressBar.show();

        storageReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        Uri url = uriTask.getResult();
                        imageUrl = url.toString();
                        progressBar.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("storeImage", e.toString());
                        progressBar.dismiss();
                    }
                });
    }

    private AlertDialog initiateProgressBar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);

        return builder.create();
    }

    private boolean validateCarData() {
        return isEditTextEmpty(etCarModel) &&
                isEditTextEmpty(etPassenger);
    }

    private boolean validateDriverData() {
        if (uri == null) {
            Toast.makeText(getActivity(),
                    "You must provide your driver's license picture",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return isEditTextEmpty(etDriverRegName) &&
                isEditTextEmpty(etDriverRegEmail) &&
                isEditTextEmpty(etDriverRegPhone) &&
                isEditTextEmpty(etDriverRegPassword) &&
                isEditTextEmpty(etDriverRegConfPassword) &&
                isMatchingPasswords();
    }

    private boolean isMatchingPasswords() {
        if (!getETString(etDriverRegPassword).equals(getETString(etDriverRegConfPassword))) {
            etDriverRegPassword.setError("!");
            etDriverRegConfPassword.setError("Passwords not matching");
            etDriverRegConfPassword.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isEditTextEmpty(EditText et) {
        if (et.getText().toString().isEmpty()) {
            et.setError("Please fill this field");
            et.requestFocus();
            return false;
        }

        return true;
    }

    private void openLoginFragment() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        if (getActivity() != null)
            getActivity().overridePendingTransition(0, 0);
    }

    private String getETString(EditText et) {
        return et.getText().toString();
    }

    private void init() {
        ivUploadImage = currentView.findViewById(R.id.ivUploadImage);
        etDriverRegName = currentView.findViewById(R.id.etDriverRegName);
        etDriverRegEmail = currentView.findViewById(R.id.etDriverRegEmail);
        etDriverRegPhone = currentView.findViewById(R.id.etDriverRegPhone);
        etDriverRegPassword = currentView.findViewById(R.id.etDriverRegPassword);
        etDriverRegConfPassword = currentView.findViewById(R.id.etDriverRegConfPassword);
        etCarModel = currentView.findViewById(R.id.etCarModel);
        etPassenger = currentView.findViewById(R.id.etPassenger);
        btnDriverRegister = currentView.findViewById(R.id.btnDriverRegister);
        tvDriverLoginClickable = currentView.findViewById(R.id.tvDriverLoginClickable);
        auth = FirebaseAuth.getInstance();
    }
}
