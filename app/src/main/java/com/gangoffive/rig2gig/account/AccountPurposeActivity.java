package com.gangoffive.rig2gig.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.musician.management.TabbedMusicianActivity;

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
        startActivity(new Intent(getApplicationContext(), TabbedMusicianActivity.class));
    }

    public void venueBtnOnClick(View view) {
        userType = "Venue";
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " +  userType);
            startActivity(new Intent(getApplicationContext(), TabbedVenueActivity.class));
    }
}
