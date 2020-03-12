package com.gangoffive.rig2gig;

import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public abstract class NavBarCompatActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    protected DrawerLayout drawer;
    protected boolean minimise = false;

    public NavBarCompatActivity()
    {

    }

    /**
     * This method is used to determine which option has been selected in the navigation drawer.
     * @param menuItem The menuItem variable passed in will contain the users clicked option in the navigation drawer.
     * @return Returns a new view of the selected navigation drawer option.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        minimise = false;
        NavBarFactory navBarFactory = new NavBarFactory();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, navBarFactory.selectFragment(menuItem)).commit() ;

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    /**
     * This method is used to handle the back button.
     */
    @Override
    public void onBackPressed()
    {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if ((fragment instanceof DefaultGoBack) && ((DefaultGoBack) fragment).onBackPressed()) {
            super.onBackPressed();
        }
        else if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(!drawer.isDrawerOpen(GravityCompat.START) && !minimise)
        {
            Toast.makeText(getApplicationContext(), "Press back again to exit.", Toast.LENGTH_SHORT).show();
            minimise = true;
        }
        else if(!drawer.isDrawerOpen(GravityCompat.START) && minimise)
        {
            minimise = false;
            this.moveTaskToBack(true);
        }
    }

    /**
     * This method is used to handle resuming an activity.
     */
    @Override
    public void onResume()
    {
        minimise = false;
        super.onResume();
    }
}
