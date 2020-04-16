package com.gangoffive.rig2gig.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gangoffive.rig2gig.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * LoginActivity loads when the activity is called.
 */
public class ForgotPasswordActivity extends AppCompatActivity {
    FirebaseAuth fAuth;

    EditText emailAddress;
    Button resetPasswordBtn;

    /**
     * When the onCreate is called previous states from the activity can be restored.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        fAuth = FirebaseAuth.getInstance();

        emailAddress = findViewById(R.id.emailReset);
        resetPasswordBtn = findViewById(R.id.resetPasswordBtn);
    }

    /**
     * This method gets the email inputted by the user and then sends them a reset password link.
     * @param view
     */
    public void resetPasswordOnClick(View view) {
        String email = emailAddress.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            emailAddress.setError("Please enter an email address");
            return;
        }

        fAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this, "Check Your Email.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }else {
                    Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
