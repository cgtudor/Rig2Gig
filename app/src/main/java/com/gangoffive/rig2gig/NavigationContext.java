package com.gangoffive.rig2gig;

import java.util.HashMap;


public class NavigationContext
{
    private HashMap<String, Class> navBarAlgorithmMap;

    public NavigationContext()
    {
        navBarAlgorithmMap = new HashMap<>();
        navBarAlgorithmMap.put("Band", ConcreteFanNavBar.class);
        //navBarAlgorithmMap.put("Musician", new ConcreteMusicianNavBar());
        //navBarAlgorithmMap.put("Venue", new ConcreteVenueNavBar());
    }

    public Class navBarFinder(String userType)
    {
        return navBarAlgorithmMap.get(userType);
    }
}
