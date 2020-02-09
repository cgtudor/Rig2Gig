package com.gangoffive.rig2gig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    Button loginBtn;
    TextView registerBtn;
    EditText emailAddress, password;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailAddress = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.registerLoginBtn);
        registerBtn = findViewById(R.id.loginRegisterBtn);

        fAuth = FirebaseAuth.getInstance();

        /**
         *  Checking credentials
         */
        loginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String getEmail = emailAddress.getText().toString().trim();
                String getPassword = password.getText().toString().trim();

                /**
                 *  Checking to make sure text has been inputed into text fields
                 */
                if (TextUtils.isEmpty(getEmail))
                {
                    emailAddress.setError("Email Is Required");
                    return;
                }
                if (TextUtils.isEmpty(getPassword))
                {
                    password.setError("Password Is Required");
                    return;
                }

                /**
                 *  Authenticating the user
                 */
                fAuth.signInWithEmailAndPassword(getEmail, getPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this, "User Logged In", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Email Or Password Is Incorrect! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        /**
         *  Redirecting from LoginActivity to RegisterActivity
         */
        registerBtn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }
}
