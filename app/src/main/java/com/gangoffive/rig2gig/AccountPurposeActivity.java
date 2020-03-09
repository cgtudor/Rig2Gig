package com.gangoffive.rig2gig;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

public class AccountPurposeActivity extends AppCompatActivity {

    RadioGroup userGroup;
    public static String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_purpose);

        userGroup = findViewById(R.id.userRadioGroup);

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

    public void submitBtnOnClick(View view) {
        if (userType == null)
        {
            Toast.makeText(AccountPurposeActivity.this, "Please make sure you select an account type", Toast.LENGTH_SHORT).show();
        }
        else
        {
            startActivity(new Intent(getApplicationContext(), CredentialActivity.class));
        }
    }
}
