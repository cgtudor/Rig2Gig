package com.gangoffive.rig2gig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.google.android.gms.tasks.OnCompleteListener;
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

import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class VenueListingDetailsActivity extends AppCompatActivity {

    private Button favourite;
    private final StringBuilder expiry = new StringBuilder("");
    private final StringBuilder venueRef = new StringBuilder("");
    private final StringBuilder listingOwner = new StringBuilder("");

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
        favourite = findViewById(R.id.favourite);
        final Button contact = findViewById(R.id.contact);

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
                                        listingOwner.append(document.get("user-ref").toString());

                                        CollectionReference sentMessages = db.collection("communications").document(FirebaseAuth.getInstance().getUid()).collection("sent");
                                        sentMessages.whereEqualTo("sent-to", listingOwner.toString()).whereEqualTo("type", "contact-request").get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            QuerySnapshot query = task.getResult();
                                                            if(!query.isEmpty())
                                                            {
                                                                contact.setAlpha(.5f);
                                                                contact.setClickable(false);
                                                                contact.setText("Contact request sent");
                                                            }
                                                        }
                                                        else
                                                        {
                                                            Log.e("FIREBASE", "Sent messages failed with ", task.getException());
                                                        }
                                                    }
                                                });

                                        getSupportActionBar().setTitle(venueName.getText().toString());
                                    } else {
                                        Log.d("FIRESTORE", "No such document");
                                    }
                                } else {
                                    Log.d("FIRESTORE", "get failed with ", task.getException());
                                }
                            }
                        });
                        Timestamp expiryDate = (Timestamp) document.get("expiry-date");
                        expiry.append(expiryDate.toDate().toString());
                        venueRef.append(document.get("venue-ref").toString());
                        description.setText(document.get("description").toString());
                    } else {
                        Log.d("FIRESTORE", "No such document");
                    }
                } else {
                    Log.d("FIRESTORE", "get failed with ", task.getException());
                }
            }
        });

        /*On clicking the favourite button we save the listing in the database and then we grey out the button*/
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> listing = new HashMap<>();
                listing.put("description", description.getText().toString());
                listing.put("expiry-date", expiry.toString());
                listing.put("venue-ref", venueRef.toString());

                CollectionReference favVenues = db.collection("favourite-ads")
                        .document(FirebaseAuth.getInstance().getUid())
                        .collection("venue-listings");
                favVenues.document(vID)
                        .set(listing)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    Log.d("FIRESTORE", "Favourite successful");
                                    Toast.makeText(VenueListingDetailsActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                                    favourite.setAlpha(.5f);
                                    favourite.setClickable(false);
                                }
                                else
                                {
                                    Log.d("FIRESTORE", "Task failed with ", task.getException());
                                }
                            }
                        });
            }
        });

        /*If the listing already exists in the users favourites, then we grey out the button on create*/
        CollectionReference favVenues = db.collection("favourite-ads")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("venue-listings");
        favVenues.document(vID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            DocumentSnapshot document = task.getResult();
                            if(document.exists())
                            {
                                favourite.setAlpha(.5f);
                                favourite.setClickable(false);
                            }
                        }
                    }
                });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> request = new HashMap<>();
                request.put("type", "contact-request");
                request.put("posting-date", Timestamp.now());
                request.put("sent-from", FirebaseAuth.getInstance().getUid());

                CollectionReference received = db.collection("communications")
                        .document(listingOwner.toString())
                        .collection("received");
                received.add(request)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if(task.isSuccessful())
                                {
                                    Log.d("FIRESTORE", "Contact request added with info " + task.getResult().toString());
                                    Toast.makeText(VenueListingDetailsActivity.this, "Contact request sent!", Toast.LENGTH_SHORT).show();
                                    contact.setAlpha(.5f);
                                    contact.setClickable(false);
                                    contact.setText("Contact request sent");
                                }
                                else
                                {
                                    Log.d("FIRESTORE", "Contact request failed with ", task.getException());
                                }
                            }
                        });

                HashMap<String, Object> requestSent = new HashMap<>();
                requestSent.put("type", "contact-request");
                requestSent.put("posting-date", Timestamp.now());
                requestSent.put("sent-to", listingOwner.toString());
                CollectionReference sent = db.collection("communications")
                        .document(FirebaseAuth.getInstance().getUid())
                        .collection("sent");

                sent.add(requestSent)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if(task.isSuccessful())
                                {
                                    Log.d("FIRESTORE", "Contact request sent with info " + task.getResult().toString());
                                }
                                else
                                {
                                    Log.d("FIRESTORE", "Contact request sending failed with ", task.getException());
                                }
                            }
                        });
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
