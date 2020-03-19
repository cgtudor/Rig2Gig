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
    }

    public void musicianBtnOnClick(View view) {
        userType = "Musician";
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " +  userType);
        startActivity(new Intent(getApplicationContext(), CredentialActivity.class));
    }

    public void venueBtnOnClick(View view) {
        userType = "Venue";
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " +  userType);
        startActivity(new Intent(getApplicationContext(), TabbedVenueActivity.class));
    }
}
