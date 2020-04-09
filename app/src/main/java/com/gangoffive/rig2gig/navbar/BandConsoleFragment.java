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
import com.gangoffive.rig2gig.advert.index.VenueAdvertIndexActivity;
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

public class BandConsoleFragment extends Fragment implements View.OnClickListener
{
    private List<DocumentSnapshot> bandAdverts;
    private List<DocumentSnapshot> bands;

    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final String USERID = fAuth.getUid();

    private final CollectionReference bandReference = FSTORE.collection("bands");
    private final Query getBands = bandReference;

    private final CollectionReference bandAdvertsReference = FSTORE.collection("band-listings");
    private final Query getBandAdverts = bandAdvertsReference;

    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";

    private View view;

    private String bandRef;
    private String displayMusicianBandsReference;
    private String performerReference;

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
        view = inflater.inflate(R.layout.activity_band_console, container, false);

        final Button noInternet = view.findViewById(R.id.noInternet);

        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        noInternet.setVisibility(isConnected ? View.GONE : View.VISIBLE);

        final CardView card_view_view_venues = view.findViewById(R.id.card_view_view_Venues);
        final CardView card_view_edit_band = view.findViewById(R.id.card_view_edit_musician);
        //final CardView card_view_invite_musicians = view.findViewById(R.id.card_view_my_bands);
        final CardView card_view_create_advert = view.findViewById(R.id.card_view_create_advert);
        final CardView card_view_edit_advert = view.findViewById(R.id.card_view_edit_advert);
        final CardView card_view_view_advert = view.findViewById(R.id.card_view_view_advert);
        final CardView card_view_delete_advert = view.findViewById(R.id.card_view_delete_advert);

        card_view_view_venues.setOnClickListener(this);
        card_view_edit_band.setOnClickListener(this);
        //card_view_invite_musicians.setOnClickListener(this);
        card_view_create_advert.setOnClickListener(this);
        card_view_edit_advert.setOnClickListener(this);
        card_view_view_advert.setOnClickListener(this);
        card_view_delete_advert.setOnClickListener(this);

        if(!isConnected)
        {
            card_view_create_advert.setAlpha(0.5f);
            card_view_edit_advert.setAlpha(0.5f);
            card_view_delete_advert.setAlpha(0.5f);
            card_view_edit_band.setAlpha(0.5f);
            card_view_create_advert.setClickable(false);
            card_view_edit_advert.setClickable(false);
            card_view_delete_advert.setClickable(false);
            card_view_edit_band.setClickable(false);
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

        getBands.whereEqualTo("user-ref", USERID).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                bands = queryDocumentSnapshots.getDocuments();

                if(!bands.isEmpty())
                {
                    bandRef = bands.get(0).getId();

                    DocumentSnapshot band = bands.get(0);

                    getBandAdverts.whereEqualTo("performer-ref", band.getId()).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            bandAdverts = queryDocumentSnapshots.getDocuments();

                            CardView editProfileLayout;

                            if(!bandAdverts.isEmpty())
                            {
                                Log.d(TAG, "DATABASEQUERY ------------------ get successful with advert");


                                editProfileLayout = view.findViewById(R.id.card_view_view_Venues);
                                editProfileLayout.setVisibility(View.VISIBLE);

                                editProfileLayout = view.findViewById(R.id.card_view_edit_musician);
                                editProfileLayout.setVisibility(View.VISIBLE);

                                editProfileLayout = view.findViewById(R.id.card_view_edit_advert);
                                editProfileLayout.setVisibility(View.VISIBLE);

                                editProfileLayout = view.findViewById(R.id.card_view_view_advert);
                                editProfileLayout.setVisibility(View.VISIBLE);

                                editProfileLayout = view.findViewById(R.id.card_view_delete_advert);
                                editProfileLayout.setVisibility(View.VISIBLE);

                                editProfileLayout = view.findViewById(R.id.card_view_create_advert);
                                editProfileLayout.setVisibility(View.GONE);

                                performerReference = bandAdverts.get(0).getId();
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

                                editProfileLayout = view.findViewById(R.id.card_view_view_Venues);
                                editProfileLayout.setVisibility(View.VISIBLE);

                                editProfileLayout = view.findViewById(R.id.card_view_edit_musician);
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
        System.out.println(v.getTag().toString());
        switch(v.getTag().toString())
        {
            case "View Venues":
                startActivity(new Intent(getActivity(), VenueAdvertIndexActivity.class));
                break;
            case "Edit Musician":
                //To be implemented
                break;
            case "Invite Musician":
                //To be implemented
                break;
            case "Create Advert":
                //To be implemented
                break;
            case "Edit Advert":
                //To be implemented
                break;
            case "View Advert":
                //To be implemented
                break;
            case "Delete Advert":
                //deleteAdvert();
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

        getBandAdverts.whereEqualTo("performer-ref", bandRef).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                bandAdverts = queryDocumentSnapshots.getDocuments();

                if(!bandAdverts.isEmpty())
                {
                    Log.d(TAG, "DELETEADVERT ------------------ get successful with advert");

                    bandAdvertsReference.document(bandAdverts.get(0).getId()).delete();
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
