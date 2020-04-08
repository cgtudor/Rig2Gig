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

public class BandConsoleActivity extends AppCompatActivity implements View.OnClickListener
{
    private List<DocumentSnapshot> bandAdverts;
    private List<DocumentSnapshot> performerAdverts;
    private List<DocumentSnapshot> bands;

    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final String USERID = fAuth.getUid();

    private final CollectionReference bandReference = FSTORE.collection("bands");
    private final Query getBands = bandReference;

    private final CollectionReference bandAdvertsReference = FSTORE.collection("band-listings");
    private final Query getBandAdverts = bandAdvertsReference;

    private final CollectionReference performerAdvertsReference = FSTORE.collection("performer-listings");
    private final Query getPerformerAdverts = performerAdvertsReference;

    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";

    private String bandRef;
    private String displayMusicianBandsReference; //The band reference.
    private String bandName;
    private String performerReference; //The band's performer advert.

    /**
     * Upon creation of the VenueConsoleFragment, create the fragment_venue_console layout.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     * @return Returns a View of the fragment_venue_console layout.
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

        final Button noInternet = findViewById(R.id.noInternet);

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        noInternet.setVisibility(isConnected ? View.GONE : View.VISIBLE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(bandName);

        //General Section
        final CardView card_view_view_venues = findViewById(R.id.card_view_view_Venues);
        final CardView card_view_view_musicians = findViewById(R.id.card_view_view_musicians);
        final CardView card_view_edit_band = findViewById(R.id.card_view_edit_band);
        final CardView card_view_manage_members = findViewById(R.id.card_view_manage_members);

        card_view_view_venues.setOnClickListener(this);
        card_view_edit_band.setOnClickListener(this);
        card_view_view_musicians.setOnClickListener(this);
        card_view_manage_members.setOnClickListener(this);

        //Performer Advert Section
        final CardView card_view_create_advert = findViewById(R.id.card_view_create_performer_advert);
        final CardView card_view_edit_advert = findViewById(R.id.card_view_edit_performer_advert);
        final CardView card_view_view_advert = findViewById(R.id.card_view_view_performer_advert);
        final CardView card_view_delete_advert = findViewById(R.id.card_view_delete_performer_advert);


        card_view_create_advert.setOnClickListener(this);
        card_view_edit_advert.setOnClickListener(this);
        card_view_view_advert.setOnClickListener(this);
        card_view_delete_advert.setOnClickListener(this);

        //Band Advert Section

        final CardView card_view_create_band_advert = findViewById(R.id.card_view_create_band_advert);
        final CardView card_view_view_band_advert = findViewById(R.id.card_view_view_band_advert);
        final CardView card_view_edit_band_band_advert = findViewById(R.id.card_view_edit_band_band_advert);
        final CardView card_view_delete_band_advert = findViewById(R.id.card_view_delete_band_advert);


        card_view_create_band_advert.setOnClickListener(this);
        card_view_view_band_advert.setOnClickListener(this);
        card_view_edit_band_band_advert.setOnClickListener(this);
        card_view_delete_band_advert.setOnClickListener(this);

        if(!isConnected)
        {
            //General Section
            card_view_edit_band.setAlpha(0.5f);
            card_view_manage_members.setAlpha(0.5f);
            card_view_edit_band.setClickable(false);
            card_view_manage_members.setClickable(false);

            //Performer Section
            card_view_create_advert.setAlpha(0.5f);
            card_view_edit_advert.setAlpha(0.5f);
            card_view_delete_advert.setAlpha(0.5f);
            card_view_create_advert.setClickable(false);
            card_view_edit_advert.setClickable(false);
            card_view_delete_advert.setClickable(false);

            //Band Section
            card_view_create_band_advert.setAlpha(0.5f);
            card_view_edit_band_band_advert.setAlpha(0.5f);
            card_view_delete_band_advert.setAlpha(0.5f);
            card_view_create_band_advert.setClickable(false);
            card_view_edit_band_band_advert.setClickable(false);
            card_view_delete_band_advert.setClickable(false);
        }

        //Setup layout
        databaseQuery();
    }

    /**
     * This method queries the database collecting all venues and venue adverts. These lists are then processed
     * to set up variables for possible button clicks where extras need to be sent with intents.
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

        getPerformerAdverts.whereEqualTo("performer-ref", displayMusicianBandsReference).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
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

                    editProfileLayout = findViewById(R.id.card_view_edit_performer_advert);
                    editProfileLayout.setVisibility(View.VISIBLE);

                    editProfileLayout = findViewById(R.id.card_view_view_performer_advert);
                    editProfileLayout.setVisibility(View.VISIBLE);

                    editProfileLayout = findViewById(R.id.card_view_delete_performer_advert);
                    editProfileLayout.setVisibility(View.VISIBLE);

                    performerReference = performerAdverts.get(0).getId();
                }
                else
                {
                    Log.d(TAG, "DATABASEQUERY PERFORMER ------------------ get successful without advert");

                    editProfileLayout = findViewById(R.id.card_view_create_performer_advert);
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

        getBandAdverts.whereEqualTo("band-ref", displayMusicianBandsReference).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
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

                    editProfileLayout = findViewById(R.id.card_view_view_band_advert);
                    editProfileLayout.setVisibility(View.VISIBLE);

                    editProfileLayout = findViewById(R.id.card_view_edit_band_band_advert);
                    editProfileLayout.setVisibility(View.VISIBLE);

                    editProfileLayout = findViewById(R.id.card_view_delete_band_advert);
                    editProfileLayout.setVisibility(View.VISIBLE);

                    bandRef = bandAdverts.get(0).getId();
                }
                else
                {
                    Log.d(TAG, "DATABASEQUERY BAND ------------------ get successful with advert");

                    editProfileLayout = findViewById(R.id.card_view_create_band_advert);
                    editProfileLayout.setVisibility(View.VISIBLE);
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
                deleteBandAdvert(); //Refactor deleteBandAdvert()
                break;
            default:
                break;
        }
    }

    /**
     * This method is used to find the logged in venue's advert and delete it from the database.
     */
    private void deleteBandAdvert()
    {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        getBandAdverts.whereEqualTo("band-ref", displayMusicianBandsReference).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                bandAdverts = queryDocumentSnapshots.getDocuments();
                System.out.println("displayMusicianBandsReference ====================== " + displayMusicianBandsReference);
                System.out.println("band adverts index 0" + bandAdverts.get(0).getReference());

                if(!bandAdverts.isEmpty())
                {
                    Log.d(TAG, "DELETEADVERT ------------------ get successful with advert");

                    bandAdvertsReference.document(bandAdverts.get(0).getId()).delete();
                    restartActivity();
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
     * This method is used to find the logged in venue's advert and delete it from the database.
     */
    private void deletePerformerAdvert()
    {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        getPerformerAdverts.whereEqualTo("performer-ref", displayMusicianBandsReference).get(source).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                performerAdverts = queryDocumentSnapshots.getDocuments();

                if(!performerAdverts.isEmpty())
                {
                    Log.d(TAG, "DELETEADVERT ------------------ get successful with advert");

                    performerAdvertsReference.document(performerAdverts.get(0).getId()).delete();
                    restartActivity();
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
     * This method is used to reload the activity layout once the advert has been deleted.
     * This is so the appropriate layout is given to the user based upon whether they have an advert or not.
     */
    private void restartActivity()
    {
        recreate();
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        recreate();
    }
}
