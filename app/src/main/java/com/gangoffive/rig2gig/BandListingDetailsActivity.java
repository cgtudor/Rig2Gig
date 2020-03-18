package com.gangoffive.rig2gig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class BandListingDetailsActivity extends AppCompatActivity {

    private String bID;
    private final StringBuilder expiry = new StringBuilder("");
    private final StringBuilder bandRef = new StringBuilder("");
    private final StringBuilder listingOwner = new StringBuilder("");
    private final ArrayList<String> positionArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_listing_details);

        /*Setting the support action bar to the newly created toolbar*/
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageView bandPhoto = findViewById(R.id.bandPhoto);
        final TextView bandName = findViewById(R.id.bandName);
        final TextView rating = findViewById(R.id.rating);
        final TextView location = findViewById(R.id.location);
        final TextView position = findViewById(R.id.position);
        final TextView description = findViewById(R.id.description);
        final Button contact = findViewById(R.id.contact);

        /*Used to get the id of the listing from the previous activity*/
        bID = getIntent().getStringExtra("EXTRA_BAND_LISTING_ID");

        /*Firestore & Cloud Storage initialization*/
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        /*Finding the listing by its ID in the "band-listings" subfolder*/
        DocumentReference bandListing = db.collection("band-listings").document(bID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        bandListing.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        /*Find the band reference by looking for the band ID in the "bands" subfolder*/
                        DocumentReference band = db.collection("bands").document(document.get("band-ref").toString());

                        band.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                                        bandName.setText(document.get("name").toString());
                                        rating.setText("Rating: " + document.get("rating").toString() + "/5");
                                        location.setText(document.get("location").toString());
                                        ArrayList<String> members = (ArrayList<String>) document.get("members");
                                        listingOwner.append(members.get(0));

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
                                        Log.d("AUTH CHECK" ,"LISTING OWNER: " + listingOwner.toString() + "\nCURRENT USER: " + FirebaseAuth.getInstance().getUid());
                                        Query musiciansInBand = db.collection("musicians").whereEqualTo("user-ref", FirebaseAuth.getInstance().getUid());
                                        musiciansInBand.get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            QuerySnapshot docs = task.getResult();
                                                            if(!docs.isEmpty())
                                                            {
                                                                if(members.contains(docs.getDocuments().get(0).getId()))
                                                                {
                                                                    getSupportActionBar().setTitle("My Advert");
                                                                    contact.setClickable(false);
                                                                    contact.setVisibility(View.GONE);
                                                                }
                                                                else
                                                                {
                                                                    getSupportActionBar().setTitle(bandName.getText().toString());
                                                                }
                                                            }
                                                            else
                                                            {
                                                                Log.d("FIRESTORE", "User not a musician!");
                                                            }
                                                        }
                                                        else
                                                        {
                                                            Log.d("FIRESTORE", "query failed with" , task.getException());
                                                        }
                                                    }
                                                });
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
                        bandRef.append(document.get("band-ref").toString());
                        description.setText(document.get("description").toString());
                        positionArray.addAll((ArrayList<String>) document.get("position"));
                        position.setText(document.get("position").toString().substring(1, document.get("position").toString().length()-1));
                    } else {
                        Log.d("FIRESTORE", "No such document");
                    }
                } else {
                    Log.d("FIRESTORE", "get failed with ", task.getException());
                }
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("musicians").whereEqualTo("user-ref", FirebaseAuth.getInstance().getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot musicians = task.getResult();
                            if (!musicians.isEmpty()) {
                                DocumentSnapshot musician = musicians.getDocuments().get(0);

                                HashMap<String, Object> request = new HashMap<>();
                                request.put("type", "contact-request");
                                request.put("posting-date", Timestamp.now());
                                request.put("sent-from", FirebaseAuth.getInstance().getUid());
                                request.put("notification-title", "Someone is interested in your advert!");
                                request.put("notification-message", musician.get("name").toString() + " is interested in you! Share contact details?");

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
                                                    Toast.makeText(BandListingDetailsActivity.this, "Contact request sent!", Toast.LENGTH_SHORT).show();
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
                                requestSent.put("notification-title", "Someone is interested in your advert!");
                                requestSent.put("notification-message", musician.get("name").toString() + " is interested in you! Share contact details?");
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
                        }
                    }});
            }
        });

        //Temp wait for pic to upload
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*Find reference for the photo associated with the listing inside the according subtree*/
        StorageReference bandPic = storage.getReference().child("/images/band-listings/" + bID + ".jpg");

        /*Using Glide to load the picture from the reference directly into the ImageView*/

        GlideApp.with(this)
                .load(bandPic)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(bandPhoto);
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
     * Checks the user-type. Redirects to console if it is a band or to the previous activity/fragment if not.
     */
    @Override
    public void onBackPressed() {
        genericBack();
    }

    public void genericBack()
    {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listing_menu, menu);

        bID = getIntent().getStringExtra("EXTRA_BAND_LISTING_ID");

        /*Firestore & Cloud Storage initialization*/
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        /*Finding the listing by its ID in the "performer-listings" subfolder*/
        DocumentReference performerListing = db.collection("band-listings").document(bID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        performerListing.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        /*Find the performer reference by looking for the performer ID in the "performers" subfolder*/
                        DocumentReference performer = db.collection("bands").document(document.get("band-ref").toString());

                        performer.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                                        ArrayList<String> members = (ArrayList<String>) document.get("members");

                                        Query musiciansInBand = db.collection("musicians").whereEqualTo("user-ref", FirebaseAuth.getInstance().getUid());
                                        musiciansInBand.get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            QuerySnapshot docs = task.getResult();
                                                            if(!docs.isEmpty())
                                                            {
                                                                if(members.contains(docs.getDocuments().get(0).getId()))
                                                                {
                                                                    MenuItem star = menu.findItem(R.id.saveButton);
                                                                    star.setIcon(R.drawable.ic_full_star);
                                                                    star.setVisible(false);
                                                                }
                                                                else
                                                                {
                                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                                    CollectionReference favBands = db.collection("favourite-ads")
                                                                            .document(FirebaseAuth.getInstance().getUid())
                                                                            .collection("band-listings");
                                                                    favBands.document(bID).get()
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                    if(task.isSuccessful())
                                                                                    {
                                                                                        DocumentSnapshot document = task.getResult();
                                                                                        if(document.exists())
                                                                                        {
                                                                                            MenuItem star = menu.findItem(R.id.saveButton);
                                                                                            star.setIcon(R.drawable.ic_full_star);
                                                                                        }
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                            else
                                                            {
                                                                Log.d("FIRESTORE", "User not a musician!");
                                                            }
                                                        }
                                                        else
                                                        {
                                                            Log.d("FIRESTORE", "query failed with" , task.getException());
                                                        }
                                                    }
                                                });
                                    } else {
                                        Log.d("FIRESTORE", "No such document");
                                    }
                                } else {
                                    Log.d("FIRESTORE", "get failed with ", task.getException());
                                }
                            }
                        });
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        TextView description = findViewById(R.id.description);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(id == R.id.saveButton)
        {
            HashMap<String, Object> listing = new HashMap<>();
            listing.put("position", positionArray);
            listing.put("description", description.getText().toString());
            listing.put("expiry-date", expiry.toString());
            listing.put("band-ref", bandRef.toString());

            CollectionReference favBands = db.collection("favourite-ads")
                    .document(FirebaseAuth.getInstance().getUid())
                    .collection("band-listings");
            favBands.document(bID).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                DocumentSnapshot document = task.getResult();
                                if(document.exists())
                                {
                                    favBands.document(bID)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("FIRESTORE", "Favourite successfully deleted!");
                                                    item.setIcon(R.drawable.ic_empty_star);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("FIRESTORE", "Error deleting document", e);
                                                }
                                            });
                                }
                                else
                                {
                                    favBands.document(bID)
                                            .set(listing)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Log.d("FIRESTORE", "Favourite successful");
                                                        Toast.makeText(BandListingDetailsActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                                                        item.setIcon(R.drawable.ic_full_star);
                                                    }
                                                    else
                                                    {
                                                        Log.d("FIRESTORE", "Task failed with ", task.getException());
                                                    }
                                                }
                                            });
                                }
                            }
                            else
                            {
                                Log.d("FIRESTORE", "Failed with: ", task.getException());
                            }
                        }
                    });
        }
        return super.onOptionsItemSelected(item);
    }
}
