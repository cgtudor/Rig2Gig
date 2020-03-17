package com.gangoffive.rig2gig;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class BandConsoleActivity extends AppCompatActivity implements View.OnClickListener
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

    private String bandRef;
    private String displayMusicianBandsReference;
    private String bandName;
    private String performerReference;

    private Toolbar toolbar;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(bandName);

        final CardView card_view_view_venues = findViewById(R.id.card_view_view_Venues);
        final CardView card_view_edit_band = findViewById(R.id.card_view_edit_band);
        //final CardView card_view_invite_musicians = view.findViewById(R.id.card_view_my_bands); REMOVE THIS
        final CardView card_view_create_advert = findViewById(R.id.card_view_create_performer_advert);
        final CardView card_view_edit_advert = findViewById(R.id.card_view_edit_performer_advert);
        final CardView card_view_view_advert = findViewById(R.id.card_view_view_performer_advert);
        final CardView card_view_delete_advert = findViewById(R.id.card_view_delete_performer_advert);

        card_view_view_venues.setOnClickListener(this);
        card_view_edit_band.setOnClickListener(this);
        //card_view_invite_musicians.setOnClickListener(this); REMOVE THIS
        card_view_create_advert.setOnClickListener(this);
        card_view_edit_advert.setOnClickListener(this);
        card_view_view_advert.setOnClickListener(this);
        card_view_delete_advert.setOnClickListener(this);

        databaseQuery();
    }

    /**
     * This method queries the database collecting all venues and venue adverts. These lists are then processed
     * to set up variables for possible button clicks where extras need to be sent with intents.
     */
    private void databaseQuery()
    {
        getBandAdverts.whereEqualTo("band-ref", displayMusicianBandsReference).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                bandAdverts = queryDocumentSnapshots.getDocuments();

                CardView editProfileLayout;
                LinearLayout textView;

                if(!bandAdverts.isEmpty())
                {
                    Log.d(TAG, "DATABASEQUERY PERFORMER ------------------ get successful with advert");

                    textView = findViewById(R.id.performer_advert_title);
                    textView.setVisibility(View.VISIBLE);

                    editProfileLayout = findViewById(R.id.card_view_view_Venues);
                    editProfileLayout.setVisibility(View.VISIBLE);

                    editProfileLayout = findViewById(R.id.card_view_edit_band);
                    editProfileLayout.setVisibility(View.VISIBLE);

                    editProfileLayout = findViewById(R.id.card_view_edit_advert);
                    editProfileLayout.setVisibility(View.VISIBLE);

                    editProfileLayout = findViewById(R.id.card_view_view_performer_advert);
                    editProfileLayout.setVisibility(View.VISIBLE);

                    editProfileLayout = findViewById(R.id.card_view_delete_advert);
                    editProfileLayout.setVisibility(View.VISIBLE);

                    performerReference = bandAdverts.get(0).getId();
                }
                else
                {
                    Log.d(TAG, "DATABASEQUERY PERFORMER ------------------ get successful without advert");

                    textView = findViewById(R.id.performer_advert_title);
                    textView.setVisibility(View.VISIBLE);

                    editProfileLayout = findViewById(R.id.card_view_view_Venues);
                    editProfileLayout.setVisibility(View.VISIBLE);

                    editProfileLayout = findViewById(R.id.card_view_edit_band);
                    editProfileLayout.setVisibility(View.VISIBLE);

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
                //To be implemented
                break;
            case "Edit Musician":
                //To be implemented
                break;
            case "Create Performer Advert":
                //To be implemented
                break;
            case "Edit Performer Advert":
                //To be implemented
                break;
            case "View Performer Advert":
                //To be implemented
                break;
            case "Delete Performer Advert":
                deletePerformerAdvert();
                break;
            case "View Musicians":
                //To be implemented.
                break;
            case "Edit Band":
                //To be implemented.
                break;
            case "View Band Advert":
                //To be implemented.
                break;
            case "Edit Band Advert":
                //To be implemented.
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
        getBandAdverts.whereEqualTo("band-ref", bandRef).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                bandAdverts = queryDocumentSnapshots.getDocuments();

                if(!bandAdverts.isEmpty())
                {
                    Log.d(TAG, "DELETEADVERT ------------------ get successful with advert");

                    //bandAdvertsReference.document(bandAdverts.get(0).getId()).delete();
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
     * This method is used to find the logged in venue's advert and delete it from the database.
     */
    private void deletePerformerAdvert()
    {
        getBandAdverts.whereEqualTo("performer-ref", bandRef).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                bandAdverts = queryDocumentSnapshots.getDocuments();

                if(!bandAdverts.isEmpty())
                {
                    Log.d(TAG, "DELETEADVERT ------------------ get successful with advert");

                    //bandAdvertsReference.document(bandAdverts.get(0).getId()).delete();
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
     * This method is used to reload the activity layout once the advert has been deleted.
     * This is so the appropriate layout is given to the user based upon whether they have an advert or not.
     */
    private void restartFragment()
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
}
