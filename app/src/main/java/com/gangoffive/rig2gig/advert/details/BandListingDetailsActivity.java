package com.gangoffive.rig2gig.advert.details;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.gangoffive.rig2gig.profile.BandProfileActivity;
import com.gangoffive.rig2gig.firebase.GlideApp;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BandListingDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String bID;
    private final Date expiry = new Date();
    private final StringBuilder bandRef = new StringBuilder("");
    private final StringBuilder listingOwner = new StringBuilder("");
    private final ArrayList<String> positionArray = new ArrayList<>();
    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";
    private GoogleMap googleMap;
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();

    /*Firestore & Cloud Storage initialization*/
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
            .clientId("AWpRTRqwsxyU-8X9zXOvNMTsgphAh7UzQz2jOt2kSE8S8OwLSsGSWsCVxvTXQq10JWGufT0bg9Dgspy3");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_listing_details);

        /*Setting the support action bar to the newly created toolbar*/
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        startService(intent);

        final ImageView bandPhoto = findViewById(R.id.bandPhoto);
        final TextView bandName = findViewById(R.id.bandName);
        final TextView rating = findViewById(R.id.rating);
        final TextView location = findViewById(R.id.location);
        final TextView position = findViewById(R.id.position);
        final TextView description = findViewById(R.id.description);
        final Button contact = findViewById(R.id.contact);
        final Button publish = findViewById(R.id.publish);
        final Button profile = findViewById(R.id.profile);
        final Button noInternet = findViewById(R.id.noInternet);

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        noInternet.setVisibility(isConnected ? View.GONE : View.VISIBLE);
        Source source = isConnected ? Source.SERVER : Source.CACHE;

        //Initialising the Google Map. See onMapReady().
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        /*Used to get the id of the listing from the previous activity*/
        bID = getIntent().getStringExtra("EXTRA_BAND_LISTING_ID");

        /*Finding the listing by its ID in the "band-listings" subfolder*/
        DocumentReference bandListing = db.collection("band-listings").document(bID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        bandListing.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        listingOwner.append(document.get("listing-owner").toString());

                        profile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(BandListingDetailsActivity.this, BandProfileActivity.class).putExtra("EXTRA_BAND_ID", document.get("band-ref").toString()));
                            }
                        });

                        /*Find the band reference by looking for the band ID in the "bands" subfolder*/
                        DocumentReference band = db.collection("bands").document(document.get("band-ref").toString());

                        band.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                request.put("sent-from-type", "musicians");
                                request.put("sent-from-ref", musician.getId());
                                request.put("sent-to-type", "bands");
                                request.put("sent-to-ref", bandRef.toString());
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
                                requestSent.put("sent-from-type", "musicians");
                                requestSent.put("sent-from-ref", musician.getId());
                                requestSent.put("sent-to-type", "bands");
                                requestSent.put("sent-to-ref", bandRef.toString());
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
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
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
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        /*Finding the listing by its ID in the "performer-listings" subfolder*/
        DocumentReference performerListing = db.collection("band-listings").document(bID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        performerListing.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        Timestamp expiryDate = (Timestamp) document.get("expiry-date");

                        /*Find the performer reference by looking for the performer ID in the "performers" subfolder*/
                        DocumentReference performer = db.collection("bands").document(document.get("band-ref").toString());

                        performer.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                                        ArrayList<String> members = (ArrayList<String>) document.get("members");

                                        Query musiciansInBand = db.collection("musicians").whereEqualTo("user-ref", FirebaseAuth.getInstance().getUid());
                                        musiciansInBand.get(source)
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful())
                                                        {
                                                            QuerySnapshot docs = task.getResult();
                                                            if(!docs.isEmpty())
                                                            {
                                                                if(members.contains(docs.getDocuments().get(0).getId()) && expiryDate.compareTo(Timestamp.now()) > 0)
                                                                {
                                                                    MenuItem star = menu.findItem(R.id.saveButton);
                                                                    star.setIcon(R.drawable.ic_full_star);
                                                                    star.setVisible(false);
                                                                    getSupportActionBar().setTitle("My Advert");
                                                                    Button contact = findViewById(R.id.contact);
                                                                    contact.setClickable(false);
                                                                    contact.setVisibility(View.GONE);
                                                                }
                                                                else if(members.contains(docs.getDocuments().get(0).getId()) && expiryDate.compareTo(Timestamp.now()) < 0)
                                                                {
                                                                    MenuItem star = menu.findItem(R.id.saveButton);
                                                                    star.setIcon(R.drawable.ic_full_star);
                                                                    star.setVisible(false);
                                                                    getSupportActionBar().setTitle("My Advert Preview");
                                                                    Button contact = findViewById(R.id.contact);
                                                                    contact.setClickable(false);
                                                                    contact.setVisibility(View.GONE);
                                                                    Button publish = findViewById(R.id.publish);
                                                                    publish.setClickable(true);
                                                                    publish.setVisibility(View.VISIBLE);
                                                                }
                                                                else
                                                                {
                                                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                                    CollectionReference favBands = db.collection("favourite-ads")
                                                                            .document(FirebaseAuth.getInstance().getUid())
                                                                            .collection("band-listings");
                                                                    favBands.document(bID).get(source)
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
            listing.put("band-ref", bandRef.toString());

            CollectionReference favBands = db.collection("favourite-ads")
                    .document(FirebaseAuth.getInstance().getUid())
                    .collection("band-listings");
            favBands.document(bID).get(source)
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

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.googleMap = googleMap;

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        final DocumentReference bandLocation = db.collection("band-listings").document(bID);

        final CollectionReference musiciansRef = db.collection("musicians");

        Query getMusiciansBands = musiciansRef;

        getMusiciansBands.whereEqualTo("user-ref", fAuth.getUid()).get(source).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if(task.isSuccessful())
                {
                    List<DocumentSnapshot> myMusicianID = task.getResult().getDocuments();

                    if (!myMusicianID.isEmpty())
                    {
                        DocumentSnapshot musician = myMusicianID.get(0);

                        ArrayList<String> bandsImIn = (ArrayList<String>) musician.get("bands");

                        if(bandsImIn != null && bandsImIn.size() > 0)
                        {
                            for(String bands : bandsImIn)
                            {
                                final CollectionReference bandRef = db.collection("bands");

                                bandRef.document(bands).get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            DocumentSnapshot bandSnapshot = task.getResult();

                                            String musicianName = bandSnapshot.get("name").toString();

                                            LatLng bandLocation = new LatLng(Double.parseDouble(bandSnapshot.get("latitude").toString()), Double.parseDouble(bandSnapshot.get("longitude").toString()));
                                            googleMap.addMarker(new MarkerOptions().position(bandLocation).title(musicianName));
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bandLocation, 10));
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }

    public void onBuyPressed(View pressed) {
        // PAYMENT_INTENT_SALE will cause the payment to complete immediately.
        // Change PAYMENT_INTENT_SALE to
        //   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
        //   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
        //     later via calls from your server.
        PayPalPayment payment = new PayPalPayment(new BigDecimal("5"), "GBP", "30-days Advert",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("paymentExample", confirm.toJSONObject().toString(4));

                    // TODO: send 'confirm' to your server for verification.

                    /*Firestore & Cloud Storage initialization*/
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseStorage storage = FirebaseStorage.getInstance();

                    /*Finding the listing by its ID in the "venue-listings" subfolder*/
                    DocumentReference bandListing = db.collection("band-listings").document(bID);

                    Calendar currentExpiry = Calendar.getInstance();
                    currentExpiry.setTime(expiry);
                    currentExpiry.add(Calendar.MONTH, 1);
                    currentExpiry.add(Calendar.DAY_OF_MONTH, 1);
                    Timestamp newDate = new Timestamp(currentExpiry.getTime());

                    bandListing.update("expiry-date", newDate);

                    Toast.makeText(this, "Ad published!", Toast.LENGTH_SHORT);

                    finish();

                } catch (JSONException e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
            Toast.makeText(this, "Payment process has been cancelled", Toast.LENGTH_SHORT);
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
        else
        {
            Toast.makeText(this, "Payment process has been cancelled", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}

