package com.gangoffive.rig2gig.navbar;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import com.gangoffive.rig2gig.R;
import com.google.android.material.navigation.NavigationView;

public class ConcreteMusicianNavBar extends NavBarCompatActivity
{
    public ConcreteMusicianNavBar()
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

        setContentView(R.layout.activity_concrete_musician_nav_bar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_concrete_musician);
        NavigationView navigationView = findViewById(R.id.nav_concrete_musician_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_draw_open, R.string.navigation_draw_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(getIntent().getStringExtra("OPEN_FRAGMENT") != null && getIntent().getStringExtra("OPEN_FRAGMENT").equals("COMMS"))
        {
            ViewCommsFragment startFragment = new ViewCommsFragment();
            visibleFragmentName = startFragment.getClass().getSimpleName();
            //Following line determines the first fragment shown to the user.
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, startFragment, visibleFragmentName).addToBackStack(visibleFragmentName).commit();
            getSupportFragmentManager().executePendingTransactions();
            visibleFragment = getSupportFragmentManager().findFragmentByTag(visibleFragmentName);
            navigationView.setCheckedItem(R.id.nav_notifications);
        }
        else if (savedInstanceState == null)
        {
            Intent intent = getIntent();
            if(intent != null && intent.getStringExtra("OPEN") != null && intent.getStringExtra("OPEN").equals("NOTIFICATION"))
            {
                ViewCommsFragment viewCommsFragment = new ViewCommsFragment();
                visibleFragmentName = viewCommsFragment.getClass().getSimpleName();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, viewCommsFragment, visibleFragmentName).addToBackStack(visibleFragmentName).commit();
                getSupportFragmentManager().executePendingTransactions();
                visibleFragment = getSupportFragmentManager().findFragmentByTag(visibleFragmentName);
                navigationView.setCheckedItem(R.id.nav_notifications);
            }
            else
            {
                MusicianConsoleFragment startFragment = new MusicianConsoleFragment();
                visibleFragmentName = startFragment.getClass().getSimpleName();
                //Following line determines the first fragment shown to the user.
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, startFragment, visibleFragmentName).addToBackStack(visibleFragmentName).commit();
                getSupportFragmentManager().executePendingTransactions();
                visibleFragment = getSupportFragmentManager().findFragmentByTag(visibleFragmentName);
                navigationView.setCheckedItem(R.id.nav_console);
            }
        }
    }
}
