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

/**
 * This class is used to create a custom dialog popup.
 * @author Ben souch
 * @version #0.3b
 * @since #0.2b
 */
public class MusicianProfileRatingsDialog extends AppCompatActivity
{
    private int height, width;
    private Button cancel, rate;
    private String mID;
    private String viewerType;
    private String viewerRef;
    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";
    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private final CollectionReference MUSICIANREFERENCE = FSTORE.collection("musicians");
    private DocumentReference ratingDocReference;

    /**
     * This method is used to create the view upon creation of the class.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     * @since #0.2b
     */
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
        mID = intent.getStringExtra("EXTRA_MUSICIAN_ID");
        viewerType = intent.getStringExtra("EXTRA_VIEWER_TYPE");
        viewerRef = intent.getStringExtra("EXTRA_VIEWER_REF");

        rate = findViewById(R.id.rate);

        rate.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This method is used to handle the click of the rate Button.
             * @param v Represents the view.
             * @since #0.2b
             */
            @Override
            public void onClick(View v)
            {
                ratingPost();
            }
        });

        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This method is used to handle the click of the cancel Button.
             * @param v Represents the view.
             * @since #0.2b
             */
            @Override
            public void onClick(View v)
            {
                returnNotRated();
            }
        });
    }

    /**
     * This method is used to get the user's rating from the rating bar and post it to Firebase.
     * @since #0.2b
     */
    private void ratingPost()
    {
        RatingBar alertDialogRatingBar = findViewById(R.id.ratingBar);

        float musicianRating = alertDialogRatingBar.getRating();

        MUSICIANREFERENCE.document(mID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            /**
             * This method is used to determine the completion of a get request of Firebase.
             * @param task References the result of the get request.
             * @since #0.2b
             */
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                HashMap<String, Object> updateRatingMap = new HashMap<>();

                //Here, calculate the new rating and store in Firebase.
                if(viewerType.equals("bands")) //If the viewer is viewing from their band.
                {
                    float musicianRatingCount = Float.valueOf(task.getResult().get("musician-rating-count").toString());
                    float musicianRatingTotal = Float.valueOf(task.getResult().get("musician-rating-total").toString());

                    if (musicianRatingCount + 1 >= 3) {
                        //After this rating, we now have enough ratings to provide a fair rating for a Venue.
                        //Calculate and submit new rating to Firebase changing unrated to new calculated rating.

                        updateRatingMap.put("musician-rating", (Float.valueOf(musicianRatingTotal + musicianRating) / (Float.valueOf(musicianRatingCount + 1))));
                        updateRatingMap.put("musician-rating-count", musicianRatingCount + 1);
                        updateRatingMap.put("musician-rating-total", musicianRatingTotal + musicianRating);
                    } else {
                        //Add to current rating count
                        //Add to current rating sum
                        //Update Firebase with new numbers.

                        updateRatingMap.put("musician-rating-count", musicianRatingCount + 1);
                        updateRatingMap.put("musician-rating-total", musicianRatingTotal + musicianRating);
                    }
                }
                else if(viewerType.equals("venues"))
                {
                    String currentMusicianRating = task.getResult().get("performer-rating").toString();
                    float performerRatingCount = Float.valueOf(task.getResult().get("performer-rating-count").toString());
                    float performerRatingTotal = Float.valueOf(task.getResult().get("performer-rating-total").toString());

                    if(performerRatingCount + 1 >= 3)
                    {
                        //After this rating, we now have enough ratings to provide a fair rating for a Venue.
                        //Calculate and submit new rating to Firebase changing unrated to new calculated rating.

                        updateRatingMap.put("performer-rating", (Float.valueOf(performerRatingTotal + musicianRating) / (Float.valueOf(performerRatingCount + 1))));
                        updateRatingMap.put("performer-rating-count", performerRatingCount + 1);
                        updateRatingMap.put("performer-rating-total", performerRatingTotal + musicianRating);
                    }
                    else
                    {
                        //Add to current rating count
                        //Add to current rating sum
                        //Update Firebase with new numbers.

                        updateRatingMap.put("performer-rating-count", performerRatingCount + 1);
                        updateRatingMap.put("performer-rating-total", performerRatingTotal + musicianRating);
                    }
                }
                else
                {
                    System.out.println(TAG + " viewerType Error! viewerType ====== " + viewerType);
                }

                MUSICIANREFERENCE.document(mID).update(updateRatingMap);

                ratingDocReference = FSTORE.collection("ratings").document(viewerRef).collection(viewerType).document(mID);

                ratingDocReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                {
                    /**
                     * This method is used to determine the completion of a get request of Firebase.
                     * @param task References the result of the get request.
                     * @since #0.2b
                     */
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        HashMap<String, Object> ratedMap = (HashMap<String, Object>) task.getResult().get("rated");

                        if(ratedMap != null)
                        {
                            ratedMap.put("rating", String.valueOf(musicianRating));
                            ratingDocReference.update(ratedMap);
                        }
                        else
                        {
                            ratedMap = new HashMap<>();
                            ratedMap.put("rating", String.valueOf(musicianRating));
                            ratingDocReference.set(ratedMap);
                        }
                    }
                });

                Intent result = new Intent();
                result.putExtra("EXTRA_HAS_RATED", true);
                result.putExtra("EXTRA_RATING_RESULT", musicianRating);
                setResult(RESULT_OK, result);
                finish();
            }
        });
    }

    /**
     * Handle on back pressed.
     * @since #0.2b
     */
    @Override
    public void onBackPressed()
    {
        returnNotRated();
    }

    /**
     * If dialog popup is cancelled or clicked off of, then treat as though no rating has occurred.
     * @param isTopResumedActivity false if no longer the top activity.
     * @since #0.2b
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
     * Finish activity if rating dialog is cancelled or clicked off of.
     * @since #0.2b
     */
    public void returnNotRated()
    {
        Intent result = new Intent();
        result.putExtra("EXTRA_HAS_RATED", false);
        setResult(RESULT_OK, result);
        finish();
    }
}
