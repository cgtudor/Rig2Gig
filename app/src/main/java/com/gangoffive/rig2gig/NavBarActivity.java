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

public class NavBarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Nav-Bar*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_draw_open, R.string.navigation_draw_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /*On startup, the my profile page will be shown. Upon rotation, savedInstanceState
        will not be null and therefore will not navigate the user away from what they
        are currently viewing.*/
        if (savedInstanceState == null) {
            //Following line determines the first fragment shown to the user.
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyProfileFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_my_profile);
        }

        //Strategy design pattern
        // interface / abstract class navigation?
        // concrete basic
        // concrete band
        // concrete fan
        // concrete venue
        // concrete another????
    }

    /**
     * This method is used to determine which option has been selected in the navigation drawer.
     * @param menuItem The menuItem variable passed in will contain the users clicked option in the navigation drawer.
     * @return Returns a new view of the selected navigation drawer option.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        //Factory design pattern
        switch(menuItem.getItemId())
        {
            case R.id.nav_my_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyProfileFragment()).commit();
                break;
            case R.id.nav_my_band:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyBandFragment()).commit();
                break;
            case R.id.nav_create_band:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CreateBandFragment()).commit();
                break;
            case R.id.nav_view_bands:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewBandsFragment()).commit();
                break;
            case R.id.nav_view_venues:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewVenuesFragment()).commit();
                break;
            case R.id.nav_notifications:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationsFragment()).commit();
                break;
            case R.id.nav_about_us:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutUsFragment()).commit();
                break;
            case R.id.nav_settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
            case R.id.nav_privacy_policy:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PrivacyPolicyFragment()).commit();
                break;
            default:
                break;
        }

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
