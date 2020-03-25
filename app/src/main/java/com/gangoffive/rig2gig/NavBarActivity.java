package com.gangoffive.rig2gig;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NavBarActivity extends AppCompatActivity
{
    /**
     * This method is used to create the navigation bar.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        NavigationContext navigationContext = new NavigationContext();
        Intent fragIntent = new Intent(this, navigationContext.navBarFinder(AccountPurposeActivity.userType));

        if(getIntent().getStringExtra("TARGET_FRAGMENT") != null)
        {
            String extra = savedInstanceState.getString("TARGET_FRAGMENT");
            fragIntent.putExtra("TARGET_FRAGMENT", extra);
        }

        //Decide which navbar to display.

        startActivity(fragIntent);

        finish();

        //for demonstrating band manager
/*        Intent intent = new Intent(this, ManageBandMembersActivity.class);
        intent.putExtra("EXTRA_BAND_ID", "S0lVRscAvnnE3sbqn9X5");
        startActivity(intent);*/
    }
}
