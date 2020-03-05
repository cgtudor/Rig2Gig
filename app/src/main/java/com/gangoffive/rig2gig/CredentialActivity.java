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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    EditText cFirstName, cLastName, cUsername, cPhoneNumber;
    RadioButton genderMale, fan, genderFemale, genderOther, accFan, accMusician, accVenue;
    RadioGroup genderGroup, userGroup;
    Button submit;

    String gender, userId;
    public static String userType;

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

        cFirstName = findViewById(R.id.cFirstName);
        cLastName = findViewById(R.id.cLastName);
        cUsername = findViewById(R.id.cUsername);
        cPhoneNumber = findViewById(R.id.cPhoneNumber);
        genderGroup = findViewById(R.id.genderRadioGroup);
        userGroup = findViewById(R.id.userRadioGroup);
        submit = findViewById(R.id.submitBtn);

        genderMale = findViewById(R.id.radioBtnMale);
        genderFemale = findViewById(R.id.radioBtnFemale);
        genderOther = findViewById(R.id.radioBtnFemale);

        accFan = findViewById(R.id.radioBtnFan);
        accMusician = findViewById(R.id.radioBtnMusician);
        accVenue = findViewById(R.id.radioBtnVenue);
        fan = findViewById(R.id.radioBtnMusician);

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioBtnMale:
                        gender = "Male";
                        break;
                    case R.id.radioBtnFemale:
                        gender = "Female";
                        break;
                    case R.id.radioBtnOther:
                        gender = "Other";
                        break;
                }
            }
        });

        userGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioBtnMusician:
                        userType = "Musician";
                        break;
                    case R.id.radioBtnFan:
                        userType = "Fan";
                        break;
                    case R.id.radioBtnVenue:
                        userType = "Venue";
                        break;
                }
            }
        });
    }

    /**
     * This method submits user details inputted to their document within the database.
     * @param view
     */
    public void submitBtnOnClick(View view) {
        if (gender == null || userType == null){
            Toast.makeText(CredentialActivity.this, "Please Make Sure Gender & User Type Is Selected", Toast.LENGTH_SHORT).show();
        }else{
            String firstName = cFirstName.getText().toString();
            String lastName = cLastName.getText().toString();
            String fullName = cFirstName.getText().toString() + " " + cLastName.getText().toString();
            String username = cUsername.getText().toString();
            String phoneNumber = cPhoneNumber.getText().toString();

            if (TextUtils.isEmpty(firstName)){
                cFirstName.setError("Please enter a first name");
            }
            if (TextUtils.isEmpty(lastName)){
                cLastName.setError("Please enter a last name");
            }
            if (TextUtils.isEmpty(username)){
                cUsername.setError("Please enter a username name");
            }
            if (TextUtils.isEmpty(phoneNumber)){
                cPhoneNumber.setError("Please enter a phone number");
            }
            
            userId = fAuth.getUid();
            DocumentReference documentReference = fStore.collection("users").document(userId);
            Map<String, Object> user = new HashMap<>();
            user.put("Full Name", fullName);
            user.put("Username", username);
            user.put("Phone Number", phoneNumber);
            user.put("Gender", gender);
            user.put("User Type", userType);
            documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(CredentialActivity.this, "Information Added", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), NavBarActivity.class));
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
}
