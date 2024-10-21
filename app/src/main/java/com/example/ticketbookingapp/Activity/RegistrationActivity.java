package com.example.ticketbookingapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketbookingapp.R;
import com.example.ticketbookingapp.utils.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class RegistrationActivity extends AppCompatActivity {

    ImageView backBtn;
    EditText edtUsername, edtMail, edtPass, edtConfirmPass;
    Button btnNext;
    private CheckBox box;
    TextView tvLogin;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseDatabase database;
    FirebaseStorage storage;
    DatabaseReference reference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        database= FirebaseDatabase.getInstance();
        reference= database.getReference("users");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Establishing The Account");
        progressDialog.setCancelable(false);

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        backBtn = findViewById(R.id.imgBackBtn);
        edtUsername = findViewById(R.id.edtuserName);
        edtMail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPassword);
        edtConfirmPass = findViewById(R.id.edtConfirmPassword);
        btnNext = findViewById(R.id.btnNext);
        tvLogin = findViewById(R.id.loginbut);
        box=findViewById(R.id.checkbox);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndProceed();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Exit")
                        .setMessage("Are you sure you want to exit?")
                        .setPositiveButton("Yes", (dialog, which) -> finishAffinity())
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void validateAndProceed() {



        String username = edtUsername.getText().toString().trim();
        String email = edtMail.getText().toString().trim();
        String password = edtPass.getText().toString().trim();
        String confirmPassword = edtConfirmPass.getText().toString().trim();


        if (TextUtils.isEmpty(username)) {
            Toast.makeText(RegistrationActivity.this, "Please enter your username", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(RegistrationActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        } else if (!email.matches(emailPattern)) {
            edtMail.setError("Please enter a valid email address");
            return;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(RegistrationActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.length() < 6) {
            edtPass.setError("Password must be at least 6 characters");
            return;
        } else if (!password.equals(confirmPassword)) {
            edtConfirmPass.setError("Passwords do not match");
            return;
        }  else if (!box.isChecked()) {
            Toast.makeText(RegistrationActivity.this, "You must accept the terms and conditions", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent=  new Intent(RegistrationActivity.this,MainActivity.class);
        startActivity(intent);

        progressDialog.show();
        // Proceed with Firebase authentication or other logic after validation.
        registerUser(username, email, password);
    }

    private void registerUser(String username, String email, String password) {
        // Firebase registration logic goes here.
        // Example:
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        // Save user data to Firebase Realtime Database or Firebase Firestore.
                        // Then navigate to the next screen, e.g., MainActivity.

                        User user= new User(username, email, password);
                        reference.child(username).setValue(user);
                        Toast.makeText(RegistrationActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Handle registration failure.
                        Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
