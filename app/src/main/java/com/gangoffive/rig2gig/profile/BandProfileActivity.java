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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BandProfileActivity extends AppCompatActivity {

    private String bID; //Band ID for  profile
    private String viewerType; //Can be null if viewer did not open the profile from communications.
    private String viewerRef;
    private final ArrayList<String> MEMBERARRAY = new ArrayList<>();
    private Button rateMeButton;
    private RatingBar bandRatingBar;
    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private final CollectionReference BANDREFERENCE = FSTORE.collection("bands");
    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";
    private DocumentReference ratingDocReference;
    private TextView viewer_rating_xml;
    private TextView fader;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_profile);

        /*Setting the support action bar to the newly created toolbar*/
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageView bandPhoto = findViewById(R.id.bandPhoto);
        final TextView bandName = findViewById(R.id.bandName);
        final TextView location = findViewById(R.id.location);
        final TextView description = findViewById(R.id.description);
        final TextView members = findViewById(R.id.members);

        /*Used to get the id of the band from the previous activity*/
        bID = getIntent().getStringExtra("EXTRA_BAND_ID");
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

        /*Finding the band by its ID in the "bands" subfolder*/
        DocumentReference band = db.collection("bands").document(bID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        band.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        bandName.setText(document.get("name").toString());
                        location.setText(document.get("location").toString());
                        description.setText(document.get("description").toString());
                        MEMBERARRAY.addAll((ArrayList<String>) document.get("members"));
                        members.setText("Members: ");

                        for(String member : MEMBERARRAY)
                        {
                            db.collection("musicians").document(member)
                                    .get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful())
                                    {
                                        DocumentSnapshot document = task.getResult();
                                        if(document.exists())
                                        {
                                            members.setText(members.getText().equals("Members: ") ?
                                                    "Members: " + document.get("name").toString() :
                                                    members.getText() + ", " + document.get("name").toString());
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
        StorageReference bandPic = storage.getReference().child("/images/bands/" + bID + ".jpg");

        /*Using Glide to load the picture from the reference directly into the ImageView*/

        GlideApp.with(this)
                .load(bandPic)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .into(bandPhoto);

        rateMeButton = findViewById(R.id.rating_button);
        bandRatingBar = findViewById(R.id.rating_bar);

        getRatingFromFirebase();
        setupRatingDialog();
        checkAlreadyRated();
    }

    /**
     * This method is used to get the Band's current rating from the database and create an appropriate display.
     * @since #0.2b
     */
    private void getRatingFromFirebase()
    {
        if(viewerType != null || viewerRef != null)
        {
            BANDREFERENCE.document(bID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
                /**
                 * This method is used to determine the completion of a get request of Firebase.
                 * @param task References the result of the get request.
                 * @since #0.2b
                 */
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                {
                    TextView ratingType = findViewById(R.id.my_rating);
                    TextView unrated = findViewById(R.id.unrated);

                    if(viewerType.equals("musicians"))
                    {
                        String currentMusicianRating = task.getResult().get("band-rating").toString();
                        rateMeButton.setText("  Rate Band!  ");

                        if(currentMusicianRating.equals("N/A"))
                        {
                            //We want to display an appropriate message to the user explaining there aren't enough ratings yet.
                            bandRatingBar.setRating(0);

                            unrated.setVisibility(View.VISIBLE);
                            ratingType.setText("Our Band Rating");
                            ratingType.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            //Else we want to show what the current rating is.
                            ratingType.setText("Our Band Rating");
                            ratingType.setVisibility(View.VISIBLE);
                            bandRatingBar.setRating(Float.valueOf(currentMusicianRating));
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
                            bandRatingBar.setRating(0);

                            unrated.setVisibility(View.VISIBLE);
                            ratingType.setText("Our Performer Rating");
                            ratingType.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            //Else we want to show what the current rating is.
                            ratingType.setText("Our Performer Rating");
                            ratingType.setVisibility(View.VISIBLE);
                            bandRatingBar.setRating(Float.valueOf(currentPerformerRating));
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
            BANDREFERENCE.document(bID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
                /**
                 * This method is used to determine the completion of a get request of Firebase.
                 * @param task References the result of the get request.
                 * @since #0.2b
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
                        bandRatingBar.setRating(0);

                        unrated.setVisibility(View.VISIBLE);
                        ratingType.setText("My Performer Rating");
                        ratingType.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        //Else we want to show what the current rating is.
                        ratingType.setText("My Performer Rating");
                        ratingType.setVisibility(View.VISIBLE);
                        bandRatingBar.setRating(Float.valueOf(currentPerformerRating));
                        unrated.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
        else
        {
            BANDREFERENCE.document(bID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
                /**
                 * This method is used to determine the completion of a get request of Firebase.
                 * @param task References the result of the get request.
                 * @since #0.2b
                 */
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                {
                    String currentBandRating = task.getResult().get("band-rating").toString();
                    TextView ratingType = findViewById(R.id.my_rating);
                    TextView unrated = findViewById(R.id.unrated);

                    if(currentBandRating.equals("N/A"))
                    {
                        //We want to display an appropriate message to the user explaining there aren't enough ratings yet.
                        bandRatingBar.setRating(0);

                        unrated.setVisibility(View.VISIBLE);
                        ratingType.setText("Our Band Rating");
                        ratingType.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        //Else we want to show what the current rating is.
                        ratingType.setText("Our Band Rating");
                        ratingType.setVisibility(View.VISIBLE);
                        bandRatingBar.setRating(Float.valueOf(currentBandRating));
                        unrated.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    /**
     * Handle activity result, namely whether the musician has been rated or not by the viewer.
     * @param requestCode Represents the request code sent by the starting activity.
     * @param resultCode Represents the result code.
     * @param data Represents the intent passed back from the completed activity.
     * @since #0.2b
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
            Toast.makeText(BandProfileActivity.this, "Rating Submitted!", Toast.LENGTH_SHORT).show();

            rateMeButton.setVisibility(View.GONE);

            viewer_rating_xml = findViewById(R.id.viewer_rating);
            viewer_rating_xml.setText("Thank you! You rated us " + data.getFloatExtra("EXTRA_RATING_RESULT", 0) + " stars!");
            viewer_rating_xml.setVisibility(View.VISIBLE);
            getRatingFromFirebase();
        }
        else
        {
            Toast.makeText(BandProfileActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method is used to set up the rating dialog for users if they have not rated a Band yet.
     * @since #0.2b
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
                 * @since #0.2b
                 */
                @Override
                public void onClick(View v) {
                    fader = findViewById(R.id.fader);
                    Window window = getWindow();
                    window.setStatusBarColor(ContextCompat.getColor(BandProfileActivity.this, R.color.darkerMain));
                    runOnUiThread(new Runnable()
                    {
                        /**
                         * This UI Thread is used to create the fade effect behind the dialog popup. Used in a separate thread for testing purposes.
                         * @since #0.2b
                         */
                        @Override
                        public void run()
                        {
                            fader.setVisibility(View.VISIBLE);
                        }
                    });

                    Intent intent = new Intent(BandProfileActivity.this, BandProfileRatingsDialog.class);
                    intent.putExtra("EXTRA_BAND_ID", bID);
                    intent.putExtra("EXTRA_VIEWER_REF", viewerRef);
                    intent.putExtra("EXTRA_VIEWER_TYPE", viewerType);
                    startActivityForResult(intent, 1);
                }
            });
        }
    }

    /**
     * This method is used to check whether or not the user viewing the Band has already submitted a rating.
     * Here we decide whether we will show the Rate Me button or an appropriate message.
     * @since #0.2b
     */
    private void checkAlreadyRated()
    {
        if(viewerRef != null || viewerType != null)
        {
            ratingDocReference = FSTORE.collection("ratings").document(viewerRef).collection(viewerType).document(bID);
            System.out.println(TAG + "BAND ID ==================== " + bID);

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
                    Object viewerRating = task.getResult().get("rating");

                    if (viewerRating != null) //Our rating isn't null and we have reviewed this Band before.
                    {
                        System.out.println(TAG + " viewerRef found in ratedMap");

                        System.out.println(TAG + " Setting viewer rating on profile.");

                        viewer_rating_xml = findViewById(R.id.viewer_rating);

                        if(viewerType.equals("musicians"))
                        {
                            viewer_rating_xml.setText("You rated our band skills " + viewerRating.toString() + " stars!");
                        }
                        else if(viewerType.equals("venues"))
                        {
                            viewer_rating_xml.setText("You rated our Performance skills " + viewerRating.toString() + " stars!");
                        }
                        else
                        {
                            System.out.println(TAG + " viewerType Error! viewerType ====== " + viewerType);
                        }

                        viewer_rating_xml.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        //We haven't reviewed this Band before.
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
     * @since #0.2b
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listing_menu, menu);

        AtomicBoolean currentUserInBand = new AtomicBoolean(false);

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        bID = getIntent().getStringExtra("EXTRA_BAND_ID");

        /*Firestore & Cloud Storage initialization*/
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        /*Finding the listing by its ID in the "performer-listings" subfolder*/
        DocumentReference band = db.collection("bands").document(bID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        band.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            /**
             * This method is used to determine the completion of a get request of Firebase.
             * @param task References the result of the get request.
             * @since #0.2b
             */
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());
                        MenuItem star = menu.findItem(R.id.saveButton);
                        star.setVisible(false);
                        getSupportActionBar().setTitle(document.get("name").toString());
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
