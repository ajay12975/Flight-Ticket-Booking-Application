package com.example.ticketbookingapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketbookingapp.R;
import com.example.ticketbookingapp.utils.SharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText edtMail, edtPass;
    TextView tvSignup;
    Button btnLogin;
    ImageView imgBack;
    FirebaseAuth auth;
    String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
    ProgressDialog progressDialog;

    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        sharedPreference = new SharedPreference(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        edtMail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.edtLogin);
        tvSignup = findViewById(R.id.btnSignup);
        imgBack = findViewById(R.id.imgBackBtn);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,IntroActivity.class);
                startActivity(intent);
            }
        });

        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
            finish();
        });

        btnLogin.setOnClickListener(v -> {
            String Email = edtMail.getText().toString().trim();
            String Password = edtPass.getText().toString().trim();

            if (!validateEmail(Email) || !validatePassword(Password)) {
                return;
            }

            progressDialog.show();
            auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(task -> {
                progressDialog.dismiss(); // Dismiss when the process is complete
                if (task.isSuccessful()) {
                    sharedPreference.setLogin(true);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Log detailed error messages
                    String errorMsg = task.getException() != null ? task.getException().getMessage() : "Authentication failed!";
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    // Handle Recaptcha or malformed auth error
                    if (errorMsg.contains("Recaptcha") || errorMsg.contains("auth/invalid-credential")) {
                        Toast.makeText(LoginActivity.this, "Please try again. ReCAPTCHA verification might be required.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        });
    }

    public void checkuser() {
        String userEmail = edtMail.getText().toString().trim();
        String userPassword = edtPass.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("email").equalTo(userEmail);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next(); // Get first user
                    String passwordFromDB = userSnapshot.child("password").getValue(String.class);

                    if (Objects.equals(passwordFromDB, userPassword)) {
                        edtMail.setError(null);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        edtPass.setError("Invalid Credentials");
                        edtPass.requestFocus();
                    }
                } else {
                    edtMail.setError("User does not exist");
                    edtMail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            edtMail.setError("Email cannot be empty");
            return false;
        } else if (!email.matches(emailPattern)) {
            edtMail.setError("Enter a valid email address");
            return false;
        } else {
            edtMail.setError(null);
            return true;
        }
    }

    public Boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            edtPass.setError("Password cannot be empty");
            return false;
        } else if (password.length() < 6) {
            edtPass.setError("Password should be more than six characters");
            return false;
        } else {
            edtPass.setError(null);
            return true;
        }
    }
}
