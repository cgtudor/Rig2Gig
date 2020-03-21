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
/*        Intent fragIntent = new Intent(this, navigationContext.navBarFinder(CredentialActivity.userType));

        if(savedInstanceState != null)
        {
            String extra = savedInstanceState.getString("CALLED_FROM");
            String normalExtra = savedInstanceState.get("CALLED_FROM").toString();

            System.out.println(
            "");
            System.out.println("");
        }

        if(savedInstanceState != null && savedInstanceState.getString("CALLED_FROM") != null)
        {
            fragIntent.putExtra("OPEN", "NOTIFICATIONS");
        }

        //Decide which navbar to display.

        startActivity(fragIntent);*/

        startActivity(new Intent(this, navigationContext.navBarFinder(AccountPurposeActivity.userType)));

        finish();

        //for demonstrating band manager
/*        Intent intent = new Intent(this, ManageBandMembersActivity.class);
        intent.putExtra("EXTRA_BAND_ID", "S0lVRscAvnnE3sbqn9X5");
        startActivity(intent);*/
    }
}
