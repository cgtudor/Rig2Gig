package com.gangoffive.rig2gig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText fullName, emailAddress, confirmEmailAddress, password, confirmPassword, phoneNumber;
    TextView loginBtn;
    Button btnRegister;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullName = findViewById(R.id.registerFullName);
        emailAddress = findViewById(R.id.registerEmail);
        confirmEmailAddress = findViewById(R.id.registerConfirmEmail);
        password = findViewById(R.id.registerPassword);
        confirmPassword = findViewById(R.id.registerConfirmPassword);
        phoneNumber = findViewById(R.id.registerPhoneNumber);
        loginBtn = findViewById(R.id.registerLoginBtn);

        btnRegister = findViewById(R.id.registerBtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        /**
         *  Registering inputed credentials
         */
        btnRegister.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                final String getEmail = emailAddress.getText().toString().trim();
                final String getConfirmEmail = confirmEmailAddress.getText().toString().trim();
                final String getPassword = password.getText().toString().trim();
                String getConfirmPassword = confirmPassword.getText().toString().trim();
                final String getFullName = fullName.getText().toString();
                final String getPhoneNumber = phoneNumber.getText().toString();

//                if (TextUtils.isEmpty(getEmail))
//                {
//                    emailAddress.setError("Email Address Is Required");
//                }
//                if (TextUtils.isEmpty(getConfirmEmail))
//                {
//                    confirmEmailAddress.setError("Email Address Is Required");
//                }
//                if (!getConfirmEmail.matches(getEmail))
//                {
//                    confirmEmailAddress.setError("Email Doesn't Match");
//                }
//                if (TextUtils.isEmpty(getPassword))
//                {
//                    password.setError("Password Is Required");
//                }
//                if (TextUtils.isEmpty(getConfirmPassword))
//                {
//                    confirmPassword.setError("Password Is Required");
//                }
//                if (getPassword.length() < 6)
//                {
//                    password.setError("Password Must Contain More Than 6 Characters");
//                }
//                if (!getConfirmPassword.matches(getPassword))
//                {
//                    confirmPassword.setError("Password Doesn't Match");
//                }
            }
        });

        /**
         *  Redirecting from LoginActivity to RegisterActivity
         */
        loginBtn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }
}
