package com.gangoffive.rig2gig.advert.management;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.gangoffive.rig2gig.advert.management.PerformerAdvertisementEditor;

public class PlaceholderPerformerAdvertisement extends Fragment
{
    private String musicianRef = "A6M0CzH2WMkw7FUIFkM8";
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(getActivity(), PerformerAdvertisementEditor.class);
        intent.putExtra("EXTRA_PERFORMER_ID", musicianRef);
        intent.putExtra("EXTRA_LISTING_ID","");
        intent.putExtra("EXTRA_PERFORMER_TYPE","Musician");
        startActivity(intent);
        //Intent intent = new Intent(CreateBandAdvertisement.this, BandListingDetailsActivity.class);
    }
}
