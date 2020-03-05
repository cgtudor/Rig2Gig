package com.gangoffive.rig2gig;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class VenueConsoleFragment extends Fragment implements View.OnClickListener
{
    private List<DocumentSnapshot> venueAdverts;
    private List<DocumentSnapshot> venues;

    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final String USERID = fAuth.getUid();

    private final CollectionReference venueReference = FSTORE.collection("venues");
    private final Query getVenues = venueReference;

    private final CollectionReference venueAdvertsReference = FSTORE.collection("venue-listings");
    private final Query getVenueAdverts = venueAdvertsReference;

    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";

    private View view;

    private String venueRef;
    private String advertReference;

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
        view = inflater.inflate(R.layout.fragment_venue_console, container, false);

        final CardView card_view_view_performers = view.findViewById(R.id.card_view_view_performers);
        final CardView card_view_edit_venue = view.findViewById(R.id.card_view_edit_venue);
        final CardView card_view_create_advert = view.findViewById(R.id.card_view_create_advert);
        final CardView card_view_edit_advert = view.findViewById(R.id.card_view_edit_advert);
        final CardView card_view_view_advert = view.findViewById(R.id.card_view_view_advert);
        final CardView card_view_delete_advert = view.findViewById(R.id.card_view_delete_advert);

        card_view_view_performers.setOnClickListener(this);
        card_view_edit_venue.setOnClickListener(this);
        card_view_create_advert.setOnClickListener(this);
        card_view_edit_advert.setOnClickListener(this);
        card_view_view_advert.setOnClickListener(this);
        card_view_delete_advert.setOnClickListener(this);

        databaseQuery();

        return view;
    }

    /**
     * This method queries the database collecting all venues and venue adverts. These lists are then processed
     * to set up variables for possible button clicks where extras need to be sent with intents.
     */
    private void databaseQuery()
    {

        getVenues.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                venues = queryDocumentSnapshots.getDocuments();

                if(!venues.isEmpty())
                {
                    for(DocumentSnapshot venue : venues)
                    {
                        if(venue.get("user-ref").equals(USERID))
                        {
                            venueRef = venue.getId();

                            getVenueAdverts.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                            {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                                {
                                    venueAdverts = queryDocumentSnapshots.getDocuments();

                                    if(!venueAdverts.isEmpty())
                                    {
                                        Log.d(TAG, "DATABASEQUERY ------------------ get successful with data");

                                        for(DocumentSnapshot adverts : venueAdverts)
                                        {
                                            CardView editProfileLayout;

                                            if(adverts.get("venue-ref").toString().equals(venueRef))
                                            {
                                                editProfileLayout = view.findViewById(R.id.card_view_view_performers);
                                                editProfileLayout.setVisibility(View.VISIBLE);

                                                editProfileLayout = view.findViewById(R.id.card_view_edit_venue);
                                                editProfileLayout.setVisibility(View.VISIBLE);

                                                editProfileLayout = view.findViewById(R.id.card_view_edit_advert);
                                                editProfileLayout.setVisibility(View.VISIBLE);

                                                editProfileLayout = view.findViewById(R.id.card_view_view_advert);
                                                editProfileLayout.setVisibility(View.VISIBLE);

                                                editProfileLayout = view.findViewById(R.id.card_view_delete_advert);
                                                editProfileLayout.setVisibility(View.VISIBLE);

                                                advertReference = adverts.getId();
                                                break;
                                            }
                                            else
                                            {
                                                editProfileLayout = view.findViewById(R.id.card_view_view_performers);
                                                editProfileLayout.setVisibility(View.VISIBLE);

                                                editProfileLayout = view.findViewById(R.id.card_view_edit_venue);
                                                editProfileLayout.setVisibility(View.VISIBLE);

                                                editProfileLayout = view.findViewById(R.id.card_view_create_advert);
                                                editProfileLayout.setVisibility(View.VISIBLE);
                                                break;
                                            }
                                        }
                                    }
                                    else
                                    {
                                        Log.d(TAG, "get successful without data");
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
                            break;
                        }
                    }
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
            case "View Performers":
                //Fragment ViewPerformersFragment
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewPerformersFragment()).commit();
                break;
            case "Edit Venue":
                //Chris 2.0 needs to implement.
                //startActivity(new Intent(getActivity(), VenueDetailsEditor.class).putExtra("EXTRA_VENUE_ID", venueRef));
                break;
            case "Create Advert":
                //startActivity(new Intent(getActivity(), VenueAdvertisementEditor.class).putExtra("EXTRA_VENUE__ID", venueRef).putExtra("EXTRA_LISTING_ID", ""));
                break;
            case "Edit Advert":
                //Chris 2.0 needs to implement.
                //startActivity(new Intent(getActivity(), VenueAdvertisementEditor.class).putExtra("EXTRA_VENUE__ID", venueRef).putExtra("EXTRA_LISTING_ID", advertReference));
                break;
            case "View Advert":
                startActivity(new Intent(getActivity(), VenueListingDetailsActivity.class).putExtra("EXTRA_VENUE_LISTING_ID", advertReference));
                break;
            case "Delete Advert":
                deleteAdvert();
                break;
            default:
                break;
        }
    }

    /**
     * This method is used to find the logged in venue's advert and delete it from the database.
     */
    private void deleteAdvert()
    {
        getVenueAdverts.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                venueAdverts = queryDocumentSnapshots.getDocuments();

                if(!venueAdverts.isEmpty())
                {
                    Log.d(TAG, "DELETEADVERT ------------------ get successful with data");

                    for(DocumentSnapshot adverts : venueAdverts)
                    {
                        if(adverts.get("venue-ref").toString().equals(venueRef))
                        {
                            venueAdvertsReference.document(adverts.getId()).delete();
                            restartFragment();
                            break;
                        }
                    }
                }
                else
                {
                    Log.d(TAG, "get successful without data");
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
}
