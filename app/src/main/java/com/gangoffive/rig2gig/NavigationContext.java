package com.gangoffive.rig2gig;

import java.util.HashMap;


public class NavigationContext
{
    private HashMap<String, Class> navBarAlgorithmMap;

    public NavigationContext()
    {
        navBarAlgorithmMap = new HashMap<>();
        navBarAlgorithmMap.put("Band", ConcreteFanNavBar.class);
        navBarAlgorithmMap.put("Musician", ConcreteMusicianNavBar.class);
        navBarAlgorithmMap.put("Venue", ConcreteVenueNavBar.class);
    }

    public Class navBarFinder(String userType)
    {
        return navBarAlgorithmMap.get(userType);
    }
}
