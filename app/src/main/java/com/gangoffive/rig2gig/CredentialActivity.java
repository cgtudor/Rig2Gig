package com.gangoffive.rig2gig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

/**
 * CredentialActivity loads when the activity is called.
 */
public class CredentialActivity extends AppCompatActivity {

    private static final String TAG = "======================";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    EditText cFirstName, cLastName, cUsername, cPhoneNumber, rEmailAddress, rConfirmEmail, rPassword, rConfirmPassword;;

    RadioButton fan, accFan, accMusician, accVenue;
    RadioGroup userGroup;
    Button submit, dateOfBirth;

    String gender, userId, dob;
    String userType;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    /**
     * When the onCreate is called previous states from the activity can be restored.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credential);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        cFirstName = findViewById(R.id.name);
        cLastName = findViewById(R.id.location);
        cUsername = findViewById(R.id.distance);
        cPhoneNumber = findViewById(R.id.cPhoneNumber);

        rEmailAddress = findViewById(R.id.emailReset);
        rConfirmEmail = findViewById(R.id.registerConfirmEmail);
        rPassword = findViewById(R.id.registerPassword);
        rConfirmPassword = findViewById(R.id.registerConfirmPassword);

        submit = findViewById(R.id.submitBtn);

    }

    /**
     * This method submits user details inputted to their document within the database.
     * @param view
     */
    public void submitBtnOnClick(View view) {
        String firstName = cFirstName.getText().toString();
        String lastName = cLastName.getText().toString();
        String username = cUsername.getText().toString();
        String phoneNumber = cPhoneNumber.getText().toString();
        String userType = AccountPurposeActivity.userType;

        final String email = rEmailAddress.getText().toString().trim();
        String confirmEmail = rConfirmEmail.getText().toString().trim();
        final String password = rPassword.getText().toString().trim();
        String confirmPassword = rConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            rEmailAddress.setError("Email is required!");
            return;
        }
        if (TextUtils.isEmpty(confirmEmail)) {
            rConfirmEmail.setError("Confirm email is required!");
            return;
        }
        if (!confirmEmail.matches(email)) {
            rConfirmEmail.setError("Email doesn't match!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            rPassword.setError("Password is required!");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            rConfirmPassword.setError("Confirm password is required!");
            return;
        }
        if (!confirmPassword.matches(password)) {
            rConfirmPassword.setError("Password doesn't match");
            return;
        }
        if (password.length() < 6) {
            rPassword.setError("Password needs to be 6 characters or longer!");
        }

        if (TextUtils.isEmpty(firstName)) {
            cFirstName.setError("Please enter a first name");
        }
        if (TextUtils.isEmpty(lastName)) {
            cLastName.setError("Please enter a last name");
        }
        if (TextUtils.isEmpty(username)) {
            cUsername.setError("Please enter a username name");
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            cPhoneNumber.setError("Please enter a phone number");
        }

        /**
         * Creating an account with Firebase from the information that the user has inputted.
         */
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    userId = fAuth.getUid();
                    DocumentReference documentReference = fStore.collection("users").document(userId);
                    Map<String, Object> user = new HashMap<>();
                    user.put("email-address", email);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(CredentialActivity.this, "Account has been created!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onSuccess: user Profile is created for " + userId);
                            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //startActivity(new Intent(getApplicationContext(),CredentialActivity.class));
                                        userId = fAuth.getUid();
                                        DocumentReference documentReference = fStore.collection("users").document(userId);
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("given-name", firstName);
                                        user.put("family-name", lastName);
                                        user.put("username", username);
                                        user.put("phone", phoneNumber);
                                        user.put("user-type", userType);
                                        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(CredentialActivity.this, "Information Added", Toast.LENGTH_SHORT).show();
                                                if (userType.equals("Venue"))
                                                {
                                                    startActivity(new Intent(getApplicationContext(), VenueActivity.class));
                                                }
                                                else
                                                    {
                                                        startActivity(new Intent(getApplicationContext(), NavBarActivity.class));
                                                    }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(CredentialActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                                String uuid = fAuth.getUid();
                                                System.out.println("=========================" + uuid);
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CredentialActivity.this, "Error creating account", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onFailure: " + e.toString());
                        }
                    });

                } else {
                    Toast.makeText(CredentialActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * This method is used to handle the back button.
     */
    @Override
    public void onBackPressed()
    {
        Toast.makeText(CredentialActivity.this, "Please fill in your credentials", Toast.LENGTH_LONG).show();
    }
}
