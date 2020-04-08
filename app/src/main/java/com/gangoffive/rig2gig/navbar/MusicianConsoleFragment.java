package com.gangoffive.rig2gig.navbar;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.gangoffive.rig2gig.musician.management.MusicianDetailsEditor;
import com.gangoffive.rig2gig.advert.management.PerformerAdvertisementEditor;
import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.advert.details.MusicianListingDetailsActivity;
import com.gangoffive.rig2gig.advert.details.PerformanceListingDetailsActivity;
import com.gangoffive.rig2gig.advert.index.BandAdvertIndexActivity;
import com.gangoffive.rig2gig.advert.index.VenueAdvertIndexActivity;
import com.gangoffive.rig2gig.advert.management.MusicianAdvertisementEditor;
import com.gangoffive.rig2gig.band.management.TabbedBandActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.List;

public class MusicianConsoleFragment extends Fragment implements View.OnClickListener
{
    private List<DocumentSnapshot> performerAdverts;
    private List<DocumentSnapshot> musicianAdverts;
    private List<DocumentSnapshot> musicians;

    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final String USERID = fAuth.getUid();

    private final CollectionReference musicianReference = FSTORE.collection("musicians");
    private final Query getMusicians = musicianReference;

    private final CollectionReference performerAdvertsReference = FSTORE.collection("performer-listings");
    private final Query getPerformerAdverts = performerAdvertsReference;

    private final CollectionReference musicianAdvertsReference= FSTORE.collection("musician-listings");
    private final Query getMusicianAdverts = musicianAdvertsReference;

    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";

    private View view;

    private String musicianRef;
    private String performerReference;
    private String musicianAdvertReference;

    /**
     * Upon creation of the VenueConsoleFragment, create the fragment_venue_console layout.
     * @param inflater The inflater is used to read the passed xml file.
     * @param container The views base class.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     * @return Returns a View of the fragment_venue_console layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_musician_console, container, false);

        final Button noInternet = view.findViewById(R.id.noInternet);

        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        noInternet.setVisibility(isConnected ? View.GONE : View.VISIBLE);

        //General Section.
        final CardView card_view_view_venues = view.findViewById(R.id.card_view_view_Venues);
        final CardView card_view_view_bands = view.findViewById(R.id.card_view_view_Bands);
        final CardView card_view_edit_musician = view.findViewById(R.id.card_view_edit_musician);
        final CardView card_view_create_band = view.findViewById(R.id.card_view_create_band);


        card_view_view_venues.setOnClickListener(this);
        card_view_edit_musician.setOnClickListener(this);
        card_view_view_bands.setOnClickListener(this);
        card_view_create_band.setOnClickListener(this);

        //Performer Section.
        final CardView card_view_create_advert = view.findViewById(R.id.card_view_create_performer_advert);
        final CardView card_view_edit_advert = view.findViewById(R.id.card_view_edit_performer_advert);
        final CardView card_view_view_advert = view.findViewById(R.id.card_view_view_performer_advert);
        final CardView card_view_delete_advert = view.findViewById(R.id.card_view_delete_performer_advert);

        card_view_create_advert.setOnClickListener(this);
        card_view_edit_advert.setOnClickListener(this);
        card_view_view_advert.setOnClickListener(this);
        card_view_delete_advert.setOnClickListener(this);

        //Band Section

        final CardView card_view_create_band_musician_advert = view.findViewById(R.id.card_view_create_band_musician_advert);
        final CardView card_view_view_band_musician_advert = view.findViewById(R.id.card_view_view_band_musician_advert);
        final CardView card_view_edit_band_musician_advert = view.findViewById(R.id.card_view_edit_band_musician_advert);
        final CardView card_view_delete_band_musician_advert = view.findViewById(R.id.card_view_delete_band_musician_advert);

        card_view_create_band_musician_advert.setOnClickListener(this);
        card_view_view_band_musician_advert.setOnClickListener(this);
        card_view_edit_band_musician_advert.setOnClickListener(this);
        card_view_delete_band_musician_advert.setOnClickListener(this);

        if(!isConnected)
        {
            //General Section
            card_view_edit_musician.setAlpha(0.5f);
            card_view_create_band.setAlpha(0.5f);
            card_view_edit_musician.setClickable(false);
            card_view_create_band.setClickable(false);

            //Performer Section
            card_view_create_advert.setAlpha(0.5f);
            card_view_edit_advert.setAlpha(0.5f);
            card_view_delete_advert.setAlpha(0.5f);
            card_view_create_advert.setClickable(false);
            card_view_edit_advert.setClickable(false);
            card_view_delete_advert.setClickable(false);

            //Band Section
            card_view_create_band_musician_advert.setAlpha(0.5f);
            card_view_edit_band_musician_advert.setAlpha(0.5f);
            card_view_delete_band_musician_advert.setAlpha(0.5f);
            card_view_create_band_musician_advert.setClickable(false);
            card_view_edit_band_musician_advert.setClickable(false);
            card_view_delete_band_musician_advert.setClickable(false);
        }

        databaseQuery();

        return view;
    }

    /**
     * This method queries the database collecting all venues and venue adverts. These lists are then processed
     * to set up variables for possible button clicks where extras need to be sent with intents.
     */
    private void databaseQuery()
    {
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        getMusicians.whereEqualTo("user-ref", USERID).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                musicians = queryDocumentSnapshots.getDocuments();

                if(!musicians.isEmpty())
                {
                    musicianRef = musicians.get(0).getId();

                    DocumentSnapshot musician = musicians.get(0);

                    CardView editProfileLayout;
                    LinearLayout titleView;

                    titleView = view.findViewById(R.id.general_title);
                    titleView.setVisibility(View.VISIBLE);

                    editProfileLayout = view.findViewById(R.id.card_view_view_Bands);
                    editProfileLayout.setVisibility(View.VISIBLE);

                    editProfileLayout = view.findViewById(R.id.card_view_view_Venues);
                    editProfileLayout.setVisibility(View.VISIBLE);

                    editProfileLayout = view.findViewById(R.id.card_view_edit_musician);
                    editProfileLayout.setVisibility(View.VISIBLE);

                    editProfileLayout = view.findViewById(R.id.card_view_create_band);
                    editProfileLayout.setVisibility(View.VISIBLE);

                    getPerformerAdverts.whereEqualTo("performer-ref", musician.getId()).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            performerAdverts = queryDocumentSnapshots.getDocuments();

                            CardView editProfileLayout;
                            LinearLayout titleView;

                            titleView = view.findViewById(R.id.performer_advert_title);
                            titleView.setVisibility(View.VISIBLE);

                            if(!performerAdverts.isEmpty())
                            {
                                Log.d(TAG, "DATABASEQUERY PERFORMER ------------------ get successful with advert");

                                editProfileLayout = view.findViewById(R.id.card_view_edit_performer_advert);
                                editProfileLayout.setVisibility(View.VISIBLE);

                                editProfileLayout = view.findViewById(R.id.card_view_view_performer_advert);
                                editProfileLayout.setVisibility(View.VISIBLE);

                                editProfileLayout = view.findViewById(R.id.card_view_delete_performer_advert);
                                editProfileLayout.setVisibility(View.VISIBLE);

                                performerReference = performerAdverts.get(0).getId();
                            }
                            else
                            {
                                Log.d(TAG, "DATABASEQUERY PERFORMER ------------------ get successful without advert");

                                editProfileLayout = view.findViewById(R.id.card_view_create_performer_advert);
                                editProfileLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Log.d(TAG, e.toString());
                        }
                    });

                    getMusicianAdverts.whereEqualTo("musician-ref", musicianRef).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            musicianAdverts = queryDocumentSnapshots.getDocuments();

                            CardView editProfileLayout;
                            LinearLayout titleView;

                            titleView = view.findViewById(R.id.band_advert_title);
                            titleView.setVisibility(View.VISIBLE);

                            if(!musicianAdverts.isEmpty())
                            {
                                Log.d(TAG, "DATABASEQUERY MUSICIAN ------------------ get successful with advert");

                                editProfileLayout = view.findViewById(R.id.card_view_view_band_musician_advert);
                                editProfileLayout.setVisibility(View.VISIBLE);

                                editProfileLayout = view.findViewById(R.id.card_view_edit_band_musician_advert);
                                editProfileLayout.setVisibility(View.VISIBLE);

                                editProfileLayout = view.findViewById(R.id.card_view_delete_band_musician_advert);
                                editProfileLayout.setVisibility(View.VISIBLE);

                                musicianAdvertReference = musicianAdverts.get(0).getId();
                            }
                            else
                            {
                                Log.d(TAG, "DATABASEQUERY MUSICIAN ------------------ get successful without advert");

                                editProfileLayout = view.findViewById(R.id.card_view_create_band_musician_advert);
                                editProfileLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                else
                {
                    Log.d(TAG, "get successful without data");
                }
            }
        });
    }

    /**
     * This method determines the activity/fragment that will be created based upon the button clicked using the card view's tag.
     * @param v This is the detected button that has been clicked. Used to create the appropriate activity/fragment.
     */
    @Override
    public void onClick(View v)
    {
        switch(v.getTag().toString())
        {
            case "View Bands":
                startActivity(new Intent(getActivity(), BandAdvertIndexActivity.class).putExtra("CURRENT_USER_TYPE", "musicians"));
                break;
            case "View Venues":
                startActivity(new Intent(getActivity(), VenueAdvertIndexActivity.class).putExtra("CURRENT_USER_TYPE", "musicians"));
                break;
            case "Edit Musician":
                startActivity(new Intent(getActivity(), MusicianDetailsEditor.class).putExtra("EXTRA_MUSICIAN_ID", musicianRef));
                break;
            case "Create Band":
                startActivity(new Intent(getActivity(), TabbedBandActivity.class).putExtra("EXTRA_MUSICIAN_ID", musicianRef));
                break;
            case "Create Performer Advert":
                startActivityForResult(new Intent(getActivity(), PerformerAdvertisementEditor.class).putExtra("EXTRA_PERFORMER_ID", musicianRef)
                                                                                           .putExtra("EXTRA_LISTING_ID", "")
                                                                                           .putExtra("EXTRA_PERFORMER_TYPE", "Musician"), 1);
                break;
            case "Edit Performer Advert":
                startActivity(new Intent(getActivity(), PerformerAdvertisementEditor.class).putExtra("EXTRA_PERFORMER_ID", musicianRef)
                                                                                           .putExtra("EXTRA_LISTING_ID", performerReference)
                                                                                           .putExtra("EXTRA_PERFORMER_TYPE", "Musician"));
                break;
            case "View Performer Advert":
                startActivity(new Intent(getActivity(), PerformanceListingDetailsActivity.class).putExtra("EXTRA_PERFORMANCE_LISTING_ID", performerReference));
                break;
            case "Delete Performer Advert":
                deletePerformerAdvert();
                break;
            case "Create Band Musician Advert":
                startActivityForResult(new Intent(getActivity(), MusicianAdvertisementEditor.class).putExtra("EXTRA_MUSICIAN_ID", musicianRef)
                                                                                          .putExtra("EXTRA_LISTING_ID", ""),1);
                break;
            case "View Band Musician Advert":
                startActivity(new Intent(getActivity(), MusicianListingDetailsActivity.class).putExtra("EXTRA_MUSICIAN_LISTING_ID", musicianAdvertReference));
                break;
            case "Edit Band Musician Advert":
                startActivity(new Intent(getActivity(), MusicianAdvertisementEditor.class).putExtra("EXTRA_MUSICIAN_ID", musicianRef)
                                                                                          .putExtra("EXTRA_LISTING_ID", musicianAdvertReference));
                break;
            case "Delete Band Musician advert":
                deleteMusicianAdvert();
                break;
            default:
                break;
        }
    }

    /**
     * This method is used to find the logged in venue's advert and delete it from the database.
     */
    private void deletePerformerAdvert()
    {
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        getPerformerAdverts.whereEqualTo("performer-ref", musicianRef).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                performerAdverts = queryDocumentSnapshots.getDocuments();

                if(!performerAdverts.isEmpty())
                {
                    Log.d(TAG, "DELETEADVERT ------------------ get successful with advert");

                    performerAdvertsReference.document(performerAdverts.get(0).getId()).delete();
                    restartFragment();
                }
                else
                {
                    Log.d(TAG, "get successful without advert");
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d(TAG, e.toString());
            }
        });
    }

    private void deleteMusicianAdvert()
    {
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        getMusicianAdverts.whereEqualTo("musician-ref", musicianRef).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                musicianAdverts = queryDocumentSnapshots.getDocuments();

                if(!musicianAdverts.isEmpty())
                {
                    Log.d(TAG, "DELETEADVERT ------------------ get successful with advert");

                    musicianAdvertsReference.document(musicianAdverts.get(0).getId()).delete();
                    restartFragment();
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d(TAG, e.toString());
            }
        });
    }

    /**
     * This method is used to reload the fragment layout once the advert has been deleted.
     * This is so the appropriate layout is given to the user based upon whether they have an advert or not.
     */
    private void restartFragment()
    {
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        restartFragment();
    }
}
