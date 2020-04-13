package com.gangoffive.rig2gig;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VenueProfileActivity extends AppCompatActivity {
    private String vID; //Venue ID for  profile
    private String viewerType; //Can be null if viewer did not open the profile from communications.
    private String viewerRef;
    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private final CollectionReference venueReference = FSTORE.collection("venues");
    private Button rateMeButton;
    private RatingBar venueRatingBar;
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final String USERID = fAuth.getUid();
    private final CollectionReference userReference = FSTORE.collection("users");
    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";
    private DocumentReference ratingDocReference;
    private CollectionReference viewerRatingsReference;
    private String venueRating;
    private int numOfVenueRatings;
    private int totaledVenueRatings;
    private TextView venueName;
    private TextView viewer_rating_xml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_profile);

        /*Setting the support action bar to the newly created toolbar*/
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageView venuePhoto = findViewById(R.id.venuePhoto);
        venueName = findViewById(R.id.venueName);
        final TextView description = findViewById(R.id.description);
        final TextView rating = findViewById(R.id.rating);
        final TextView location = findViewById(R.id.location);
        final TextView type = findViewById(R.id.type);

        /*Used to get the id of the venue from the previous activity*/
        vID = getIntent().getStringExtra("EXTRA_VENUE_ID");
        /*If a user is opening the profile from communications*/
        viewerType = getIntent().getStringExtra("EXTRA_VIEWER_TYPE"); //venues / musicians / bands
        viewerRef = getIntent().getStringExtra("EXTRA_VIEWER_REF"); //The ID of the viewer

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
        setupRatingDialog();
        checkAlreadyRated();
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
                String currentVenueRating = task.getResult().get("venue-rating").toString();

                if(currentVenueRating.equals("unrated"))
                {
                    //We want to display an appropriate message to the user explaining there aren't enough ratings yet.
                    venueRatingBar.setRating(0);

                    TextView unrated = findViewById(R.id.unrated);
                    unrated.setVisibility(View.VISIBLE);
                }
                else
                {
                    //Else we want to show what the current rating is.
                    venueRatingBar.setRating(Float.valueOf(currentVenueRating));
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
                AlertDialog.Builder builder = new AlertDialog.Builder(VenueProfileActivity.this);

                View layout = null;

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                layout = inflater.inflate(R.layout.rating, null);

                RatingBar alertDialogRatingBar = (RatingBar) layout.findViewById(R.id.ratingBar);

                builder.setTitle("Rate Us!");
                builder.setMessage("Thank you for rating us. It will help us improve in the future.");

                builder.setPositiveButton("Rate!", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        float venueRating = alertDialogRatingBar.getRating();

                        venueReference.document(vID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task)
                            {
                                //Here, calculate the new rating and store in Firebase.

                                String currentVenueRating = task.getResult().get("venue-rating").toString();
                                float venueRatingCount = Float.valueOf(task.getResult().get("venue-rating-count").toString());
                                float venueRatingTotal = Float.valueOf(task.getResult().get("venue-rating-total").toString());

                                HashMap<String, Object> updateRatingMap = new HashMap<>();

                                if(venueRatingCount + 1 >= 3)
                                {
                                    //After this rating, we now have enough ratings to provide a fair rating for a Venue.
                                    //Calculate and submit new rating to Firebase changing unrated to new calculated rating.

                                    updateRatingMap.put("venue-rating", (Float.valueOf(venueRatingTotal + venueRating) / (Float.valueOf(venueRatingCount + 1))));
                                    updateRatingMap.put("venue-rating-count", venueRatingCount + 1);
                                    updateRatingMap.put("venue-rating-total", venueRatingTotal + venueRating);
                                }
                                else
                                {
                                    //Add to current rating count
                                    //Add to current rating sum
                                    //Update Firebase with new numbers.

                                    updateRatingMap.put("venue-rating-count", venueRatingCount + 1);
                                    updateRatingMap.put("venue-rating-total", venueRatingTotal + venueRating);
                                }

                                venueReference.document(vID).update(updateRatingMap);

                                ratingDocReference = FSTORE.collection("ratings").document(viewerRef);

                                ratingDocReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                    {
                                        HashMap<String, Object> ratedMap = (HashMap<String, Object>) task.getResult().get("rated");

                                        if(ratedMap != null)
                                        {
                                            ratedMap.put(vID, String.valueOf(venueRating));
                                            ratingDocReference.update("rated", ratedMap);
                                        }
                                        else
                                        {
                                            ratedMap = new HashMap<>();
                                            ratedMap.put(vID, String.valueOf(venueRating));
                                            ratingDocReference.update("rated", ratedMap);
                                        }
                                    }
                                });

                                Toast.makeText(VenueProfileActivity.this, "Rating Submitted!", Toast.LENGTH_SHORT).show();

                                rateMeButton.setVisibility(View.GONE);

                                viewer_rating_xml = findViewById(R.id.viewer_rating);
                                viewer_rating_xml.setText("Thank you for rating us!");
                                viewer_rating_xml.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        Toast.makeText(VenueProfileActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setCancelable(false);
                builder.setView(layout);
                builder.show();
            }
        });
    }

    /**
     * This method is used to check whether or not the user viewing the Musician has already submitted a rating.
     * Here we decide whether we will show the Rate Me button or an appropriate message.
     */
    private void checkAlreadyRated()
    {
        ratingDocReference = FSTORE.collection("ratings").document(viewerRef);
                //.collection(viewerType);

        //FSTORE.collection("ratings").document(vID).collection(viewerType).document(viewerRef);

        ratingDocReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                HashMap<String, String> ratedMap = (HashMap<String, String>) task.getResult().get("rated");

                if (ratedMap != null && ratedMap.containsKey(vID))//Our map isn't null and we have reviewed this Venue before.
                {
                    System.out.println(TAG + " viewerRef found in ratedMap");

                    String viewerRating = ratedMap.get(vID);

                    System.out.println(TAG + " Setting viewer rating on profile.");

                    viewer_rating_xml = findViewById(R.id.viewer_rating);
                    viewer_rating_xml.setText("You rated us " + viewerRating + " stars!");
                    viewer_rating_xml.setVisibility(View.VISIBLE);
                }
                else
                {
                    //We haven't reviewed this Venue before.
                    Button rating_button_xml = findViewById(R.id.rating_button);
                    rating_button_xml.setVisibility(View.VISIBLE);
                }
            }
        });
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
