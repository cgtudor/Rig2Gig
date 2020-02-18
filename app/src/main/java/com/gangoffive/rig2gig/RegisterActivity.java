package com.gangoffive.rig2gig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    EditText rEmailAddress, rConfirmEmail, rPassword, rConfirmPassword;
    Button rRegisterBtn;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        rEmailAddress = findViewById(R.id.registerEmail);
        rConfirmEmail = findViewById(R.id.registerConfirmEmail);
        rPassword = findViewById(R.id.registerPassword);
        rConfirmPassword = findViewById(R.id.registerConfirmPassword);
        rRegisterBtn = findViewById(R.id.registerBtn);
    }

    public void registerBtnOnClick(View view) {
        final String email = rEmailAddress.getText().toString().trim();
        String password = rPassword.getText().toString().trim();

        //Validation needed to check inputs

        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    userId = fAuth.getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userId);
                    Map<String, Object> user = new HashMap<>();
                    user.put("Email Address", email);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisterActivity.this, "Account has been created please log in", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onSuccess: user Profile is created for "+ userId);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Error creating account", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onFailure: " + e.toString());
                        }
                    });
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }else {
                    Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void registerLoginBtn(View view) {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
    }
}

