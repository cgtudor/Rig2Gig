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
    protected Toolbar toolbar;
    protected DrawerLayout drawer;
    protected NavigationView navigationView;
    protected ActionBarDrawerToggle toggle;

    public ConcreteFanNavBar()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_concrete_fan_nav_bar);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_concrete_fan);
        navigationView = findViewById(R.id.nav_concrete_fan_view);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_draw_open, R.string.navigation_draw_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null)
        {
            //Following line determines the first fragment shown to the user.
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyProfileFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_my_profile);
        }

        /*navigationView.getMenu().removeItem(R.id.nav_my_band);
        navigationView.getMenu().removeItem(R.id.nav_create_band);*/
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        NavBarFactory navBarFactory = new NavBarFactory();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, navBarFactory.selectFragment(menuItem)).commit() ;

        //Add following lines to concrete classes of Fan, Musician and Venue.
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
