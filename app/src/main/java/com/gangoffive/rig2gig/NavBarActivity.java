package com.gangoffive.rig2gig;


import android.content.Intent;
import android.os.Bundle;
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

        //Decide which navbar to display.
        NavigationContext navigationContext = new NavigationContext();
        startActivity(new Intent(this, navigationContext.navBarFinder(AccountPurposeActivity.userType)));
    }
}
