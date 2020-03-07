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
    private Button editVenueAd, editVenueDetails, createVenueAd,
            editMusicianPerformerAd, editMusicianDetails, createMusicianPerformerAd,
            editBandPerformerAd, editBandDetails, createBandPerformerAd;
    private String musicianRef = "A6M0CzH2WMkw7FUIFkM8";
    private String musicianAdvertRef = "8DVBTUHrV5ZWIS46aN6Z";
    private String bandRef = "S0lVRscAvnnE3sbqn9X5";
    private String bandAdvertRef = "Sbn1SoXanABp2Y3KSZbV";

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
        createMusicianPerformerAd = v.findViewById(R.id.createMusicianPerformerAd);
        createMusicianPerformerAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMusicianPerformerAd();
            }
        });
        editMusicianPerformerAd = v.findViewById(R.id.editMusicianPerformerAd);
        editMusicianPerformerAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMusicianPerformerAd();
            }
        });
        editMusicianDetails = v.findViewById(R.id.editMusicianDetails);
        editMusicianDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMusicianDetails();
            }
        });
        createBandPerformerAd = v.findViewById(R.id.createBandPerformerAd);
        createBandPerformerAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBandPerformerAd();
            }
        });
        editBandPerformerAd = v.findViewById(R.id.editBandPerformerAd);
        editBandPerformerAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBandPerformerAd();
            }
        });
        editBandDetails = v.findViewById(R.id.editBandDetails);
        editBandDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBandDetails();
            }
        });
        return v;
    }

    public void createMusicianPerformerAd()
    {
        Intent intent = new Intent(getActivity(), PerformerAdvertisementEditor.class);
        intent.putExtra("EXTRA_PERFORMER_ID", musicianRef);
        intent.putExtra("EXTRA_LISTING_ID","");
        intent.putExtra("EXTRA_PERFORMER_TYPE","Musician");
        startActivity(intent);
    }

    public void editMusicianPerformerAd()
    {
        Intent intent = new Intent(getActivity(), PerformerAdvertisementEditor.class);
        intent.putExtra("EXTRA_PERFORMER_ID", musicianRef);
        intent.putExtra("EXTRA_LISTING_ID",musicianAdvertRef);
        intent.putExtra("EXTRA_PERFORMER_TYPE","Musician");
        startActivity(intent);
    }

    public void editMusicianDetails()
    {
        Intent intent = new Intent(getActivity(), MusicianDetailsEditor.class);
        intent.putExtra("EXTRA_MUSICIAN_ID", musicianRef);
        startActivity(intent);
    }

    public void createBandPerformerAd()
    {
        Intent intent = new Intent(getActivity(), PerformerAdvertisementEditor.class);
        intent.putExtra("EXTRA_PERFORMER_ID", bandRef);
        intent.putExtra("EXTRA_LISTING_ID","");
        intent.putExtra("EXTRA_PERFORMER_TYPE","Band");
        startActivity(intent);
    }

    public void editBandPerformerAd()
    {
        Intent intent = new Intent(getActivity(), PerformerAdvertisementEditor.class);
        intent.putExtra("EXTRA_PERFORMER_ID", bandRef);
        intent.putExtra("EXTRA_LISTING_ID",bandAdvertRef);
        intent.putExtra("EXTRA_PERFORMER_TYPE","Band");
        startActivity(intent);
    }

    public void editBandDetails()
    {
        Intent intent = new Intent(getActivity(), BandDetailsEditor.class);
        intent.putExtra("EXTRA_BAND_ID", bandRef);
        startActivity(intent);
    }
}
