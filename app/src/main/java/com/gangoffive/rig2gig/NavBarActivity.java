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
        if (AccountPurposeActivity.userType != null)
        {
            Intent fragIntent = new Intent(this, navigationContext.navBarFinder(AccountPurposeActivity.userType));

            if(getIntent().getStringExtra("TARGET_FRAGMENT") != null)
            {
                String extra = getIntent().getStringExtra("TARGET_FRAGMENT");
                fragIntent.putExtra("OPEN_FRAGMENT", extra);
            }

            //Decide which navbar to display.


            startActivity(fragIntent);
        }

        finish();

    }
}
