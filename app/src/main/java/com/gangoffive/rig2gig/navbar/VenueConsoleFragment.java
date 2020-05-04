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

/**
 * This class is used to create the venue console fragment.
 * @author Ben souch
 * @version #0.3b
 * @since #0.1b
 */
public class VenueConsoleFragment extends Fragment implements View.OnClickListener
{
    private List<DocumentSnapshot> venueAdverts;
    private List<DocumentSnapshot> venues;

    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private final FirebaseAuth FAUTH = FirebaseAuth.getInstance();
    private final String USERID = FAUTH.getUid();

    private final CollectionReference VENUEREFERENCE = FSTORE.collection("venues");
    private final Query GETVENUES = VENUEREFERENCE;

    private final CollectionReference VENUEADVERTSREFERENCE = FSTORE.collection("venue-listings");
    private final Query GETVENUEADVERTS = VENUEADVERTSREFERENCE;

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
     * @since #0.1b
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_venue_console, container, false);

        final Button NOINTERNET = view.findViewById(R.id.noInternet);

        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        NOINTERNET.setVisibility(isConnected ? View.GONE : View.VISIBLE);

        final CardView CARD_VIEW_VIEW_PERFORMERS = view.findViewById(R.id.card_view_view_performers);
        final CardView CARD_VIEW_EDIT_VENUE = view.findViewById(R.id.card_view_edit_venue);
        final CardView CARD_VIEW_CREATE_ADVERT = view.findViewById(R.id.card_view_create_advert);
        final CardView CARD_VIEW_EDIT_ADVERT = view.findViewById(R.id.card_view_edit_advert);
        final CardView CARD_VIEW_VIEW_ADVERT = view.findViewById(R.id.card_view_view_advert);
        final CardView CARD_VIEW_DELETE_ADVERT = view.findViewById(R.id.card_view_delete_advert);

        CARD_VIEW_VIEW_PERFORMERS.setOnClickListener(this);
        CARD_VIEW_EDIT_VENUE.setOnClickListener(this);
        CARD_VIEW_CREATE_ADVERT.setOnClickListener(this);
        CARD_VIEW_EDIT_ADVERT.setOnClickListener(this);
        CARD_VIEW_VIEW_ADVERT.setOnClickListener(this);
        CARD_VIEW_DELETE_ADVERT.setOnClickListener(this);

        if(!isConnected)
        {
            CARD_VIEW_EDIT_VENUE.setAlpha(0.5f);
            CARD_VIEW_CREATE_ADVERT.setAlpha(0.5f);
            CARD_VIEW_EDIT_ADVERT.setAlpha(0.5f);
            CARD_VIEW_DELETE_ADVERT.setAlpha(0.5f);
            CARD_VIEW_EDIT_VENUE.setClickable(false);
            CARD_VIEW_CREATE_ADVERT.setClickable(false);
            CARD_VIEW_EDIT_ADVERT.setClickable(false);
            CARD_VIEW_DELETE_ADVERT.setClickable(false);
        }

        databaseQuery();

        return view;
    }

    /**
     * This method queries the database collecting all venues and venue adverts. These lists are then processed
     * to set up variables for possible button clicks where extras need to be sent with intents.
     * @since #0.1b
     */
    private void databaseQuery()
    {

        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        GETVENUES.whereEqualTo("user-ref", USERID).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            /**
             * This method is used to query Firebase.
             * @param queryDocumentSnapshots References the documents found in Firebase upon a successful query.
             */
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                venues = queryDocumentSnapshots.getDocuments();

                if(!venues.isEmpty())
                {
                    venueRef = venues.get(0).getId();

                    DocumentSnapshot venue = venues.get(0);

                    GETVENUEADVERTS.whereEqualTo("venue-ref", venue.getId()).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        /**
                         * This method is used to query Firebase.
                         * @param queryDocumentSnapshots References the documents found in Firebase upon a successful query.
                         */
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
                        /**
                         * Upon failure when querying the database, display the error to the console.
                         * @param e Represents the exception that has occurred.
                         */
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
     * @since #0.1b
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
                startActivityForResult(new Intent(getActivity(), VenueAdvertisementEditor.class).putExtra("EXTRA_VENUE_ID", venueRef)
                                                                                                .putExtra("EXTRA_LISTING_ID", ""), 1);
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
     * @since #0.1b
     */
    private void deleteAdvert()
    {
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        GETVENUEADVERTS.whereEqualTo("venue-ref", venueRef).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            /**
             * This method is used to query Firebase.
             * @param queryDocumentSnapshots References the documents found in Firebase upon a successful query.
             */
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                venueAdverts = queryDocumentSnapshots.getDocuments();

                if(!venueAdverts.isEmpty())
                {
                    Log.d(TAG, "DELETEADVERT ------------------ get successful with advert");

                    VENUEADVERTSREFERENCE.document(venueAdverts.get(0).getId()).delete();
                    restartFragment();
                }
                else
                {
                    Log.d(TAG, "get successful without advert");
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            /**
             * Upon failure when querying the database, display the error to the console.
             * @param e Represents the exception that has occurred.
             */
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
     * @since #0.1b
     */
    private void restartFragment()
    {
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    /**
     * Upon starting an activity awaiting a result, this method handles what this activity does upon receiving such a result.
     * @param requestCode Represents the request code sent by the starting activity.
     * @param resultCode Represents the result code.
     * @param data Represents the intent passed back from the completed activity.
     * @since #0.1b
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        restartFragment();
    }
}
