package com.gangoffive.rig2gig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

public class CredentialActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    EditText cFirstName, cLastName, cUsername, cPhoneNumber;
    RadioButton genderMale, genderFemale, genderOther;
    RadioGroup genderGroup, userGroup;
    Button submit;

    String gender, userType, userId;
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
                    case R.id.radioBtnBand:
                        userType = "Band";
                        break;
                    case R.id.radioBtnVenue:
                        userType = "Venue";
                        break;
                }
            }
        });
    }

    public void submitBtnOnClick(View view) {
        String fullName = cFirstName.getText().toString() + " " + cLastName.getText().toString();
        String username = cUsername.getText().toString();
        String phoneNumber = cPhoneNumber.getText().toString();
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
            }
        });
        //Link to next page
    }
}
