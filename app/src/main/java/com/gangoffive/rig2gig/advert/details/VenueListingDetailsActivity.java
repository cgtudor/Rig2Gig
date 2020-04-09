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

import com.gangoffive.rig2gig.firebase.GlideApp;
import com.gangoffive.rig2gig.navbar.NavBarActivity;
import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.profile.VenueProfileActivity;
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
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class VenueListingDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String vID;
    private String currentUserType;
    private String bandId = "";
    private final Date expiry = new Date();
    private final StringBuilder venueRef = new StringBuilder("");
    private final StringBuilder listingOwner = new StringBuilder("");
    private GoogleMap googleMap;
    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";

    /*Firestore & Cloud Storage initialization*/
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId("AWpRTRqwsxyU-8X9zXOvNMTsgphAh7UzQz2jOt2kSE8S8OwLSsGSWsCVxvTXQq10JWGufT0bg9Dgspy3");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_listing_details);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
        startService(intent);

        /*Setting the support action bar to the newly created toolbar*/
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageView venuePhoto = findViewById(R.id.venuePhoto);
        final TextView venueName = findViewById(R.id.venueName);
        final TextView description = findViewById(R.id.description);
        final TextView rating = findViewById(R.id.rating);
        final TextView location = findViewById(R.id.location);
        final Button contact = findViewById(R.id.contact);
        final Button publish = findViewById(R.id.publish);
        final Button profile = findViewById(R.id.profile);

        //Initialising the Google Map. See onMapReady().
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        /*Used to get the id of the listing from the previous activity*/
        vID = getIntent().getStringExtra("EXTRA_VENUE_LISTING_ID");

        if (getIntent().getStringExtra("CURRENT_USER_TYPE") != null) {
            currentUserType = getIntent().getStringExtra("CURRENT_USER_TYPE");
        } else {
            currentUserType = "";
        }

        if (currentUserType.equals("bands")) {
            bandId = getIntent().getStringExtra("CURRENT_BAND_ID");
        }

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

        /*Finding the listing by its ID in the "venue-listings" subfolder*/
        DocumentReference venueListing = db.collection("venue-listings").document(vID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        venueListing.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        Timestamp expiryDate = (Timestamp) document.get("expiry-date");

                        profile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(VenueListingDetailsActivity.this, VenueProfileActivity.class).putExtra("EXTRA_VENUE_ID", document.get("venue-ref").toString()));
                            }
                        });

                        /*Find the venue reference by looking for the venue ID in the "venues" subfolder*/
                        DocumentReference venue = db.collection("venues").document(document.get("venue-ref").toString());

                        venue.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                        sentMessages.whereEqualTo("sent-to", listingOwner.toString()).whereEqualTo("type", "contact-request").get(source)
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            QuerySnapshot query = task.getResult();
                                                            if (!query.isEmpty()) {
                                                                contact.setAlpha(.5f);
                                                                contact.setClickable(false);
                                                                contact.setText("Contact request sent");
                                                            }
                                                        } else {
                                                            Log.e("FIREBASE", "Sent messages failed with ", task.getException());
                                                        }
                                                    }
                                                });
                                        Log.d("AUTH CHECK", "LISTING OWNER: " + listingOwner.toString() + "\nCURRENT USER: " + FirebaseAuth.getInstance().getUid());
                                        if (listingOwner.toString().equals(FirebaseAuth.getInstance().getUid()) && expiryDate.compareTo(Timestamp.now()) > 0) {
                                            getSupportActionBar().setTitle("My Advert");
                                            contact.setClickable(false);
                                            contact.setVisibility(View.GONE);
                                        } else if (listingOwner.toString().equals(FirebaseAuth.getInstance().getUid()) && expiryDate.compareTo(Timestamp.now()) < 0) {
                                            getSupportActionBar().setTitle("My Advert Preview");
                                            contact.setClickable(false);
                                            contact.setVisibility(View.GONE);
                                            publish.setVisibility(View.VISIBLE);
                                            publish.setClickable(true);
                                        } else {
                                            getSupportActionBar().setTitle(venueName.getText().toString());
                                        }
                                    } else {
                                        Log.d("FIRESTORE", "No such document");
                                    }
                                } else {
                                    Log.d("FIRESTORE", "get failed with ", task.getException());
                                }
                            }
                        });
                        expiry.setTime(expiryDate.toDate().getTime());
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

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUserType.equals("musicians")) {
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
                                    request.put("sent-to-type", "venues");
                                    request.put("sent-to-ref", venueRef.toString());
                                    request.put("notification-title", "Someone is interested in your advert!");
                                    request.put("notification-message", musician.get("name").toString() + " is interested in you! Share contact details?");

                                    CollectionReference received = db.collection("communications")
                                            .document(listingOwner.toString())
                                            .collection("received");
                                    received.add(request)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("FIRESTORE", "Contact request added with info " + task.getResult().toString());
                                                        Toast.makeText(VenueListingDetailsActivity.this, "Contact request sent!", Toast.LENGTH_SHORT).show();
                                                        contact.setAlpha(.5f);
                                                        contact.setClickable(false);
                                                        contact.setText("Contact request sent");
                                                    } else {
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
                                    requestSent.put("sent-to-type", "venues");
                                    requestSent.put("sent-to-ref", venueRef.toString());
                                    requestSent.put("notification-title", "Someone is interested in your advert!");
                                    requestSent.put("notification-message", musician.get("name").toString() + " is interested in you! Share contact details?");
                                    CollectionReference sent = db.collection("communications")
                                            .document(FirebaseAuth.getInstance().getUid())
                                            .collection("sent");

                                    sent.add(requestSent)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("FIRESTORE", "Contact request sent with info " + task.getResult().toString());
                                                    } else {
                                                        Log.d("FIRESTORE", "Contact request sending failed with ", task.getException());
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });
                } else {
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
                                    request.put("sent-from-ref", bandId);
                                    request.put("sent-to-type", "venues");
                                    request.put("sent-to-ref", venueRef.toString());
                                    request.put("notification-title", "Someone is interested in your advert!");
                                    request.put("notification-message", musician.get("name").toString() + " is interested in you! Share contact details?");

                                    CollectionReference received = db.collection("communications")
                                            .document(listingOwner.toString())
                                            .collection("received");
                                    received.add(request)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("FIRESTORE", "Contact request added with info " + task.getResult().toString());
                                                        Toast.makeText(VenueListingDetailsActivity.this, "Contact request sent!", Toast.LENGTH_SHORT).show();
                                                        contact.setAlpha(.5f);
                                                        contact.setClickable(false);
                                                        contact.setText("Contact request sent");
                                                    } else {
                                                        Log.d("FIRESTORE", "Contact request failed with ", task.getException());
                                                    }
                                                }
                                            });

                                    HashMap<String, Object> requestSent = new HashMap<>();
                                    requestSent.put("type", "contact-request");
                                    requestSent.put("posting-date", Timestamp.now());
                                    requestSent.put("sent-to", listingOwner.toString());
                                    requestSent.put("sent-from-type", "bands");
                                    requestSent.put("sent-from-ref", bandId);
                                    requestSent.put("sent-to-type", "venues");
                                    requestSent.put("sent-to-ref", venueRef.toString());
                                    requestSent.put("notification-title", "Someone is interested in your advert!");
                                    requestSent.put("notification-message", musician.get("name").toString() + " is interested in you! Share contact details?");
                                    CollectionReference sent = db.collection("communications")
                                            .document(FirebaseAuth.getInstance().getUid())
                                            .collection("sent");

                                    sent.add(requestSent)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("FIRESTORE", "Contact request sent with info " + task.getResult().toString());
                                                    } else {
                                                        Log.d("FIRESTORE", "Contact request sending failed with ", task.getException());
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });
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
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false)
                .into(venuePhoto);
    }

    /**
     * Overriding the up navigation to call onBackPressed
     *
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

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        db.collection("users").document(FirebaseAuth.getInstance().getUid()).get(source)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String accType = document.get("user-type").toString();
                                if (accType.equals("Venue")) {
                                    Intent intent = new Intent(VenueListingDetailsActivity.this, NavBarActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    VenueListingDetailsActivity.this.genericBack();
                                    finish();
                                }
                            } else {
                                Log.d("FIRESTORE", "No such document");
                            }
                        } else {
                            Log.d("FIRESTORE", "get failed with ", task.getException());
                        }
                    }
                });
    }

    public void genericBack() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.listing_menu, menu);

        vID = getIntent().getStringExtra("EXTRA_VENUE_LISTING_ID");

        /*Firestore & Cloud Storage initialization*/
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        /*Finding the listing by its ID in the "performer-listings" subfolder*/
        DocumentReference performerListing = db.collection("venue-listings").document(vID);

        /*Retrieving information from the reference, listeners allow use to change what we do in case of success/failure*/
        performerListing.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                        /*Find the performer reference by looking for the performer ID in the "performers" subfolder*/
                        DocumentReference performer = db.collection("venues").document(document.get("venue-ref").toString());

                        performer.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                                        if (document.get("user-ref").toString().equals(FirebaseAuth.getInstance().getUid())) {
                                            MenuItem star = menu.findItem(R.id.saveButton);
                                            star.setIcon(R.drawable.ic_full_star);
                                            star.setVisible(false);
                                        } else {
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            CollectionReference favVenues = db.collection("favourite-ads")
                                                    .document(FirebaseAuth.getInstance().getUid())
                                                    .collection("venue-listings");
                                            favVenues.document(vID).get(source)
                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot document = task.getResult();
                                                                if (document.exists()) {
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


        if (id == R.id.saveButton) {
            Timestamp expiryDate = new Timestamp(expiry);

            HashMap<String, Object> listing = new HashMap<>();
            listing.put("description", description.getText().toString());
            listing.put("expiry-date", expiry);
            listing.put("venue-ref", venueRef.toString());

            CollectionReference favVenues = db.collection("favourite-ads")
                    .document(FirebaseAuth.getInstance().getUid())
                    .collection("venue-listings");
            favVenues.document(vID).get(source)
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    favVenues.document(vID)
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
                                } else {
                                    favVenues.document(vID)
                                            .set(listing)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("FIRESTORE", "Favourite successful");
                                                        Toast.makeText(VenueListingDetailsActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                                                        item.setIcon(R.drawable.ic_full_star);
                                                    } else {
                                                        Log.d("FIRESTORE", "Task failed with ", task.getException());
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Log.d("FIRESTORE", "Failed with: ", task.getException());
                            }
                        }
                    });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        final DocumentReference venueLocation = db.collection("venue-listings").document(vID);

        venueLocation.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Google Map get location successful");

                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Log.d(TAG, "Venue Document exists");



                        final DocumentReference venue = db.collection("venues").document(document.get("venue-ref").toString());

                        venue.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Log.d(TAG, "Google Map get venue successful");

                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Google Map get venue completed");

                                    DocumentSnapshot document = task.getResult();

                                    String venueName = document.get("name").toString();
                                    LatLng venueLocation = new LatLng(Double.parseDouble(document.get("latitude").toString()), Double.parseDouble(document.get("longitude").toString()));
                                    googleMap.addMarker(new MarkerOptions().position(venueLocation).title(venueName));
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(venueLocation, 16));
                                } else {
                                    Log.d(TAG, "Google Map get venue failed");
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Google Map get venue unsuccessful");
                            }
                        });
                    } else {
                        Log.d(TAG, "Venue Document does not exist");
                    }
                } else {
                    Log.d(TAG, "Google Map get location unsuccessful");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Google Map get location failed.");
            }
        });
    }

    public void onBuyPressed (View pressed){
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
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("paymentExample", confirm.toJSONObject().toString(4));

                    // TODO: send 'confirm' to your server for verification.

                    /*Firestore & Cloud Storage initialization*/
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();

                    /*Finding the listing by its ID in the "venue-listings" subfolder*/
                    DocumentReference venueListing = db.collection("venue-listings").document(vID);

                    Calendar currentExpiry = Calendar.getInstance();
                    Timestamp postingDate = new Timestamp(currentExpiry.getTime());
                    venueListing.update("posting-date", postingDate);

                    currentExpiry.setTime(expiry);
                    currentExpiry.add(Calendar.MONTH, 1);
                    currentExpiry.add(Calendar.DAY_OF_MONTH, 1);
                    Timestamp newDate = new Timestamp(currentExpiry.getTime());

                    venueListing.update("expiry-date", newDate);

                    Toast.makeText(this, "Ad published!", Toast.LENGTH_SHORT);

                    finish();

                } catch (JSONException e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
            Toast.makeText(this, "Payment process has been cancelled", Toast.LENGTH_SHORT);
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        } else {
            Toast.makeText(this, "Payment process has been cancelled", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onDestroy () {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}