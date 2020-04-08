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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.advert.details.VenueListingDetailsActivity;
import com.gangoffive.rig2gig.advert.index.PerformerAdvertIndexActivity;
import com.gangoffive.rig2gig.advert.management.VenueAdvertisementEditor;
import com.gangoffive.rig2gig.venue.management.VenueDetailsEditor;
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

        final Button noInternet = view.findViewById(R.id.noInternet);

        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        noInternet.setVisibility(isConnected ? View.GONE : View.VISIBLE);

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

        if(!isConnected)
        {
            card_view_edit_venue.setAlpha(0.5f);
            card_view_create_advert.setAlpha(0.5f);
            card_view_edit_advert.setAlpha(0.5f);
            card_view_delete_advert.setAlpha(0.5f);
            card_view_edit_venue.setClickable(false);
            card_view_create_advert.setClickable(false);
            card_view_edit_advert.setClickable(false);
            card_view_delete_advert.setClickable(false);
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

        getVenues.whereEqualTo("user-ref", USERID).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                venues = queryDocumentSnapshots.getDocuments();

                if(!venues.isEmpty())
                {
                    venueRef = venues.get(0).getId();

                    DocumentSnapshot venue = venues.get(0);

                    getVenueAdverts.whereEqualTo("venue-ref", venue.getId()).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            venueAdverts = queryDocumentSnapshots.getDocuments();

                            CardView editProfileLayout;

                            if(!venueAdverts.isEmpty())
                            {
                                Log.d(TAG, "DATABASEQUERY ------------------ get successful with advert");

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

                                editProfileLayout = view.findViewById(R.id.card_view_create_advert);
                                editProfileLayout.setVisibility(View.GONE);

                                advertReference = venueAdverts.get(0).getId();
                            }
                            else
                            {
                                Log.d(TAG, "get successful without advert");

                                editProfileLayout = view.findViewById(R.id.card_view_edit_advert);
                                editProfileLayout.setVisibility(View.GONE);

                                editProfileLayout = view.findViewById(R.id.card_view_view_advert);
                                editProfileLayout.setVisibility(View.GONE);

                                editProfileLayout = view.findViewById(R.id.card_view_delete_advert);
                                editProfileLayout.setVisibility(View.GONE);

                                editProfileLayout = view.findViewById(R.id.card_view_view_performers);
                                editProfileLayout.setVisibility(View.VISIBLE);

                                editProfileLayout = view.findViewById(R.id.card_view_edit_venue);
                                editProfileLayout.setVisibility(View.VISIBLE);

                                editProfileLayout = view.findViewById(R.id.card_view_create_advert);
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
                startActivity(new Intent(getActivity(), PerformerAdvertIndexActivity.class));
                break;
            case "Edit Venue":
                startActivity(new Intent(getActivity(), VenueDetailsEditor.class).putExtra("EXTRA_VENUE_ID", venueRef));
                break;
            case "Create Advert":
                startActivity(new Intent(getActivity(), VenueAdvertisementEditor.class).putExtra("EXTRA_VENUE_ID", venueRef).putExtra("EXTRA_LISTING_ID", ""));
                break;
            case "Edit Advert":
                startActivity(new Intent(getActivity(), VenueAdvertisementEditor.class).putExtra("EXTRA_VENUE_ID", venueRef).putExtra("EXTRA_LISTING_ID", advertReference));
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
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        getVenueAdverts.whereEqualTo("venue-ref", venueRef).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                venueAdverts = queryDocumentSnapshots.getDocuments();

                if(!venueAdverts.isEmpty())
                {
                    Log.d(TAG, "DELETEADVERT ------------------ get successful with advert");

                    venueAdvertsReference.document(venueAdverts.get(0).getId()).delete();
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

    /**
     * This method is used to reload the fragment layout once the advert has been deleted.
     * This is so the appropriate layout is given to the user based upon whether they have an advert or not.
     */
    private void restartFragment()
    {
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}
