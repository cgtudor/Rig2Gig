package com.gangoffive.rig2gig.navbar;

import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import com.gangoffive.rig2gig.R;
import com.google.android.material.navigation.NavigationView;


public class ConcreteFanNavBar extends NavBarCompatActivity
{
    public ConcreteFanNavBar()
    {

    }

    /**
     * This method is used to create the navigation bar.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_concrete_fan_nav_bar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_concrete_fan);
        NavigationView navigationView = findViewById(R.id.nav_concrete_fan_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_draw_open, R.string.navigation_draw_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null)
        {
            //Following line determines the first fragment shown to the user.
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new VenueConsoleFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_my_profile);
        }
    }
}
