package com.gangoffive.rig2gig.advert.details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.gangoffive.rig2gig.firebase.GlideApp;
import com.gangoffive.rig2gig.profile.MusicianProfileActivity;
import com.gangoffive.rig2gig.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MusicianListingDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String mID;
    private String currentBandId = "";
    private final Date expiry = new Date();
    private final StringBuilder musicianRef = new StringBuilder("");
    private final StringBuilder listingOwner = new StringBuilder("");
    private final ArrayList<String> positionArray = new ArrayList<>();

    private GoogleMap googleMap;
    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";

    /*Firestore & Cloud Storage initialization*/
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musician_listing_details);

        /*Setting the support action bar to the newly created toolbar*/
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageView musicianPhoto = findViewById(R.id.musicianPhoto);
        final TextView musicianName = findViewById(R.id.musicianName);
        final TextView description = findViewById(R.id.description);
        final TextView rating = findViewById(R.id.rating);
        final TextView location = findViewById(R.id.location);
        final TextView distance = findViewById(R.id.venue_description_final);
        final TextView position = findViewById(R.id.position);
        final Button contact = findViewById(R.id.contact);
        final Button profile = findViewById(R.id.profile);

        //Initialising the Google Map. See onMapReady().
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        /*Used to get the id of the listing from the previous activity*/
        mID = getIntent().getStringExtra("EXTRA_MUSICIAN_LISTING_ID");

        currentBandId = getIntent().getStringExtra("CURRENT_BAND_ID");

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

        /*Finding the listing by its ID in the "musician-listings" subfolder*/
        DocumentReference musicianListing = db.collection("musician-listings").document(mID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        musicianListing.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        profile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(MusicianListingDetailsActivity.this, MusicianProfileActivity.class).putExtra("EXTRA_MUSICIAN_ID", document.get("musician-ref").toString()));
                            }
                        });

                        /*Find the musician reference by looking for the musician ID in the "musicians" subfolder*/
                        DocumentReference musician = db.collection("musicians").document(document.get("musician-ref").toString());

                        musician.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                                        musicianName.setText(document.get("name").toString());
                                        rating.setText("Rating: " + document.get("rating").toString() + "/5");
                                        location.setText(document.get("location").toString());
                                        distance.setText("Distance willing to travel: " + document.get("distance").toString() + " miles");
                                        listingOwner.append(document.get("user-ref").toString());

                                        CollectionReference sentMessages = db.collection("communications").document(FirebaseAuth.getInstance().getUid()).collection("sent");
                                        sentMessages.whereEqualTo("sent-to", listingOwner.toString()).whereEqualTo("type", "contact-request").get(source)
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
                                        if(listingOwner.toString().equals(FirebaseAuth.getInstance().getUid()))
                                        {
                                            getSupportActionBar().setTitle("My Advert");
                                            contact.setClickable(false);
                                            contact.setVisibility(View.GONE);
                                        }
                                        else
                                        {
                                            getSupportActionBar().setTitle(musicianName.getText().toString());
                                        }
                                    } else {
                                        Log.d("FIRESTORE", "No such document");
                                    }
                                } else {
                                    Log.d("FIRESTORE", "get failed with ", task.getException());
                                }
                            }
                        });
                        Timestamp expiryDate = (Timestamp) document.get("expiry-date");
                        expiry.setTime(expiryDate.toDate().getTime());
                        musicianRef.append(document.get("musician-ref").toString());
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
                        .get(source).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                                request.put("sent-from-type", "bands");
                                request.put("sent-from-ref", currentBandId);
                                request.put("sent-to-type", "musicians");
                                request.put("sent-to-ref", musicianRef.toString());
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
                                                    Toast.makeText(MusicianListingDetailsActivity.this, "Contact request sent!", Toast.LENGTH_SHORT).show();
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
                                requestSent.put("sent-from-type", "band");
                                requestSent.put("sent-from-ref", currentBandId);
                                requestSent.put("sent-to-type", "musicians");
                                requestSent.put("sent-to-ref", musicianRef.toString());
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
        StorageReference musicianPic = storage.getReference().child("/images/musician-listings/" + mID + ".jpg");

        /*Using Glide to load the picture from the reference directly into the ImageView*/

        GlideApp.with(this)
                .load(musicianPic)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .into(musicianPhoto);
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
     * Checks the user-type. Redirects to console if it is a musician or to the previous activity/fragment if not.
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

        mID = getIntent().getStringExtra("EXTRA_MUSICIAN_LISTING_ID");

        /*Firestore & Cloud Storage initialization*/
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        /*Finding the listing by its ID in the "performer-listings" subfolder*/
        DocumentReference performerListing = db.collection("musician-listings").document(mID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        performerListing.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        /*Find the performer reference by looking for the performer ID in the "performers" subfolder*/
                        DocumentReference performer = db.collection("musicians").document(document.get("musician-ref").toString());

                        performer.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                                        if(document.get("user-ref").toString().equals(FirebaseAuth.getInstance().getUid()))
                                        {
                                            MenuItem star = menu.findItem(R.id.saveButton);
                                            star.setIcon(R.drawable.ic_full_star);
                                            star.setVisible(false);
                                        }
                                        else
                                        {
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            CollectionReference favMusicians = db.collection("favourite-ads")
                                                    .document(FirebaseAuth.getInstance().getUid())
                                                    .collection("musician-listings");
                                            favMusicians.document(mID).get(source)
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
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        if(id == R.id.saveButton)
        {
            Timestamp expiryDate = new Timestamp(expiry);

            HashMap<String, Object> listing = new HashMap<>();
            listing.put("position", positionArray);
            listing.put("description", description.getText().toString());
            listing.put("expiry-date", expiry);
            listing.put("musician-ref", musicianRef.toString());

            CollectionReference favMusicians = db.collection("favourite-ads")
                    .document(FirebaseAuth.getInstance().getUid())
                    .collection("musician-listings");
            favMusicians.document(mID).get(source)
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                DocumentSnapshot document = task.getResult();
                                if(document.exists())
                                {
                                    favMusicians.document(mID)
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
                                    favMusicians.document(mID)
                                            .set(listing)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Log.d("FIRESTORE", "Favourite successful");
                                                        Toast.makeText(MusicianListingDetailsActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        this.googleMap = googleMap;

        final DocumentReference musicianLocation = db.collection("musician-listings").document(mID);

        musicianLocation.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    Log.d(TAG, "Google Map get location successful");

                    DocumentSnapshot document = task.getResult();

                    if(document.exists())
                    {
                        Log.d(TAG, "Musician Document exists");



                        final DocumentReference musician = db.collection("musicians").document(document.get("musician-ref").toString());

                        musician.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task)
                            {
                                Log.d(TAG, "Google Map get musician successful");

                                if(task.isSuccessful())
                                {
                                    Log.d(TAG, "Google Map get musician completed");

                                    DocumentSnapshot document = task.getResult();

                                    String musicianName = document.get("name").toString();
                                    LatLng musicianLocation = new LatLng(Double.parseDouble(document.get("latitude").toString()), Double.parseDouble(document.get("longitude").toString()));
                                    googleMap.addMarker(new MarkerOptions().position(musicianLocation).title(musicianName));
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(musicianLocation, 10));
                                }
                                else
                                {
                                    Log.d(TAG, "Google Map get musician failed");
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Log.d(TAG, "Google Map get musician unsuccessful");
                            }
                        });
                    }
                    else
                    {
                        Log.d(TAG, "Musician Document does not exist");
                    }
                }
                else
                {
                    Log.d(TAG, "Google Map get location unsuccessful");
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d(TAG, "Google Map get location failed.");
            }
        });
    }
}
