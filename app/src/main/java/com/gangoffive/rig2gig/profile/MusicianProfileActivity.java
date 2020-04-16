package com.gangoffive.rig2gig.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.gangoffive.rig2gig.firebase.GlideApp;
import com.gangoffive.rig2gig.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class MusicianProfileActivity extends AppCompatActivity {

    private String mID; //Venue ID for  profile
    private String viewerType; //Can be null if viewer did not open the profile from communications.
    private String viewerRef;
    private final ArrayList<String> bandArray = new ArrayList<>();
    private Button rateMeButton;
    private RatingBar musicianRatingBar;
    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private final CollectionReference musicianReference = FSTORE.collection("musicians");
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final String USERID = fAuth.getUid();
    private final CollectionReference userReference = FSTORE.collection("users");
    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";
    private DocumentReference ratingDocReference;
    private TextView viewer_rating_xml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musician_profile);

        /*Setting the support action bar to the newly created toolbar*/
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageView musicianPhoto = findViewById(R.id.musicianPhoto);
        final TextView musicianName = findViewById(R.id.musicianName);
        //final TextView rating = findViewById(R.id.rating);
        final TextView location = findViewById(R.id.location);
        final TextView distance = findViewById(R.id.venue_description_final);
        final TextView bands = findViewById(R.id.bands);

        /*Used to get the id of the musician from the previous activity*/
        mID = getIntent().getStringExtra("EXTRA_MUSICIAN_ID");
        /*If a user is opening the profile from communications*/
        viewerType = getIntent().getStringExtra("EXTRA_VIEWER_TYPE"); //venues / musicians / bands
        viewerRef = getIntent().getStringExtra("EXTRA_VIEWER_REF"); //The ID of the viewer

        /*Firestore & Cloud Storage initialization*/
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        final Button noInternet = findViewById(R.id.noInternet);

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        noInternet.setVisibility(isConnected ? View.GONE : View.VISIBLE);

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        /*Finding the musician by its ID in the "musicians" subfolder*/
        DocumentReference musician = db.collection("musicians").document(mID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        musician.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        musicianName.setText(document.get("name").toString());
                        //rating.setText("Rating: " + document.get("rating").toString() + "/5");
                        location.setText(document.get("location").toString());
                        distance.setText("Distance willing to travel: " + document.get("distance").toString() + " miles");
                        bandArray.addAll((ArrayList<String>) document.get("bands"));
                        bands.setText("Bands: ");
                        for(String band : bandArray)
                        {
                            db.collection("bands").document(band)
                                    .get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        DocumentSnapshot bandDoc = task.getResult();
                                        if(bandDoc.exists())
                                        {
                                            String bandName = bandDoc.get("name").toString();
                                            bands.setText(bands.getText().equals("Bands: ") ?
                                                    "Bands: " + bandName :
                                                    bands.getText() + ", " + bandName);
                                        }
                                    }
                                }
                            });
                        }
                    } else {
                        Log.d("FIRESTORE", "No such document");
                    }
                } else {
                    Log.d("FIRESTORE", "get failed with ", task.getException());
                }
            }
        });

        /*Find reference for the photo associated with the listing inside the according subtree*/
        StorageReference musicianPic = storage.getReference().child("/images/musicians/" + mID + ".jpg");

        /*Using Glide to load the picture from the reference directly into the ImageView*/

        GlideApp.with(this)
                .load(musicianPic)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .into(musicianPhoto);

        rateMeButton = findViewById(R.id.rating_button);
        musicianRatingBar = findViewById(R.id.rating_bar);

        getRatingFromFirebase();
        setupRatingDialog();
        checkAlreadyRated();
    }

    /**
     * This method is used to get the Venue's current rating from the database and create an appropriate display.
     */
    private void getRatingFromFirebase()
    {
        musicianReference.document(mID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                TextView ratingType = findViewById(R.id.my_rating);
                TextView unrated = findViewById(R.id.unrated);

                if(viewerType.equals("bands"))
                {
                    String currentMusicianRating = task.getResult().get("musician-rating").toString();

                    if(currentMusicianRating.equals("unrated"))
                    {
                        //We want to display an appropriate message to the user explaining there aren't enough ratings yet.
                        musicianRatingBar.setRating(0);

                        unrated.setVisibility(View.VISIBLE);
                        ratingType.setText("My Musician Rating");
                        ratingType.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        //Else we want to show what the current rating is.
                        musicianRatingBar.setRating(Float.valueOf(currentMusicianRating));
                    }
                }
                else if(viewerType.equals("venues"))
                {
                    String currentPerformerRating = task.getResult().get("performer-rating").toString();

                    if(currentPerformerRating.equals("Unrated"))
                    {
                        //We want to display an appropriate message to the user explaining there aren't enough ratings yet.
                        musicianRatingBar.setRating(0);

                        unrated.setVisibility(View.VISIBLE);
                        ratingType.setText("My Performer Rating");
                        ratingType.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        //Else we want to show what the current rating is.
                        musicianRatingBar.setRating(Float.valueOf(currentPerformerRating));
                    }
                }
                else
                {
                    System.out.println(TAG + " viewerType Error! viewerType ====== " + viewerType);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(MusicianProfileActivity.this);

                View layout = null;

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                layout = inflater.inflate(R.layout.rating, null);

                RatingBar alertDialogRatingBar = layout.findViewById(R.id.ratingBar);

                builder.setTitle("Rate Me!");
                builder.setMessage("Thank you for rating me. It will help me improve in the future.");

                builder.setPositiveButton("Rate!", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        float musicianRating = alertDialogRatingBar.getRating();

                        musicianReference.document(mID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task)
                            {
                                HashMap<String, Object> updateRatingMap = new HashMap<>();

                                //Here, calculate the new rating and store in Firebase.
                                if(viewerType.equals("bands")) //If the viewer is viewing from their band.
                                {
                                    String currentVenueRating = task.getResult().get("musician-rating").toString();
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
                                    String currentVenueRating = task.getResult().get("performer-rating").toString();
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

                                musicianReference.document(mID).update(updateRatingMap);

                                ratingDocReference = FSTORE.collection("ratings").document(viewerRef).collection(viewerType).document(mID);

                                ratingDocReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                                {
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

                                Toast.makeText(MusicianProfileActivity.this, "Rating Submitted!", Toast.LENGTH_SHORT).show();

                                rateMeButton.setVisibility(View.GONE);

                                viewer_rating_xml = findViewById(R.id.viewer_rating);
                                viewer_rating_xml.setText("Thank you for rating me!");
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
                        Toast.makeText(MusicianProfileActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
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
     */
    private void checkAlreadyRated()
    {
        ratingDocReference = FSTORE.collection("ratings").document(viewerRef).collection(viewerType).document(mID);

        ratingDocReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                Object viewerRating = task.getResult().get("rating");

                if (viewerRating != null) //Our rating isn't null and we have reviewed this Musician before.
                {
                    System.out.println(TAG + " viewerRef found in ratedMap");

                    System.out.println(TAG + " Setting viewer rating on profile.");

                    viewer_rating_xml = findViewById(R.id.viewer_rating);

                    if(viewerType.equals("bands"))
                    {
                        viewer_rating_xml.setText("You rated my Musician skills " + viewerRating.toString() + " stars!");
                    }
                    else if(viewerType.equals("venues"))
                    {
                        viewer_rating_xml.setText("You rated my Performance skills " + viewerRating.toString() + " stars!");
                    }
                    else
                    {
                        System.out.println(TAG + " viewerType Error! viewerType ====== " + viewerType);
                    }

                    viewer_rating_xml.setVisibility(View.VISIBLE);
                }
                else
                {
                    //We haven't reviewed this Musician before.
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listing_menu, menu);

        mID = getIntent().getStringExtra("EXTRA_MUSICIAN_ID");

        /*Firestore & Cloud Storage initialization*/
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        /*Finding the listing by its ID in the "performer-listings" subfolder*/
        DocumentReference musician = db.collection("musicians").document(mID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        musician.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                            getSupportActionBar().setTitle("My profile");
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
