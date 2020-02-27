package com.gangoffive.rig2gig;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.HashMap;

public class NavBarFactory
{
    private HashMap<String, Fragment> fragmentMap;

    /**
     * Constructor creates a new HashMap and adds all fragment instances to it.
     */
    NavBarFactory()
    {
        fragmentMap = new HashMap<>();

        fragmentMap.put("My Profile", new MyProfileFragment());
        fragmentMap.put("My Band", new MyBandFragment());
        fragmentMap.put("Create band listing", new CreatePerformerListing());
        fragmentMap.put("Create Band", new CreateBandFragment());
        fragmentMap.put("View Bands", new ViewBandsFragment());
        fragmentMap.put("View Venues", new ViewVenuesFragment());
        fragmentMap.put("Notifications", new NotificationsFragment());
        fragmentMap.put("About Us", new AboutUsFragment());
        fragmentMap.put("Settings", new SettingsFragment());
    }

    /**
     * This method is used to obtain a fragment from the HashMap based upon the passed in parameter.
     * @param menuItem This parameter represents the nav bar label selected by the user.
     * @return Returns the fragment to be instantiated.
     */
    public Fragment selectFragment(@NonNull MenuItem menuItem)
    {
        return fragmentMap.get(menuItem.toString());
    }
}
