package com.gangoffive.rig2gig.advert.management;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.venue.management.VenueDetailsEditor;
import com.gangoffive.rig2gig.band.management.BandDetailsEditor;
import com.gangoffive.rig2gig.musician.management.MusicianDetailsEditor;

public class PlaceholderVenueAdvertisement extends Fragment
{
    private Button editVenueAd, editVenueDetails, createVenueAd,
            editMusicianPerformerAd, editMusicianDetails, createMusicianPerformerAd,
            editBandPerformerAd, editBandDetails, createBandPerformerAd,
            editBandAdvert, createBandAdvert,
            editMusicianAdvert, createMusicianAdvert;
    private String musicianRef = "A6M0CzH2WMkw7FUIFkM8";
    private String musicianPerformerAdvertRef = "8DVBTUHrV5ZWIS46aN6Z";
    private String bandRef = "S0lVRscAvnnE3sbqn9X5";
    private String bandPerformerAdvertRef = "Sbn1SoXanABp2Y3KSZbV";

    private String bandAdvertRef = "ELxzP1AmD24yfOjALC8Y";
    private String musicianAdvertRef = "J8wNIJeCHyapAJ11khir";

    @Override
    public View onCreateView(LayoutInflater l, ViewGroup vg, Bundle b)
    {
        View v = l.inflate(R.layout.placeholder_venue_advertisement_delete_later, vg, false);
        createVenueAd = v.findViewById(R.id.createVenueAd);
        createVenueAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VenueAdvertisementEditor.class);
                intent.putExtra("EXTRA_VENUE_ID", "0wRVzyLdNSHlMQv31g1y");
                intent.putExtra("EXTRA_LISTING_ID","");
                startActivity(intent);
            }
        });
        editVenueAd = v.findViewById(R.id.editVenueAd);
        editVenueAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VenueAdvertisementEditor.class);
                intent.putExtra("EXTRA_VENUE_ID", "0wRVzyLdNSHlMQv31g1y");
                intent.putExtra("EXTRA_LISTING_ID","xqVn92V8BvUkdPby4Kus");
                startActivity(intent);
            }
        });
        editVenueDetails = v.findViewById(R.id.editVenueDetails);
        editVenueDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VenueDetailsEditor.class);
                intent.putExtra("EXTRA_VENUE_ID", "0wRVzyLdNSHlMQv31g1y");
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
        createBandAdvert = v.findViewById(R.id.createBandAdvert);
        createBandAdvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBandAdvert();
            }
        });
        editBandAdvert = v.findViewById(R.id.editBandAdvert);
        editBandAdvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBandAdvert();
            }
        });
        createMusicianAdvert = v.findViewById(R.id.createMusicianAdvert);
        createMusicianAdvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMusicianAdvert();
            }
        });
        editMusicianAdvert = v.findViewById(R.id.editMusicianAdvert);
        editMusicianAdvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMusicianAdvert();
            }
        });
        return v;
    }
    //create ad for musicians advertising to venues (as performers)
    public void createMusicianPerformerAd()
    {
        Intent intent = new Intent(getActivity(), PerformerAdvertisementEditor.class);
        intent.putExtra("EXTRA_PERFORMER_ID", musicianRef);
        intent.putExtra("EXTRA_LISTING_ID","");
        intent.putExtra("EXTRA_PERFORMER_TYPE","Musician");
        startActivity(intent);
    }

    //edit ad for musicians advertising to venues (as performers)
    public void editMusicianPerformerAd()
    {
        Intent intent = new Intent(getActivity(), PerformerAdvertisementEditor.class);
        intent.putExtra("EXTRA_PERFORMER_ID", musicianRef);
        intent.putExtra("EXTRA_LISTING_ID",musicianPerformerAdvertRef);
        intent.putExtra("EXTRA_PERFORMER_TYPE","Musician");
        startActivity(intent);
    }

    //create ad for musicians advertising themselves to bands (ie musician looking for a band)
    public void createMusicianAdvert()
    {
        Intent intent = new Intent(getActivity(), MusicianAdvertisementEditor.class);
        intent.putExtra("EXTRA_MUSICIAN_ID", musicianRef);
        intent.putExtra("EXTRA_LISTING_ID","");
        startActivity(intent);
    }

    //edit ad for musicians advertising themselves to bands (ie musician looking for a band)
    public void editMusicianAdvert()
    {
        Intent intent = new Intent(getActivity(), MusicianAdvertisementEditor.class);
        intent.putExtra("EXTRA_MUSICIAN_ID", musicianRef);
        intent.putExtra("EXTRA_LISTING_ID", musicianAdvertRef);
        startActivity(intent);
    }

    //edit musician details (data held in musicians collection)
    public void editMusicianDetails()
    {
        Intent intent = new Intent(getActivity(), MusicianDetailsEditor.class);
        intent.putExtra("EXTRA_MUSICIAN_ID", musicianRef);
        startActivity(intent);
    }

    //create ad for bands advertising to venues (as performers)
    public void createBandPerformerAd()
    {
        Intent intent = new Intent(getActivity(), PerformerAdvertisementEditor.class);
        intent.putExtra("EXTRA_PERFORMER_ID", bandRef);
        intent.putExtra("EXTRA_LISTING_ID","");
        intent.putExtra("EXTRA_PERFORMER_TYPE","Band");
        startActivity(intent);
    }

    //edit ad for bands advertising to venues (as performers)
    public void editBandPerformerAd()
    {
        Intent intent = new Intent(getActivity(), PerformerAdvertisementEditor.class);
        intent.putExtra("EXTRA_PERFORMER_ID", bandRef);
        intent.putExtra("EXTRA_LISTING_ID",bandPerformerAdvertRef);
        intent.putExtra("EXTRA_PERFORMER_TYPE","Band");
        startActivity(intent);
    }

    //create ad for bands advertising to musicians (ie band looking for new members)
    public void createBandAdvert()
    {
        Intent intent = new Intent(getActivity(), BandAdvertisementEditor.class);
        intent.putExtra("EXTRA_BAND_ID", bandRef);
        intent.putExtra("EXTRA_LISTING_ID","");
        startActivity(intent);
    }

    //edit ad for bands advertising to musicians (ie band looking for new members)
    public void editBandAdvert()
    {
        Intent intent = new Intent(getActivity(), BandAdvertisementEditor.class);
        intent.putExtra("EXTRA_BAND_ID", bandRef);
        intent.putExtra("EXTRA_LISTING_ID",bandAdvertRef);
        startActivity(intent);
    }

    //edit band details (data held in bands collection)
    public void editBandDetails()
    {
        Intent intent = new Intent(getActivity(), BandDetailsEditor.class);
        intent.putExtra("EXTRA_BAND_ID", bandRef);
        startActivity(intent);
    }

}