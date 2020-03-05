package com.gangoffive.rig2gig;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;


public class ConcreteFanNavBar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout drawer;

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
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyProfileFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_my_profile);
        }
    }

    /**
     * This method is used to determine which option has been selected in the navigation drawer.
     * @param menuItem The menuItem variable passed in will contain the users clicked option in the navigation drawer.
     * @return Returns a new view of the selected navigation drawer option.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        NavBarFactory navBarFactory = new NavBarFactory();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, navBarFactory.selectFragment(menuItem)).commit();

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    /**
     * If the back button is pressed, simply close the navigation drawer instead of navigating away from the activity.
     */
    @Override
    public void onBackPressed()
    {
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }
}
