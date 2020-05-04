package com.gangoffive.rig2gig.navbar;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.gangoffive.rig2gig.advert.management.BandAdvertisementEditor;
import com.gangoffive.rig2gig.band.management.ManageBandMembersActivity;
import com.gangoffive.rig2gig.advert.management.PerformerAdvertisementEditor;
import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.advert.details.BandListingDetailsActivity;
import com.gangoffive.rig2gig.advert.details.PerformanceListingDetailsActivity;
import com.gangoffive.rig2gig.advert.index.MusicianAdvertIndexActivity;
import com.gangoffive.rig2gig.advert.index.VenueAdvertIndexActivity;
import com.gangoffive.rig2gig.band.management.BandDetailsEditor;
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
 * This class is used to display the band console activity.
 * @author Ben souch
 * @version #0.3b
 * @since #0.2b
 */
public class BandConsoleActivity extends AppCompatActivity implements View.OnClickListener
{
    private List<DocumentSnapshot> bandAdverts;
    private List<DocumentSnapshot> performerAdverts;
    private List<DocumentSnapshot> bands;

    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();

    private final CollectionReference BANDADVERTSREFERENCE = FSTORE.collection("band-listings");
    private final Query GETBANDADVERTS = BANDADVERTSREFERENCE;

    private final CollectionReference PERFORMERADVERTSREFERENCE = FSTORE.collection("performer-listings");
    private final Query GETPERFORMERADVERTS = PERFORMERADVERTSREFERENCE;

    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";

    private String bandRef;
    private String displayMusicianBandsReference; //The band reference.
    private String bandName;
    private String performerReference; //The band's performer advert.

    private final OnSuccessListener performerSuccessListener = new OnSuccessListener<QuerySnapshot>()
    {
        /**
         * This method is used to query Firebase.
         * @param queryDocumentSnapshots References the documents found in Firebase upon a successful query.
         * @since #0.2b
         */
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
        {
            performerAdverts = queryDocumentSnapshots.getDocuments();

            CardView editProfileLayout;
            LinearLayout textView;

            textView = findViewById(R.id.performer_advert_title);
            textView.setVisibility(View.VISIBLE);

            if(!performerAdverts.isEmpty())
            {
                Log.d(TAG, "DATABASEQUERY PERFORMER ------------------ get successful with advert");

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        CardView editProfileLayout;

                        editProfileLayout = findViewById(R.id.card_view_edit_performer_advert);
                        editProfileLayout.setVisibility(View.VISIBLE);

                        editProfileLayout = findViewById(R.id.card_view_view_performer_advert);
                        editProfileLayout.setVisibility(View.VISIBLE);

                        editProfileLayout = findViewById(R.id.card_view_delete_performer_advert);
                        editProfileLayout.setVisibility(View.VISIBLE);
                    }
                });

                performerReference = performerAdverts.get(0).getId();
            }
            else
            {
                Log.d(TAG, "DATABASEQUERY PERFORMER ------------------ get successful without advert");

                editProfileLayout = findViewById(R.id.card_view_create_performer_advert);
                editProfileLayout.setVisibility(View.VISIBLE);
            }
        }
    };

    private final OnFailureListener failureListener = new OnFailureListener()
    {
        /**
         * Upon failure when querying the database, display the error to the console.
         * @param e Represents the exception that has occurred.
         * @since #0.2b
         */
        @Override
        public void onFailure(@NonNull Exception e)
        {
            Log.d(TAG, e.toString());
        }
    };

    private final OnSuccessListener bandSuccessListener = new OnSuccessListener<QuerySnapshot>()
    {
        /**
         * This method is used to query Firebase.
         * @param queryDocumentSnapshots References the documents found in Firebase upon a successful query.
         * @since #0.2b
         */
        @Override
        public void onSuccess(QuerySnapshot queryDocumentSnapshots)
        {
            bandAdverts = queryDocumentSnapshots.getDocuments();

            CardView editProfileLayout;
            LinearLayout textView;

            textView = findViewById(R.id.band_advert_title);
            textView.setVisibility(View.VISIBLE);

            if(!bandAdverts.isEmpty())
            {
                Log.d(TAG, "DATABASEQUERY BAND ------------------ get successful with advert");

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        CardView editProfileLayout;

                        editProfileLayout = findViewById(R.id.card_view_view_band_advert);
                        editProfileLayout.setVisibility(View.VISIBLE);

                        editProfileLayout = findViewById(R.id.card_view_edit_band_band_advert);
                        editProfileLayout.setVisibility(View.VISIBLE);

                        editProfileLayout = findViewById(R.id.card_view_delete_band_advert);
                        editProfileLayout.setVisibility(View.VISIBLE);
                    }
                });

                bandRef = bandAdverts.get(0).getId();
            }
            else
            {
                Log.d(TAG, "DATABASEQUERY BAND ------------------ get successful with advert");

                editProfileLayout = findViewById(R.id.card_view_create_band_advert);
                editProfileLayout.setVisibility(View.VISIBLE);
            }
        }
    };

    /**
     * Upon creation of the BandConsoleActivity, create the activity_band_console layout.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     * @return Returns a View of the activity_band_console layout.
     * @since #0.2b
     */
    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_console);

        Intent intent = getIntent();
        displayMusicianBandsReference = intent.getStringExtra("EXTRA_SELECTED_BAND_ID");
        bandName = intent.getStringExtra("EXTRA_SELECTED_BAND_NAME");

        final Button NOINTERNET = findViewById(R.id.noInternet);

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        NOINTERNET.setVisibility(isConnected ? View.GONE : View.VISIBLE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(bandName);

        //General Section
        final CardView CARD_VIEW_VIEW_VENUES = findViewById(R.id.card_view_view_Venues);
        final CardView CARD_VIEW_VIEW_MUSICIANS = findViewById(R.id.card_view_view_musicians);
        final CardView CARD_VIEW_EDIT_BAND = findViewById(R.id.card_view_edit_band);
        final CardView CARD_VIEW_MANAGE_MEMBERS = findViewById(R.id.card_view_manage_members);

        CARD_VIEW_VIEW_VENUES.setOnClickListener(this);
        CARD_VIEW_EDIT_BAND.setOnClickListener(this);
        CARD_VIEW_VIEW_MUSICIANS.setOnClickListener(this);
        CARD_VIEW_MANAGE_MEMBERS.setOnClickListener(this);

        //Performer Advert Section
        final CardView CARD_VIEW_CREATE_ADVERT = findViewById(R.id.card_view_create_performer_advert);
        final CardView CARD_VIEW_EDIT_ADVERT = findViewById(R.id.card_view_edit_performer_advert);
        final CardView CARD_VIEW_VIEW_ADVERT = findViewById(R.id.card_view_view_performer_advert);
        final CardView CARD_VIEW_DELETE_ADVERT = findViewById(R.id.card_view_delete_performer_advert);


        CARD_VIEW_CREATE_ADVERT.setOnClickListener(this);
        CARD_VIEW_EDIT_ADVERT.setOnClickListener(this);
        CARD_VIEW_VIEW_ADVERT.setOnClickListener(this);
        CARD_VIEW_DELETE_ADVERT.setOnClickListener(this);

        //Band Advert Section

        final CardView CARD_VIEW_CREATE_BAND_ADVERT = findViewById(R.id.card_view_create_band_advert);
        final CardView CARD_VIEW_VIEW_BAND_ADVERT = findViewById(R.id.card_view_view_band_advert);
        final CardView CARD_VIEW_EDIT_BAND_BAND_ADVERT = findViewById(R.id.card_view_edit_band_band_advert);
        final CardView CARD_VIEW_DELETE_BAND_ADVERT = findViewById(R.id.card_view_delete_band_advert);


        CARD_VIEW_CREATE_BAND_ADVERT.setOnClickListener(this);
        CARD_VIEW_VIEW_BAND_ADVERT.setOnClickListener(this);
        CARD_VIEW_EDIT_BAND_BAND_ADVERT.setOnClickListener(this);
        CARD_VIEW_DELETE_BAND_ADVERT.setOnClickListener(this);

        if(!isConnected)
        {
            //General Section
            CARD_VIEW_EDIT_BAND.setAlpha(0.5f);
            CARD_VIEW_MANAGE_MEMBERS.setAlpha(0.5f);
            CARD_VIEW_EDIT_BAND.setClickable(false);
            CARD_VIEW_MANAGE_MEMBERS.setClickable(false);

            //Performer Section
            CARD_VIEW_CREATE_ADVERT.setAlpha(0.5f);
            CARD_VIEW_EDIT_ADVERT.setAlpha(0.5f);
            CARD_VIEW_DELETE_ADVERT.setAlpha(0.5f);
            CARD_VIEW_CREATE_ADVERT.setClickable(false);
            CARD_VIEW_EDIT_ADVERT.setClickable(false);
            CARD_VIEW_DELETE_ADVERT.setClickable(false);

            //Band Section
            CARD_VIEW_CREATE_BAND_ADVERT.setAlpha(0.5f);
            CARD_VIEW_EDIT_BAND_BAND_ADVERT.setAlpha(0.5f);
            CARD_VIEW_DELETE_BAND_ADVERT.setAlpha(0.5f);
            CARD_VIEW_CREATE_BAND_ADVERT.setClickable(false);
            CARD_VIEW_EDIT_BAND_BAND_ADVERT.setClickable(false);
            CARD_VIEW_DELETE_BAND_ADVERT.setClickable(false);
        }

        //Setup layout
        databaseQuery();
    }

    /**
     * This method queries the database collecting all venues and venue adverts. These lists are then processed
     * to set up variables for possible button clicks where extras need to be sent with intents.
     * @since #0.2b
     */
    private void databaseQuery()
    {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        CardView editProfileLayout;
        LinearLayout textView;

        textView = findViewById(R.id.general_title);
        textView.setVisibility(View.VISIBLE);

        editProfileLayout = findViewById(R.id.card_view_view_Venues);
        editProfileLayout.setVisibility(View.VISIBLE);

        editProfileLayout = findViewById(R.id.card_view_view_musicians);
        editProfileLayout.setVisibility(View.VISIBLE);

        editProfileLayout = findViewById(R.id.card_view_edit_band);
        editProfileLayout.setVisibility(View.VISIBLE);

        editProfileLayout = findViewById(R.id.card_view_manage_members);
        editProfileLayout.setVisibility(View.VISIBLE);

        GETPERFORMERADVERTS.whereEqualTo("performer-ref", displayMusicianBandsReference).get(source).addOnSuccessListener(performerSuccessListener).addOnFailureListener(failureListener);

        GETBANDADVERTS.whereEqualTo("band-ref", displayMusicianBandsReference).get(source).addOnSuccessListener(bandSuccessListener);
    }

    /**
     * This method determines the activity/fragment that will be created based upon the button clicked using the card view's tag.
     * @param v This is the detected button that has been clicked. Used to create the appropriate activity/fragment.
     * @since #0.2b
     */
    @Override
    public void onClick(View v)
    {
        System.out.println(v.getTag().toString());
        switch(v.getTag().toString())
        {
            case "View Venues":
                startActivity(new Intent(this, VenueAdvertIndexActivity.class).putExtra("CURRENT_USER_TYPE", "bands")
                                                                                             .putExtra("CURRENT_BAND_ID", displayMusicianBandsReference));
                break;
            case "Create Performer Advert":
                startActivityForResult(new Intent(this, PerformerAdvertisementEditor.class).putExtra("EXTRA_PERFORMER_ID", displayMusicianBandsReference)
                                                                                                          .putExtra("EXTRA_LISTING_ID", "")
                                                                                                          .putExtra("EXTRA_PERFORMER_TYPE", "Band"), 1);
                break;
            case "Manage Members":
                startActivity(new Intent(this, ManageBandMembersActivity.class).putExtra("EXTRA_BAND_ID", displayMusicianBandsReference));
                break;
            case "Edit Performer Advert":
                startActivity(new Intent(this, PerformerAdvertisementEditor.class).putExtra("EXTRA_PERFORMER_ID", displayMusicianBandsReference)
                                                                                                 .putExtra("EXTRA_LISTING_ID", performerReference)
                                                                                                 .putExtra("EXTRA_PERFORMER_TYPE", "Band"));
                break;
            case "View Performer Advert":
                startActivity(new Intent(this, PerformanceListingDetailsActivity.class).putExtra("EXTRA_PERFORMANCE_LISTING_ID", performerReference));
                break;
            case "Delete Performer Advert":
                deletePerformerAdvert();
                break;
            case "Create Band Advert":
                startActivityForResult(new Intent(this, BandAdvertisementEditor.class).putExtra("EXTRA_BAND_ID", displayMusicianBandsReference)
                                                                                                     .putExtra("EXTRA_LISTING_ID", ""), 1);
                break;
            case "View Musicians":
                startActivity(new Intent(this, MusicianAdvertIndexActivity.class).putExtra("CURRENT_BAND_ID", displayMusicianBandsReference));
                break;
            case "Edit Band":
                startActivity(new Intent(this, BandDetailsEditor.class).putExtra("EXTRA_BAND_ID", displayMusicianBandsReference));
                break;
            case "View Band Advert":
                startActivity(new Intent(this, BandListingDetailsActivity.class).putExtra("EXTRA_BAND_LISTING_ID", bandRef));
                break;
            case "Edit Band Advert":
                startActivity(new Intent(this, BandAdvertisementEditor.class).putExtra("EXTRA_BAND_ID", displayMusicianBandsReference)
                                                                                            .putExtra("EXTRA_LISTING_ID", bandRef));
                break;
            case "Delete Band Advert":
                deleteBandAdvert();
                break;
            default:
                break;
        }
    }

    /**
     * This method is used to find the logged in venue's advert and delete it from the database.
     * @since #0.2b
     */
    private void deleteBandAdvert()
    {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        GETBANDADVERTS.whereEqualTo("band-ref", displayMusicianBandsReference).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            /**
             * This method is used to query Firebase.
             * @param queryDocumentSnapshots References the documents found in Firebase upon a successful query.
             */
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                bandAdverts = queryDocumentSnapshots.getDocuments();
                System.out.println("displayMusicianBandsReference ====================== " + displayMusicianBandsReference);
                System.out.println("band adverts index 0" + bandAdverts.get(0).getReference());

                if(!bandAdverts.isEmpty())
                {
                    Log.d(TAG, "DELETEADVERT ------------------ get successful with advert");

                    BANDADVERTSREFERENCE.document(bandAdverts.get(0).getId()).delete();
                    restartActivity();
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
     * This method is used to find the logged in venue's advert and delete it from the database.
     * @since #0.2b
     */
    private void deletePerformerAdvert()
    {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        GETPERFORMERADVERTS.whereEqualTo("performer-ref", displayMusicianBandsReference).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
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
                    restartActivity();
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
     * This method is used to reload the activity layout once the advert has been deleted.
     * This is so the appropriate layout is given to the user based upon whether they have an advert or not.
     * @since #0.2b
     */
    private void restartActivity()
    {
        recreate();
    }

    /**
     * This method is used to handle the back button being pressed on the toolbar.
     * @return Returns true after being pressed.
     * @since #0.2b
     */
    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

    /**
     * This method is used to handle the back button being pressed by the user.
     * @since #0.2b
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    /**
     * Upon starting an activity awaiting a result, this method handles what this activity does upon receiving such a result.
     * @param requestCode Represents the request code sent by the starting activity.
     * @param resultCode Represents the result code.
     * @param data Represents the intent passed back from the completed activity.
     * @since #0.2b
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
    }

    /**
     * Gets the performerSuccessListener. Used for testing purposes.
     * @return Returns the performerSuccessListener of type OnSuccessListener.
     * @since #0.2b
     */
    public OnSuccessListener getPerformerSuccessListener()
    {
        return performerSuccessListener;
    }

    /**
     * Gets the performerFailureListener. Used for testing purposes.
     * @return Returns the failureListener of type OnFailureListener.
     * @since #0.2b
     */
    public OnFailureListener getPerformerFailurelistener()
    {
        return failureListener;
    }

    /**
     * Gets the bandSuccessListener. Used for testing purposes.
     * @return Returns the bandSuccessListener of type OnSuccessListener.
     * @since #0.2b
     */
    public OnSuccessListener getBandSuccessListener()
    {
        return bandSuccessListener;
    }
}
