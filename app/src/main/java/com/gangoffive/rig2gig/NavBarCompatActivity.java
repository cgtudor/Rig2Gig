package com.gangoffive.rig2gig;

import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public abstract class NavBarCompatActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener
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
     * This method
     */
    @Override
    public void onBackPressed()
    {
        if(drawer.isDrawerOpen(GravityCompat.START))
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

    //Create variable to become true once the user has been warned about pressing back.
    //In the onBackPressed() method, set the variable to true. Upon pressing back again, the app will minimise.
    //In the onResume() method, set the variable back to false.
    //In the onNavigationItemSelected() method, set the variable back to false as the user has navigated away between back button presses.
    @Override
    public void onResume()
    {
        super.onResume();
    }
}
