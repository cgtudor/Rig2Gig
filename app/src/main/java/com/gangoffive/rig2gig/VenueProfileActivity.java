package com.gangoffive.rig2gig;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class VenueProfileActivity extends AppCompatActivity {
    private String vID;
    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private final CollectionReference venueReference = FSTORE.collection("venues");
    private Button rateMeButton;
    private RatingBar venueRatingBar;
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final String USERID = fAuth.getUid();
    private final CollectionReference userReference = FSTORE.collection("users");
    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";
    private String venueRating;
    private int numOfVenueRatings;
    private int totaledVenueRatings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_profile);

        /*Setting the support action bar to the newly created toolbar*/
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageView venuePhoto = findViewById(R.id.venuePhoto);
        final TextView venueName = findViewById(R.id.venueName);
        final TextView description = findViewById(R.id.description);
        final TextView rating = findViewById(R.id.rating);
        final TextView location = findViewById(R.id.location);
        final TextView type = findViewById(R.id.type);

        /*Used to get the id of the venue from the previous activity*/
        vID = getIntent().getStringExtra("EXTRA_VENUE_ID");

        /*Firestore & Cloud Storage initialization*/
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        /*Finding the venue by its ID in the "venue-listings" subfolder*/
        DocumentReference venue = db.collection("venues").document(vID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        venue.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        venueName.setText(document.get("name").toString());
                        rating.setText("Rating: " + document.get("rating").toString() + "/5");
                        location.setText(document.get("location").toString());
                        type.setText(document.get("venue-type").toString());
                        description.setText(document.get("description").toString());
                    } else {
                        Log.d("FIRESTORE", "No such document");
                    }
                } else {
                    Log.d("FIRESTORE", "get failed with ", task.getException());
                }
            }
        });

        /*Find reference for the photo associated with the listing inside the according subtree*/
        StorageReference venuePic = storage.getReference().child("/images/venues/" + vID + ".jpg");

        /*Using Glide to load the picture from the reference directly into the ImageView*/

        GlideApp.with(this)
                .load(venuePic)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(venuePhoto);

        rateMeButton = findViewById(R.id.rating_button);
        venueRatingBar = findViewById(R.id.rating_bar);

        getRatingFromFirebase();
        //setupRatingDialog();
        //checkAlreadyRated();
    }

    /**
     * This method is used to get the Venue's current rating from the database and create an appropriate display.
     */
    private void getRatingFromFirebase()
    {
        venueReference.document(vID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                venueRating = task.getResult().get("venue-rating").toString();
                numOfVenueRatings = Integer.parseInt(task.getResult().get("venue-rating-count").toString());
                totaledVenueRatings = Integer.parseInt(task.getResult().get("venue-rating-total").toString());
                venueRatingBar.setVisibility(View.VISIBLE);

                if(numOfVenueRatings >= 3)
                {
                    venueRatingBar.setRating(Float.parseFloat(venueRating));
                }
                else
                {
                    //Don't show rating in stars. Show "Not enough ratings gathered yet".
                    TextView notEnoughRatings = findViewById(R.id.unrated);
                    notEnoughRatings.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * This method is used to set up the rating dialog for users if they have not rated a Musician yet.
     */
    private void setupRatingDialog()
    {
        rateMeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
    }

    /**
     * This method is used to check whether or not the user viewing the Musician has already submitted a rating.
     */
    private void checkAlreadyRated()
    {
        //Use vID global variable to get the correct Venue Document from the Venue Collection in Firebase
        //Then create and check the "Already Rated" String[] to see if the logged in user has already submitted a rating.
        //If they have, do not call setupRatingDialog() and replace the rating button on layout with appropriate text.
        //Else if they haven't, call setupRatingDialog() to create the necessary steps for the user to rate this Musician.

        /*


        Insert check for a band here.
        Upon clicking on a Venue Profile via their advert, pass an intent to the Venue Profile containing the reference to the viewer's type.
        Check if the type of the viewer is a Band or a Musician.
        If the type of viewer is a Band Performer, then check if the band has already submitted a rating for this Venue yet.
        Else the viewer must be a Musician Performer, then check again if the musician has already submitted a rating for this Venue yet.

        Once one of those checks are complete:
        If the checked performer has already submitted a rating, display appropriate message (Maybe what they rated the Venue as).
        Else if they haven't submitted a rating yet, call setupRatingDialog() method to setup the Rate Me button and the pop up rating alert dialog.


         */

        userReference.document(USERID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                //Determine whether the logged in user viewing the profile is a Band or Venue.
                if(task.getResult().get("user-type").equals("Musician"))
                {
                    venueReference.document(vID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task)
                        {
                            //We ignore the casting exception here because we know "venue-rated-by" is a Map in Firebase.
                            @SuppressWarnings("unchecked")
                            HashMap<String, String> alreadyRatedByList = (HashMap<String, String>) task.getResult().get("venue-rated-by");

                            if(alreadyRatedByList != null && !alreadyRatedByList.isEmpty())
                            {
                                if(alreadyRatedByList.containsKey(USERID))
                                {
                                    System.out.println(TAG + " User " + USERID + " has ALREADY submitted a rating for Venue profile: " + vID);
                                }
                                else
                                {
                                    System.out.println(TAG + " User " + USERID + " has NOT YET submitted a rating for Venue profile: " + vID);
                                    setupRatingDialog();
                                }
                            }
                            else
                            {
                                System.out.println(TAG + " alreadyRatedByList is either null or empty.");
                            }

                        }
                    });
                }
                else if(task.getResult().get("user-type").equals("Venue"))
                {

                }
                else
                {
                    System.out.println(TAG + " ERROR! user-type neither Musician or Venue");
                }
            }
        });
    }

    /**
     * This method is used to decide which rating to show based upon the user type of the viewer.
     * @param viewerUserType This user type is either a Musician Performer or a Band Performer.
     */
    private void setupRatingLayout(String viewerUserType)
    {
        if(viewerUserType.equals("Musician Performer"))
        {

        }
        else if(viewerUserType.equals("Band Performer"))
        {

        }
    }

    /**
     * Overriding the up navigation to call onBackPressed
     * @return true
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void genericBack()
    {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listing_menu, menu);

        vID = getIntent().getStringExtra("EXTRA_VENUE_ID");

        /*Firestore & Cloud Storage initialization*/
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        /*Finding the listing by its ID in the "venues" subfolder*/
        DocumentReference venue = db.collection("venues").document(vID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        venue.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        MenuItem star = menu.findItem(R.id.saveButton);
                        star.setVisible(false);

                        if(document.get("user-ref").toString().equals(FirebaseAuth.getInstance().getUid()))
                        {
                            getSupportActionBar().setTitle("My venue");
                        }
                        else
                        {
                            getSupportActionBar().setTitle(document.get("name").toString());
                        }
                    } else {
                        Log.d("FIRESTORE", "No such document");
                    }
                } else {
                    Log.d("FIRESTORE", "get failed with ", task.getException());
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
