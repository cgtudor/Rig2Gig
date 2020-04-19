package com.gangoffive.rig2gig.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

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

public class BandProfileRatingsDialog extends AppCompatActivity
{
    private int height, width;
    private Button cancel, rate;
    private String bID;
    private String viewerType;
    private String viewerRef;
    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";
    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private final CollectionReference bandReference = FSTORE.collection("bands");
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
        bID = intent.getStringExtra("EXTRA_BAND_ID");
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

        float bandRating = alertDialogRatingBar.getRating();

        bandReference.document(bID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                HashMap<String, Object> updateRatingMap = new HashMap<>();

                //Here, calculate the new rating and store in Firebase.
                if(viewerType.equals("musicians")) //If the viewer is viewing from their band.
                {
                    float bandRatingCount = Float.valueOf(task.getResult().get("band-rating-count").toString());
                    float bandRatingTotal = Float.valueOf(task.getResult().get("band-rating-total").toString());

                    if (bandRatingCount + 1 >= 3) {
                        //After this rating, we now have enough ratings to provide a fair rating for a Venue.
                        //Calculate and submit new rating to Firebase changing unrated to new calculated rating.

                        updateRatingMap.put("band-rating", (Float.valueOf(bandRatingTotal + bandRating) / (Float.valueOf(bandRatingCount + 1))));
                        updateRatingMap.put("band-rating-count", bandRatingCount + 1);
                        updateRatingMap.put("band-rating-total", bandRatingTotal + bandRating);
                    } else {
                        //Add to current rating count
                        //Add to current rating sum
                        //Update Firebase with new numbers.

                        updateRatingMap.put("band-rating-count", bandRatingCount + 1);
                        updateRatingMap.put("band-rating-total", bandRatingTotal + bandRating);
                    }
                }
                else if(viewerType.equals("venues"))
                {
                    float performerRatingCount = Float.valueOf(task.getResult().get("performer-rating-count").toString());
                    float performerRatingTotal = Float.valueOf(task.getResult().get("performer-rating-total").toString());

                    if(performerRatingCount + 1 >= 3)
                    {
                        //After this rating, we now have enough ratings to provide a fair rating for a Venue.
                        //Calculate and submit new rating to Firebase changing unrated to new calculated rating.

                        updateRatingMap.put("performer-rating", (Float.valueOf(performerRatingTotal + bandRating) / (Float.valueOf(performerRatingCount + 1))));
                        updateRatingMap.put("performer-rating-count", performerRatingCount + 1);
                        updateRatingMap.put("performer-rating-total", performerRatingTotal + bandRating);
                    }
                    else
                    {
                        //Add to current rating count
                        //Add to current rating sum
                        //Update Firebase with new numbers.

                        updateRatingMap.put("performer-rating-count", performerRatingCount + 1);
                        updateRatingMap.put("performer-rating-total", performerRatingTotal + bandRating);
                    }
                }
                else
                {
                    System.out.println(TAG + " viewerType Error! viewerType ====== " + viewerType);
                }

                bandReference.document(bID).update(updateRatingMap);

                ratingDocReference = FSTORE.collection("ratings").document(viewerRef).collection(viewerType).document(bID);

                ratingDocReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        HashMap<String, Object> ratedMap = (HashMap<String, Object>) task.getResult().get("rated");

                        if(ratedMap != null)
                        {
                            ratedMap.put("rating", String.valueOf(bandRating));
                            ratingDocReference.update(ratedMap);
                        }
                        else
                        {
                            ratedMap = new HashMap<>();
                            ratedMap.put("rating", String.valueOf(bandRating));
                            ratingDocReference.set(ratedMap);
                        }
                    }
                });

                Intent result = new Intent();
                result.putExtra("EXTRA_HAS_RATED", true);
                result.putExtra("EXTRA_RATING_RESULT", bandRating);
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
