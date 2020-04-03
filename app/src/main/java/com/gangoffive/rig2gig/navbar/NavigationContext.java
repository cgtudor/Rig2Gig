package com.gangoffive.rig2gig.navbar;

import com.gangoffive.rig2gig.navbar.ConcreteFanNavBar;
import com.gangoffive.rig2gig.navbar.ConcreteMusicianNavBar;
import com.gangoffive.rig2gig.navbar.ConcreteVenueNavBar;

import java.util.HashMap;


public class NavigationContext
{
    private HashMap<String, Class> navBarAlgorithmMap;

    /**
     * This constructor creates a new HashMap and adds all concrete versions of the navbar to it.
     */
    public NavigationContext()
    {
        navBarAlgorithmMap = new HashMap<>();
        navBarAlgorithmMap.put("Fan", ConcreteFanNavBar.class);
        navBarAlgorithmMap.put("Musician", ConcreteMusicianNavBar.class);
        navBarAlgorithmMap.put("Venue", ConcreteVenueNavBar.class);
    }

    /**
     * This method is used to obtain a concrete navbar from the  HashMap based upon the passed in parameter.
     * @param userType This parameter is used to match the appropriate concrete navbar from the HashMap.
     * @return Returns a class to instantiate from the HashMap.
     */
    public Class navBarFinder(String userType)
    {
        return navBarAlgorithmMap.get(userType);
    }
}
