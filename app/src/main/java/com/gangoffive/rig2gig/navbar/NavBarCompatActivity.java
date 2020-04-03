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

public abstract class NavBarCompatActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    protected DrawerLayout drawer;
    protected boolean minimise = false;
    protected String visibleFragmentName;
    protected Fragment visibleFragment;

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
     * This method is used to handle the back button.
     */
    @Override
    public void onBackPressed()
    {
        System.out.println("CURRENT FRAGMENT =================== " + getSupportFragmentManager().findFragmentByTag(visibleFragmentName));

        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();

        System.out.println("BACK STACK ENTRY COUNT ============= " + backStackEntryCount);

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
            //System.out.println("BACK STACK ENTRY COUNT ============= " + backStackEntryCount);
            int index = backStackEntryCount - 2;
            FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager().getBackStackEntryAt(index);

            System.out.println(backStackEntry.getName());

            visibleFragmentName = backStackEntry.getName();
            visibleFragment = getSupportFragmentManager().findFragmentByTag(visibleFragmentName);

            super.onBackPressed();

            System.out.println(backStackEntryCount);
            System.out.println(backStackEntry.getClass().getSimpleName());



            System.out.println("VISIBLE FRAGMENT NAME AFTER BACK ================= " + visibleFragmentName);
            System.out.println("VISIBLE FRAGMENT SIMPLE NAME AFTER BACK ========== " + visibleFragment);
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
