package com.gangoffive.rig2gig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class VenueListingDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_listing_details);

        /*Setting the support action bar to the newly created toolbar*/
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageView venuePhoto = findViewById(R.id.bandPhoto);
        final TextView venueName = findViewById(R.id.bandName);
        final TextView description = findViewById(R.id.description);
        final TextView rating = findViewById(R.id.rating);
        final TextView location = findViewById(R.id.position);

        /*Used to get the id of the listing from the previous activity*/
        String vID = getIntent().getStringExtra("EXTRA_VENUE_LISTING_ID");

        /*Firestore & Cloud Storage initialization*/
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        /*Finding the listing by its ID in the "venue-listings" subfolder*/
        DocumentReference venueListing = db.collection("venue-listings").document(vID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        venueListing.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        /*Find the venue reference by looking for the venue ID in the "venues" subfolder*/
                        DocumentReference venue = db.collection("venues").document(document.get("venue-ref").toString());

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

                                        getSupportActionBar().setTitle(venueName.getText().toString());

                                    } else {
                                        Log.d("FIRESTORE", "No such document");
                                    }
                                } else {
                                    Log.d("FIRESTORE", "get failed with ", task.getException());
                                }
                            }
                        });
                        description.setText(document.get("description").toString());
                    } else {
                        Log.d("FIRESTORE", "No such document");
                    }
                } else {
                    Log.d("FIRESTORE", "get failed with ", task.getException());
                }
            }
        });

        //Temp wait for pic to upload
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*Find reference for the photo associated with the listing inside the according subtree*/
        StorageReference venuePic = storage.getReference().child("/images/venue-listings/" + vID + ".jpg");

        /*Using Glide to load the picture from the reference directly into the ImageView*/

        GlideApp.with(this)
                .load(venuePic)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(venuePhoto);
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

    /**
     * Checks the user-type. Redirects to console if it is a venue or to the previous activity/fragment if not.
     */
    @Override
    public void onBackPressed() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(FirebaseAuth.getInstance().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            DocumentSnapshot document = task.getResult();
                            if(document.exists())
                            {
                                String accType = document.get("user-type").toString();
                                if(accType.equals("Venue"))
                                {
                                    Intent intent = new Intent(VenueListingDetailsActivity.this, NavBarActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    VenueListingDetailsActivity.this.onBackPressed();
                                    finish();
                                }
                            }
                            else
                            {
                                Log.d("FIRESTORE", "No such document");
                            }
                        }
                        else
                        {
                            Log.d("FIRESTORE", "get failed with ", task.getException());
                        }
                    }
                });
    }
}
