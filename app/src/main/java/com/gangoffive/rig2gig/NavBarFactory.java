package com.gangoffive.rig2gig;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.HashMap;

public class NavBarFactory
{
    private HashMap<String, Fragment> fragmentMap;

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

    public Fragment selectFragment(@NonNull MenuItem menuItem)
    {
        return fragmentMap.get(menuItem.toString());
    }
}
