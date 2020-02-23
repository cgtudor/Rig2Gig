package com.gangoffive.rig2gig;

import com.google.android.material.navigation.NavigationView;

class VenueNavigation implements NavigationStrategyInterface
{
    VenueNavigation()
    {

    }

    @Override
    public void createNavBar(NavigationView navigationView)
    {
        navigationView.getMenu().removeItem(R.id.nav_my_band);
        navigationView.getMenu().removeItem(R.id.nav_create_band);
        navigationView.getMenu().removeItem(R.id.nav_create_performer_listing);
        navigationView.getMenu().removeItem(R.id.nav_view_venues);
    }
}
