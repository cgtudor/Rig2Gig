package com.gangoffive.rig2gig.advert.management;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.gangoffive.rig2gig.advert.management.BandAdvertisementEditor;

public class PlaceholderBandAdvertisement extends Fragment
{

    private String bandRef = "S0lVRscAvnnE3sbqn9X5";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(getActivity(), BandAdvertisementEditor.class);
        intent.putExtra("EXTRA_BAND_ID", bandRef);
        intent.putExtra("EXTRA_LISTING_ID","");
        startActivity(intent);
    }
}
