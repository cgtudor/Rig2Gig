package com.gangoffive.rig2gig;

import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;

public class NavigationContext
{
    private HashMap<String, NavigationStrategyInterface> navBarAlgorithmMap;

    NavigationContext()
    {
        navBarAlgorithmMap = new HashMap<>();
        navBarAlgorithmMap.put("Band", new ConcreteFanNavBar());
        navBarAlgorithmMap.put("Musician", new MusicianNavigation());
        navBarAlgorithmMap.put("Venue", new VenueNavigation());
    }

    public void navBarFinder(String userType, NavigationView navigationView)
    {
        navBarAlgorithmMap.get(userType).createNavBar(navigationView);
    }
}
