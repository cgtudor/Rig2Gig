package com.gangoffive.rig2gig.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gangoffive.rig2gig.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class VenueProfileRatingsDialog extends AppCompatActivity
{
    private int height, width;
    private Button cancel, rate;
    private String vID;
    private String viewerType;
    private String viewerRef;
    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private final CollectionReference venueReference = FSTORE.collection("venues");
    private DocumentReference ratingDocReference;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ratings);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = (metrics.heightPixels) /100 * 50;
        width = (metrics.widthPixels) /100 * 80;
        getWindow().setLayout(width,height);

        Intent intent = getIntent();
        vID = intent.getStringExtra("EXTRA_VENUE_ID");
        viewerType = intent.getStringExtra("EXTRA_VIEWER_TYPE");
        viewerRef = intent.getStringExtra("EXTRA_VIEWER_REF");

        rate = findViewById(R.id.rate);

        rate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ratingPost();
            }
        });

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                returnNotRated();
            }
        });
    }

    private void ratingPost()
    {
        RatingBar alertDialogRatingBar = findViewById(R.id.ratingBar);

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

                ratingDocReference = FSTORE.collection("ratings").document(viewerRef).collection(viewerType).document(vID);

                ratingDocReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        HashMap<String, Object> ratedMap = (HashMap<String, Object>) task.getResult().get("rated");

                        if(ratedMap != null)
                        {
                            ratedMap.put("rating", String.valueOf(venueRating));
                            ratingDocReference.update(ratedMap);
                        }
                        else
                        {
                            ratedMap = new HashMap<>();
                            ratedMap.put("rating", String.valueOf(venueRating));
                            ratingDocReference.set(ratedMap);
                        }
                    }
                });

                Intent result = new Intent();
                result.putExtra("EXTRA_HAS_RATED", true);
                result.putExtra("EXTRA_RATING_RESULT", venueRating);
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }

    /**
     * Handle on back pressed
     */
    @Override
    public void onBackPressed()
    {
        returnNotRated();
    }

    /**
     * Handle if top activity changes
     * @param isTopResumedActivity false if no longer the top activty
     */
    @Override
    public void onTopResumedActivityChanged (boolean isTopResumedActivity)
    {
        if(!isTopResumedActivity)
        {
            returnNotRated();
        }
    }

    /**
     * Finish activity if band member is not deleted
     */
    public void returnNotRated()
    {
        Intent result = new Intent();
        result.putExtra("EXTRA_HAS_RATED", false);
        setResult(RESULT_OK, result);
        finish();
    }
}
