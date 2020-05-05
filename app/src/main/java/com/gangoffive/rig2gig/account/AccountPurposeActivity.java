package com.gangoffive.rig2gig.account;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.musician.management.SignedInTabbedMusicianActivity;
import com.gangoffive.rig2gig.musician.management.TabbedMusicianActivity;
import com.gangoffive.rig2gig.ui.TabbedView.SignedInVenuePagerAdapter;
import com.google.firebase.auth.FirebaseAuth;

public class AccountPurposeActivity extends AppCompatActivity {

    RadioGroup userGroup;
    public static String userType;
    FirebaseAuth fAuth;

    /**
     * Loads the xml and gets an instance of Firebase.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_purpose);
        fAuth = FirebaseAuth.getInstance();
    }

    /**
     * Sets the user type to a Musician and loads the TabbedMusicianActivity
     * @param view
     */
    public void musicianBtnOnClick(View view) {
        AccountPurposeActivity.userType = "Musician";
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " +  userType);
        startActivity(new Intent(getApplicationContext(), TabbedMusicianActivity.class));
    }

    /**
     * Sets the user type to a Venue and loads the TabbedVenueActivity
     * @param view
     */
    public void venueBtnOnClick(View view) {
        AccountPurposeActivity.userType = "Venue";
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " +  userType);
        startActivity(new Intent(getApplicationContext(), TabbedVenueActivity.class));
    }
}
