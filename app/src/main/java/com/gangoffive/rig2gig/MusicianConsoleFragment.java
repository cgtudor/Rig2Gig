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

public class MusicianConsoleFragment extends Fragment implements View.OnClickListener
{
    private List<DocumentSnapshot> performerAdverts;
    private List<DocumentSnapshot> musicians;

    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final String USERID = fAuth.getUid();

    private final CollectionReference musicianReference = FSTORE.collection("musicians");
    private final Query getMusicians = musicianReference;

    private final CollectionReference performerAdvertsReference = FSTORE.collection("performer-listings");
    private final Query getPerformerAdverts = performerAdvertsReference;

    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";

    private View view;

    private String musicianRef;
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
        view = inflater.inflate(R.layout.fragment_musician_console, container, false);

        final CardView card_view_view_venues = view.findViewById(R.id.card_view_view_Venues);
        final CardView card_view_edit_musician = view.findViewById(R.id.card_view_edit_musician);
        //final CardView card_view_my_bands = view.findViewById(R.id.card_view_my_bands);
        final CardView card_view_create_advert = view.findViewById(R.id.card_view_create_advert);
        final CardView card_view_edit_advert = view.findViewById(R.id.card_view_edit_advert);
        final CardView card_view_view_advert = view.findViewById(R.id.card_view_view_advert);
        final CardView card_view_delete_advert = view.findViewById(R.id.card_view_delete_advert);

        card_view_view_venues.setOnClickListener(this);
        card_view_edit_musician.setOnClickListener(this);
        //card_view_my_bands.setOnClickListener(this);
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

        getMusicians.whereEqualTo("user-ref", USERID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                musicians = queryDocumentSnapshots.getDocuments();

                if(!musicians.isEmpty())
                {
                    musicianRef = musicians.get(0).getId();

                    DocumentSnapshot musician = musicians.get(0);

                    getPerformerAdverts.whereEqualTo("performer-ref", musician.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                        {
                            performerAdverts = queryDocumentSnapshots.getDocuments();

                            CardView editProfileLayout;

                            if(!performerAdverts.isEmpty())
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

                                //editProfileLayout = view.findViewById(R.id.card_view_my_bands);
                                //editProfileLayout.setVisibility(View.VISIBLE);

                                editProfileLayout = view.findViewById(R.id.card_view_create_advert);
                                editProfileLayout.setVisibility(View.GONE);

                                performerReference = performerAdverts.get(0).getId();
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

                                //editProfileLayout = view.findViewById(R.id.card_view_my_bands);
                                //editProfileLayout.setVisibility(View.VISIBLE);
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
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewVenuesFragment()).commit();
                break;
            case "Edit Musician":
                /*startActivity(new Intent(getActivity(), MusicianDetailsEditor.class).putExtra("EXTRA_MUSICIAN_ID", musicianRef));*/
                break;
            case "My Bands":
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new DisplayMusiciansBands()).commit();
                break;
            case "Create Advert":
                /*startActivity(new Intent(getActivity(), PerformerAdvertisementEditor.class).putExtra("EXTRA_PERFORMER_ID", performerReference)
                                                                                          .putExtra("EXTRA_LISTING_ID", "")
                                                                                          .putExtra("EXTRA_PERFORMER_TYPE", "Musician"));*/
                break;
            case "Edit Advert":
                /*startActivity(new Intent(getActivity(), PerformerAdvertisementEditor.class).putExtra("EXTRA_PERFORMER_ID", performerReference)
                                                                                      .putExtra("EXTRA_LISTING_ID", musicianRef)
                                                                                      .putExtra("EXTRA_PERFORMER_TYPE", "Musician"));*/
                break;
            case "View Advert":
                /*startActivity(new Intent(getActivity(), PerformanceListingDetailsActivity.class).putExtra("EXTRA_PERFORMANCE_LISTING_ID", performerReference));*/
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
        getPerformerAdverts.whereEqualTo("performer-ref", musicianRef).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                performerAdverts = queryDocumentSnapshots.getDocuments();

                if(!performerAdverts.isEmpty())
                {
                    Log.d(TAG, "DELETEADVERT ------------------ get successful with advert");

                    //performerAdvertsReference.document(performerAdverts.get(0).getId()).delete();
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
