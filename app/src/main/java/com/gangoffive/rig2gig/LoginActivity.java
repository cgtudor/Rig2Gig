package com.gangoffive.rig2gig;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUserMetadata;

import org.identityconnectors.common.security.GuardedString;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "TAG";

    FirebaseAuth fAuth;
    SignInButton signInButton;

    Button loginBtn;
    TextView registerBtn, forgotPasswordBtn;
    EditText emailAddress, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();

        emailAddress = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.registerLoginBtn);
        registerBtn = findViewById(R.id.loginRegisterBtn);
        forgotPasswordBtn = findViewById(R.id.forgotPasswordBtn);
        signInButton = findViewById(R.id.sign_in_button);
    }

    public void LoginBtnOnClick(View view) {
        final String getEmail = emailAddress.getText().toString().trim();

        /*Creating an empty char array with the length of the password*/
        char[] passChars = new char[password.getText().length()];

        /*Copying all the characters in the password textbox to the char array*/
        password.getText().getChars(0, password.getText().length(), passChars, 0);
        GuardedString encryptedPassword = new GuardedString(passChars);
        /*Method used to access the encrypted password and get the clear chars*/
        encryptedPassword.access(new GuardedString.Accessor() {
            @Override
            public void access(char[] clearChars) {
                fAuth.signInWithEmailAndPassword(getEmail, new String(clearChars)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "User Logged In", Toast.LENGTH_LONG).show();
                            /**
                             * Checking if this is users first time logging in or existing user
                             */
                            FirebaseUserMetadata metadata = fAuth.getCurrentUser().getMetadata();
                            if (metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp()) {
                                // The user is new, show them a fancy intro screen!
                                startActivity(new Intent(getApplicationContext(), CredentialActivity.class));
                            } else {
                                // This is an existing user, show them a welcome back screen.
                                startActivity(new Intent(getApplicationContext(), NavBarActivity.class));
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Email Or Password Is Incorrect! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        /*Disposes of the encrypted password from the memory*/
        encryptedPassword.dispose();
    }

     public void loginRegisterBtn(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
    }

    public void forgotPasswordBtn(View view) {
        startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
    }
}
