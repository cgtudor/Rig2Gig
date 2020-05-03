package com.gangoffive.rig2gig.profile;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gangoffive.rig2gig.account.AccountPurposeActivity;
import com.gangoffive.rig2gig.firebase.GlideApp;
import com.gangoffive.rig2gig.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MusicianProfileActivity extends AppCompatActivity {

    private String mID; //Musician ID for  profile
    private String viewerType; //Can be null if viewer did not open the profile from communications.
    private String viewerRef;
    private final ArrayList<String> bandArray = new ArrayList<>();
    private Button rateMeButton;
    private RatingBar musicianRatingBar;
    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private final CollectionReference MUSICIANREFERENCE = FSTORE.collection("musicians");
    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";
    private DocumentReference ratingDocReference;
    private TextView viewer_rating_xml;
    private TextView fader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musician_profile);

        /*Setting the support action bar to the newly created toolbar*/
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageView musicianPhoto = findViewById(R.id.musicianPhoto);
        final TextView musicianName = findViewById(R.id.musicianName);
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
        musician.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            /**
             * This method is used to determine the completion of a get request of Firebase.
             * @param task References the result of the get request.
             */
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        musicianName.setText(document.get("name").toString());
                        location.setText(document.get("location").toString());
                        distance.setText("Distance willing to travel: " + document.get("distance").toString() + " miles");
                        bandArray.addAll((ArrayList<String>) document.get("bands"));
                        bands.setText("Bands: ");
                        for(String band : bandArray)
                        {
                            db.collection("bands").document(band)
                                    .get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                            {
                                /**
                                 * This method is used to determine the completion of a get request of Firebase.
                                 * @param task References the result of the get request.
                                 */
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
     * This method is used to get the Musician's current rating from the database and create an appropriate display.
     */
    private void getRatingFromFirebase()
    {
        if(viewerType != null || viewerRef != null)
        {
            MUSICIANREFERENCE.document(mID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
                /**
                 * This method is used to determine the completion of a get request of Firebase.
                 * @param task References the result of the get request.
                 */
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                {
                    TextView ratingType = findViewById(R.id.my_rating);
                    TextView unrated = findViewById(R.id.unrated);

                    if(viewerType.equals("bands"))
                    {
                        String currentMusicianRating = task.getResult().get("musician-rating").toString();
                        rateMeButton.setText("  Rate Musician!  ");

                        if(currentMusicianRating.equals("N/A"))
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
                            ratingType.setText("My Musician Rating");
                            ratingType.setVisibility(View.VISIBLE);
                            musicianRatingBar.setRating(Float.valueOf(currentMusicianRating));
                            unrated.setVisibility(View.INVISIBLE);
                        }
                    }
                    else if(viewerType.equals("venues"))
                    {
                        String currentPerformerRating = task.getResult().get("performer-rating").toString();
                        rateMeButton.setText("  Rate Performer!  ");

                        if(currentPerformerRating.equals("N/A"))
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
                            ratingType.setText("My Performer Rating");
                            ratingType.setVisibility(View.VISIBLE);
                            musicianRatingBar.setRating(Float.valueOf(currentPerformerRating));
                            unrated.setVisibility(View.INVISIBLE);
                        }
                    }
                    else
                    {
                        System.out.println(TAG + " viewerType Error! viewerType ====== " + viewerType);
                    }
                }
            });
        }
        else if(AccountPurposeActivity.userType.equals("Venue"))
        {
            MUSICIANREFERENCE.document(mID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
                /**
                 * This method is used to determine the completion of a get request of Firebase.
                 * @param task References the result of the get request.
                 */
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                {
                    String currentPerformerRating = task.getResult().get("performer-rating").toString();
                    TextView ratingType = findViewById(R.id.my_rating);
                    TextView unrated = findViewById(R.id.unrated);

                    if(currentPerformerRating.equals("N/A"))
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
                        ratingType.setText("My Performer Rating");
                        ratingType.setVisibility(View.VISIBLE);
                        musicianRatingBar.setRating(Float.valueOf(currentPerformerRating));
                        unrated.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
        else
        {
            MUSICIANREFERENCE.document(mID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
                /**
                 * This method is used to determine the completion of a get request of Firebase.
                 * @param task References the result of the get request.
                 */
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                {
                    String currentMusicianRating = task.getResult().get("musician-rating").toString();
                    TextView ratingType = findViewById(R.id.my_rating);
                    TextView unrated = findViewById(R.id.unrated);

                    if(currentMusicianRating.equals("N/A"))
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
                        ratingType.setText("My Musician Rating");
                        ratingType.setVisibility(View.VISIBLE);
                        musicianRatingBar.setRating(Float.valueOf(currentMusicianRating));
                        unrated.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    /**
     * This method is used to set up the rating dialog for users if they have not rated the Musician yet.
     */
    private void setupRatingDialog()
    {
        if(viewerRef != null || viewerType != null)
        {
            rateMeButton.setOnClickListener(new View.OnClickListener()
            {
                /**
                 * This method is used to handle the click of the rateMeButton.
                 * @param v Represents the view.
                 */
                @Override
                public void onClick(View v) {
                    fader = findViewById(R.id.fader);
                    Window window = getWindow();
                    window.setStatusBarColor(ContextCompat.getColor(MusicianProfileActivity.this, R.color.darkerMain));
                    runOnUiThread(new Runnable()
                    {
                        /**
                         * This UI Thread is used to create the fade effect behind the dialog popup. Used in a separate thread for testing purposes.
                         */
                        @Override
                        public void run() {
                            fader.setVisibility(View.VISIBLE);
                        }
                    });

                    Intent intent = new Intent(MusicianProfileActivity.this, MusicianProfileRatingsDialog.class);
                    intent.putExtra("EXTRA_MUSICIAN_ID", mID);
                    intent.putExtra("EXTRA_VIEWER_REF", viewerRef);
                    intent.putExtra("EXTRA_VIEWER_TYPE", viewerType);
                    startActivityForResult(intent, 1);
                }
            });
        }
    }

    /**
     * Handle activity result, namely whether the musician has been rated or not by the viewer.
     * @param requestCode Represents the request code sent by the starting activity.
     * @param resultCode Represents the result code.
     * @param data Represents the intent passed back from the completed activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        fader.setVisibility(View.GONE);

        if(data != null && data.getBooleanExtra("EXTRA_HAS_RATED", true))
        {
            Toast.makeText(MusicianProfileActivity.this, "Rating Submitted!", Toast.LENGTH_SHORT).show();

            rateMeButton.setVisibility(View.GONE);

            viewer_rating_xml = findViewById(R.id.viewer_rating);
            viewer_rating_xml.setText("Thank you! You rated me " + data.getFloatExtra("EXTRA_RATING_RESULT", 0) + " stars!");
            viewer_rating_xml.setVisibility(View.VISIBLE);
            getRatingFromFirebase();
        }
        else
        {
            Toast.makeText(MusicianProfileActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method is used to check whether or not the user viewing the Musician has already submitted a rating.
     * Here we decide whether we will show the Rate Me button or an appropriate message.
     */
    private void checkAlreadyRated()
    {
        if(viewerRef != null || viewerType != null)
        {
            ratingDocReference = FSTORE.collection("ratings").document(viewerRef).collection(viewerType).document(mID);

            ratingDocReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
                /**
                 * This method is used to determine the completion of a get request of Firebase.
                 * @param task References the result of the get request.
                 */
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Object viewerRating = task.getResult().get("rating");

                    if (viewerRating != null) //Our rating isn't null and we have reviewed this Musician before.
                    {
                        System.out.println(TAG + " viewerRef found in ratedMap");

                        System.out.println(TAG + " Setting viewer rating on profile.");

                        viewer_rating_xml = findViewById(R.id.viewer_rating);

                        if (viewerType.equals("bands")) {
                            viewer_rating_xml.setText("You rated my Musician skills " + viewerRating.toString() + " stars!");
                        } else if (viewerType.equals("venues")) {
                            viewer_rating_xml.setText("You rated my Performance skills " + viewerRating.toString() + " stars!");
                        } else {
                            System.out.println(TAG + " viewerType Error! viewerType ====== " + viewerType);
                        }

                        viewer_rating_xml.setVisibility(View.VISIBLE);
                    } else {
                        //We haven't reviewed this Musician before.
                        Button rating_button_xml = findViewById(R.id.rating_button);
                        rating_button_xml.setVisibility(View.VISIBLE);
                    }
                }
            });
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
        musician.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            /**
             * This method is used to determine the completion of a get request of Firebase.
             * @param task References the result of the get request.
             */
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
