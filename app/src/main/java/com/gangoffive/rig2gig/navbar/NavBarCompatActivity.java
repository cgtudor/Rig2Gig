package com.gangoffive.rig2gig.navbar;

import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.utils.DefaultGoBack;
import com.google.android.material.navigation.NavigationView;

/**
 * Abstract class used to set up shared variables and methods between all three concrete nav bar classes.
 * @author Ben souch
 * @version #0.3b
 * @since #0.1b
 */
public abstract class NavBarCompatActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    protected DrawerLayout drawer;
    protected boolean minimise = false;
    protected String visibleFragmentName;
    protected Fragment visibleFragment;

    /**
     * Default constructor.
     * @since #0.1b
     */
    public NavBarCompatActivity()
    {

    }

    /**
     * This method is used to determine which option has been selected in the navigation drawer.
     * @param menuItem The menuItem variable passed in will contain the users clicked option in the navigation drawer.
     * @return Returns a new view of the selected navigation drawer option.
     * @since #0.1b
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
    {
        minimise = false;
        NavBarFactory navBarFactory = new NavBarFactory();

        if(visibleFragmentName != null && visibleFragment != null && visibleFragment.isVisible() && navBarFactory.selectFragment(menuItem).getClass().getSimpleName().equals(visibleFragment.getClass().getSimpleName()))
        {

        }
        else
        {
            visibleFragmentName = navBarFactory.selectFragment(menuItem).getClass().getSimpleName();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, navBarFactory.selectFragment(menuItem), visibleFragmentName).addToBackStack(visibleFragmentName).commit();
            getSupportFragmentManager().executePendingTransactions();
            visibleFragment = getSupportFragmentManager().findFragmentByTag(visibleFragmentName);
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    /**
     * This method is used to handle the back button and maintain track of the currently visible nav bar fragment to stop re-loading of the currently visible fragment.
     * @since #0.1b
     */
    @Override
    public void onBackPressed()
    {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if ((fragment instanceof DefaultGoBack) && ((DefaultGoBack) fragment).onBackPressed()) {
            super.onBackPressed();
        }
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if(!drawer.isDrawerOpen(GravityCompat.START) && minimise && backStackEntryCount == 1)
        {
            minimise = false;
            this.moveTaskToBack(true);
        }
        else if(!drawer.isDrawerOpen(GravityCompat.START) && backStackEntryCount == 1)
        {
            Toast.makeText(getApplicationContext(), "Press back again to exit.", Toast.LENGTH_SHORT).show();
            minimise = true;
        }
        else if(backStackEntryCount > 1)
        {
            int index = backStackEntryCount - 2;
            FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager().getBackStackEntryAt(index);

            visibleFragmentName = backStackEntry.getName();
            visibleFragment = getSupportFragmentManager().findFragmentByTag(visibleFragmentName);

            super.onBackPressed();
        }
    }

    /**
     * This method is used to handle resuming an activity.
     * @since #0.1b
     */
    @Override
    public void onResume()
    {
        minimise = false;
        super.onResume();
    }
}
