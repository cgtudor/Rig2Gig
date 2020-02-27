package com.gangoffive.rig2gig;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class PlaceholderVenueAdvertisement extends Fragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(getActivity(), CreateVenueAdvertisement.class));
        //Intent intent = new Intent(CreateBandAdvertisement.this, BandListingDetailsActivity.class);
    }
}
