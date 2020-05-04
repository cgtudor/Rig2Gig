package com.gangoffive.rig2gig.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.musician.management.SignedInTabbedMusicianActivity;
import com.gangoffive.rig2gig.musician.management.TabbedMusicianActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SignedInAccountPurpose extends AppCompatActivity {

    RadioGroup userGroup;
    public static String userType;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_purpose);

        fAuth = FirebaseAuth.getInstance();
    }

    public void musicianBtnOnClick(View view) {
        AccountPurposeActivity.userType = "Musician";
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " +  userType);
        if (fAuth.getCurrentUser() != null)
        startActivity(new Intent(getApplicationContext(), SignedInTabbedMusicianActivity.class));
    }

    public void venueBtnOnClick(View view) {
        AccountPurposeActivity.userType = "Venue";
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " +  userType);
        startActivity(new Intent(getApplicationContext(), SignedInTabbedVenueActivity.class));
    }

    @Override
    public void onBackPressed() {
    // super.onBackPressed();
    // Not calling **super**, disables back button in current screen.
    }
}
