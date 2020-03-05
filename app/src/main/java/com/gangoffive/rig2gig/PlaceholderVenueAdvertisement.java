package com.gangoffive.rig2gig;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class PlaceholderVenueAdvertisement extends Fragment
{
    private Button editVenueAd, editVenueDetails, createVenueAd;

/*    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        //Intent intent = new Intent(CreateBandAdvertisement.this, BandListingDetailsActivity.class);


        //startActivity(new Intent(getActivity(), VenueAdvertisementEditor.class));

    }*/

    @Override
    public View onCreateView(LayoutInflater l, ViewGroup vg, Bundle b)
    {
        View v = l.inflate(R.layout.placeholder_venue_advertisement_delete_later, vg, false);
        createVenueAd = v.findViewById(R.id.createVenueAd);
        createVenueAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VenueAdvertisementEditor.class);
                intent.putExtra("EXTRA_VENUE_ID", "80kaTvOTmggcOWjMqW8r");
                intent.putExtra("EXTRA_LISTING_ID","");
                startActivity(intent);
            }
        });
        editVenueAd = v.findViewById(R.id.editVenueAd);
        editVenueAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VenueAdvertisementEditor.class);
                intent.putExtra("EXTRA_VENUE_ID", "80kaTvOTmggcOWjMqW8r");
                intent.putExtra("EXTRA_LISTING_ID","t1izIZlw870JdYRMhAWI");
                startActivity(intent);
            }
        });
        editVenueDetails = v.findViewById(R.id.editVenueDetails);
        editVenueDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VenueDetailsEditor.class);
                intent.putExtra("EXTRA_VENUE_ID", "80kaTvOTmggcOWjMqW8r");
                startActivity(intent);
            }
        });
        return v;
    }
}
