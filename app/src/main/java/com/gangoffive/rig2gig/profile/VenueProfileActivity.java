package com.gangoffive.rig2gig.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import java.util.HashMap;

public class VenueProfileActivity extends AppCompatActivity {
    private String vID; //Venue ID for  profile
    private String viewerType; //Can be null if viewer did not open the profile from communications.
    private String viewerRef;
    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private final CollectionReference VENUEREFERENCE = FSTORE.collection("venues");
    private Button rateMeButton;
    private RatingBar venueRatingBar;
    private DocumentReference ratingDocReference;
    private TextView viewer_rating_xml;
    private TextView fader;

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

        final Button noInternet = findViewById(R.id.noInternet);

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        noInternet.setVisibility(isConnected ? View.GONE : View.VISIBLE);

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        /*Finding the venue by its ID in the "venue-listings" subfolder*/
        DocumentReference venue = db.collection("venues").document(vID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        venue.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
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

                        venueName.setText(document.get("name").toString());
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
     * @since #0.2b
     */
    private void getRatingFromFirebase()
    {
        VENUEREFERENCE.document(vID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            /**
             * This method is used to determine the completion of a get request of Firebase.
             * @param task References the result of the get request.
             * @since #0.2b
             */
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                String currentVenueRating = task.getResult().get("venue-rating").toString();
                TextView unrated = findViewById(R.id.unrated);

                if(currentVenueRating.equals("N/A"))
                {
                    //We want to display an appropriate message to the user explaining there aren't enough ratings yet.
                    venueRatingBar.setRating(0);
                    unrated.setVisibility(View.VISIBLE);
                }
                else
                {
                    //Else we want to show what the current rating is.
                    venueRatingBar.setRating(Float.valueOf(currentVenueRating));
                    unrated.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * Handle activity result, namely whether the venue has been rated or not by the viewer.
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
            Toast.makeText(VenueProfileActivity.this, "Rating Submitted!", Toast.LENGTH_SHORT).show();

            rateMeButton.setVisibility(View.GONE);

            viewer_rating_xml = findViewById(R.id.viewer_rating);
            viewer_rating_xml.setText("Thank you! You rated us " + data.getFloatExtra("EXTRA_RATING_RESULT", 0) + " stars!");
            viewer_rating_xml.setVisibility(View.VISIBLE);
            getRatingFromFirebase();
        }
        else
        {
            Toast.makeText(VenueProfileActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method is used to set up the rating dialog for users if they have not rated a Venue yet.
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
                    window.setStatusBarColor(ContextCompat.getColor(VenueProfileActivity.this, R.color.darkerMain));
                    runOnUiThread(new Runnable()
                    {
                        /**
                         * This UI Thread is used to create the fade effect behind the dialog popup. Used in a separate thread for testing purposes.
                         * @since #0.2b
                         */
                        @Override
                        public void run() {
                            fader.setVisibility(View.VISIBLE);
                        }
                    });

                    Intent intent = new Intent(VenueProfileActivity.this, VenueProfileRatingsDialog.class);
                    intent.putExtra("EXTRA_VENUE_ID", vID);
                    intent.putExtra("EXTRA_VIEWER_REF", viewerRef);
                    intent.putExtra("EXTRA_VIEWER_TYPE", viewerType);
                    startActivityForResult(intent, 1);
                }
            });
        }
    }

    /**
     * This method is used to check whether or not the user viewing the Venue has already submitted a rating.
     * Here we decide whether we will show the Rate Me button or an appropriate message.
     * @since #0.2b
     */
    private void checkAlreadyRated()
    {
        if(viewerRef != null || viewerType != null)
        {
            ratingDocReference = FSTORE.collection("ratings").document(viewerRef).collection(viewerType).document(vID);

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

                    if (viewerRating != null) //Our rating isn't null and we have reviewed this Venue before.
                    {
                        viewer_rating_xml = findViewById(R.id.viewer_rating);
                        viewer_rating_xml.setText("You rated us " + viewerRating.toString() + " stars!");
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

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        /*Finding the listing by its ID in the "venues" subfolder*/
        DocumentReference venue = db.collection("venues").document(vID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        venue.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                /**
                 * This method is used to determine the completion of a get request of Firebase.
                 * @param task References the result of the get request.
                 * @since #0.2b
                 */
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
