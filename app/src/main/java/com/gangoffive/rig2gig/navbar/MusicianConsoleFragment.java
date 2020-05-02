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

/**
 * This class is used to create the musician console fragment.
 */
public class MusicianConsoleFragment extends Fragment implements View.OnClickListener
{
    private List<DocumentSnapshot> performerAdverts;
    private List<DocumentSnapshot> musicianAdverts;
    private List<DocumentSnapshot> musicians;

    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private final FirebaseAuth FAUTH = FirebaseAuth.getInstance();
    private final String USERID = FAUTH.getUid();

    private final CollectionReference MUSICIANREFERENCE = FSTORE.collection("musicians");
    private final Query GETMUSICIANS = MUSICIANREFERENCE;

    private final CollectionReference PERFORMERADVERTSREFERENCE = FSTORE.collection("performer-listings");
    private final Query GETPERFORMERADVERTS = PERFORMERADVERTSREFERENCE;

    private final CollectionReference MUSICIANADVERTSREFERENCE= FSTORE.collection("musician-listings");
    private final Query GETMUSICIANADVERTS = MUSICIANADVERTSREFERENCE;

    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";

    private View view;

    private String musicianRef;
    private String performerReference;
    private String musicianAdvertReference;

    /**
     * Upon creation of the MusicianConsoleFragment, create the fragment_musician_console layout.
     * @param inflater The inflater is used to read the passed xml file.
     * @param container The views base class.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     * @return Returns a View of the fragment_musician_console layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_musician_console, container, false);

        final Button NOINTERNET = view.findViewById(R.id.noInternet);

        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        NOINTERNET.setVisibility(isConnected ? View.GONE : View.VISIBLE);

        //General Section.
        final CardView CARD_VIEW_VIEW_VENUES = view.findViewById(R.id.card_view_view_Venues);
        final CardView CCARD_VIEW_VIEW_BANDS = view.findViewById(R.id.card_view_view_Bands);
        final CardView CARD_VIEW_EDIT_MUSICIAN = view.findViewById(R.id.card_view_edit_musician);
        final CardView CARD_VIEW_CREATE_BAND = view.findViewById(R.id.card_view_create_band);


        CARD_VIEW_VIEW_VENUES.setOnClickListener(this);
        CARD_VIEW_EDIT_MUSICIAN.setOnClickListener(this);
        CCARD_VIEW_VIEW_BANDS.setOnClickListener(this);
        CARD_VIEW_CREATE_BAND.setOnClickListener(this);

        //Performer Section.
        final CardView CARD_VIEW_CREATE_ADVERT = view.findViewById(R.id.card_view_create_performer_advert);
        final CardView CARD_VIEW_EDIT_ADVERT = view.findViewById(R.id.card_view_edit_performer_advert);
        final CardView CARD_VIEW_VIEW_ADVERT = view.findViewById(R.id.card_view_view_performer_advert);
        final CardView CARD_VIEW_DELETE_ADVERT = view.findViewById(R.id.card_view_delete_performer_advert);

        CARD_VIEW_CREATE_ADVERT.setOnClickListener(this);
        CARD_VIEW_EDIT_ADVERT.setOnClickListener(this);
        CARD_VIEW_VIEW_ADVERT.setOnClickListener(this);
        CARD_VIEW_DELETE_ADVERT.setOnClickListener(this);

        //Band Section

        final CardView CARD_VIEW_CREATE_BAND_MUSICIAN_ADVERT = view.findViewById(R.id.card_view_create_band_musician_advert);
        final CardView CARD_VIEW_VIEW_BAND_MUSICIAN_ADVERT = view.findViewById(R.id.card_view_view_band_musician_advert);
        final CardView CARD_VIEW_EDIT_BAND_MUSICIAN_ADVERT = view.findViewById(R.id.card_view_edit_band_musician_advert);
        final CardView CARD_VIEW_DELETE_BAND_MUSICIAN_ADVERT = view.findViewById(R.id.card_view_delete_band_musician_advert);

        CARD_VIEW_CREATE_BAND_MUSICIAN_ADVERT.setOnClickListener(this);
        CARD_VIEW_VIEW_BAND_MUSICIAN_ADVERT.setOnClickListener(this);
        CARD_VIEW_EDIT_BAND_MUSICIAN_ADVERT.setOnClickListener(this);
        CARD_VIEW_DELETE_BAND_MUSICIAN_ADVERT.setOnClickListener(this);

        if(!isConnected)
        {
            //General Section
            CARD_VIEW_EDIT_MUSICIAN.setAlpha(0.5f);
            CARD_VIEW_CREATE_BAND.setAlpha(0.5f);
            CARD_VIEW_EDIT_MUSICIAN.setClickable(false);
            CARD_VIEW_CREATE_BAND.setClickable(false);

            //Performer Section
            CARD_VIEW_CREATE_ADVERT.setAlpha(0.5f);
            CARD_VIEW_EDIT_ADVERT.setAlpha(0.5f);
            CARD_VIEW_DELETE_ADVERT.setAlpha(0.5f);
            CARD_VIEW_CREATE_ADVERT.setClickable(false);
            CARD_VIEW_EDIT_ADVERT.setClickable(false);
            CARD_VIEW_DELETE_ADVERT.setClickable(false);

            //Band Section
            CARD_VIEW_CREATE_BAND_MUSICIAN_ADVERT.setAlpha(0.5f);
            CARD_VIEW_EDIT_BAND_MUSICIAN_ADVERT.setAlpha(0.5f);
            CARD_VIEW_DELETE_BAND_MUSICIAN_ADVERT.setAlpha(0.5f);
            CARD_VIEW_CREATE_BAND_MUSICIAN_ADVERT.setClickable(false);
            CARD_VIEW_EDIT_BAND_MUSICIAN_ADVERT.setClickable(false);
            CARD_VIEW_DELETE_BAND_MUSICIAN_ADVERT.setClickable(false);
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

        GETMUSICIANS.whereEqualTo("user-ref", USERID).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            /**
             * This method is used to query Firebase.
             * @param queryDocumentSnapshots References the documents found in Firebase upon a successful query.
             */
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

                    GETPERFORMERADVERTS.whereEqualTo("performer-ref", musician.getId()).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        /**
                         * This method is used to query Firebase.
                         * @param queryDocumentSnapshots References the documents found in Firebase upon a successful query.
                         */
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

                    GETMUSICIANADVERTS.whereEqualTo("musician-ref", musicianRef).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                    {
                        /**
                         * This method is used to query Firebase.
                         * @param queryDocumentSnapshots References the documents found in Firebase upon a successful query.
                         */
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

        GETPERFORMERADVERTS.whereEqualTo("performer-ref", musicianRef).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            /**
             * This method is used to query Firebase.
             * @param queryDocumentSnapshots References the documents found in Firebase upon a successful query.
             */
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                performerAdverts = queryDocumentSnapshots.getDocuments();

                if(!performerAdverts.isEmpty())
                {
                    Log.d(TAG, "DELETEADVERT ------------------ get successful with advert");

                    PERFORMERADVERTSREFERENCE.document(performerAdverts.get(0).getId()).delete();
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
     * This method is used to delete the existing musician advert from Firebase.
     */
    private void deleteMusicianAdvert()
    {
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        GETMUSICIANADVERTS.whereEqualTo("musician-ref", musicianRef).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            /**
             * This method is used to query Firebase.
             * @param queryDocumentSnapshots References the documents found in Firebase upon a successful query.
             */
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                musicianAdverts = queryDocumentSnapshots.getDocuments();

                if(!musicianAdverts.isEmpty())
                {
                    Log.d(TAG, "DELETEADVERT ------------------ get successful with advert");

                    MUSICIANADVERTSREFERENCE.document(musicianAdverts.get(0).getId()).delete();
                    restartFragment();
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
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        restartFragment();
    }
}
