package com.gangoffive.rig2gig.advert.management;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.advert.details.PerformanceListingDetailsActivity;
import com.gangoffive.rig2gig.firebase.ListingManager;
import com.gangoffive.rig2gig.utils.ImageRequestHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerformerAdvertisementEditor extends AppCompatActivity implements CreateAdvertisement {

    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.SYSTEM_ALERT_WINDOW","android.permission.CAMERA"};
    private TextView distance, name;
    private Button createListing, cancel, galleryImage, takePhoto;
    private ImageView image;
    private String performerRef, performerType, listingRef, editType;
    private HashMap<String, Object> listing;
    private Map<String, Object> band, previousListing;
    private ListingManager listingManager;
    private Drawable chosenPic;
    private boolean finalCheck;
    private View.OnClickListener confirm = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            createListing.setOnClickListener(null);
            createAdvertisement();
        }
    };
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int leadingZeros = 0;
            String distanceValue = distance.getText().toString();
            while (true)
            {
                if (distanceValue.length() != 0 && distanceValue.length() > leadingZeros && distanceValue.charAt(leadingZeros) == '0')
                {
                    leadingZeros++;
                }
                else
                {
                    break;
                }
            }
            String actualNumber = distanceValue.substring(leadingZeros,distanceValue.length());
            if (createListing != null && (s.toString().trim().length() == 0 ||
                    actualNumber.length() == 0))
            {
                createListing.setBackgroundColor(Color.parseColor("#a6a6a6"));
                createListing.setTextColor(Color.parseColor("#FFFFFF"));
            }
            else if (before == 0 && count == 1 && createListing != null
                    && name.getText().toString().trim().length() > 0
            )
            {
                createListing.setBackgroundColor(Color.parseColor("#12c2e9"));
                createListing.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private double performerLatitude;
    private double performerLongitude;
    private FirebaseAuth fAuth;
    private FirebaseFirestore FSTORE;
    private CollectionReference musicianReference;
    private Query getPerformerLocation;
    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";

    /**
     * setup view and listing manager, initiating getting performer info from database
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finalCheck = false;
        performerRef = getIntent().getStringExtra("EXTRA_PERFORMER_ID");
        performerType = getIntent().getStringExtra("EXTRA_PERFORMER_TYPE");
        listingRef = getIntent().getStringExtra("EXTRA_LISTING_ID");
        if (listingRef != null && listingRef.equals(""))
        {
            editType = "creation";
        }
        else
        {
            editType = "edit";
        }
        setContentView(R.layout.activity_create_performer_advertisement);
        listingManager = new ListingManager(performerRef, performerType + " Performer", listingRef);
        listingManager.getUserInfo(this);
        fAuth = FirebaseAuth.getInstance();
        FSTORE = FirebaseFirestore.getInstance();
        musicianReference = FSTORE.collection("musicians");
        getPerformerLocation = musicianReference;
        getPerformerLocation();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 2);
        }
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Performer Advert");
        /*Setting the support action bar to the newly created toolbar*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Populate view if database request was successful
     * @param data performer data
     */
    @Override
    public void onSuccessFromDatabase(Map<String, Object> data) {
        if (finalCheck)
        {
            postToDatabase(data);
        }
        else
        {
            setViewReferences();
            band = data;
            listingManager.getImage(this);
        }
    }

    /**
     * Populate view if database request was successful
     * @param data performer data
     * @param listingData existing listing data
     */
    @Override
    public void onSuccessFromDatabase(Map<String, Object> data, Map<String, Object> listingData) {
        if (finalCheck)
        {
            postToDatabase(data);
        }
        else
        {
            setViewReferences();
            band = data;
            previousListing = listingData;
            listingManager.getImage(this);
        }
    }

    /**
     * Populate view if database request was successful
     */
    @Override
    public void onSuccessfulImageDownload() {populateInitialFields();}

    /**
     * set references to text and image views and buttons
     */
    @Override
    public void setViewReferences() {
        name = findViewById(R.id.firstName);
        image = findViewById(R.id.image);
        if (image != null)
        {
            image.setImageDrawable(null);
        }
        distance = findViewById(R.id.venue_description_final);
        if (distance != null)
        {
            distance.addTextChangedListener(textWatcher);
        }
        createListing = findViewById(R.id.createListing);
        createListing.setBackgroundColor(Color.parseColor("#12c2e9"));
        createListing.setTextColor(Color.parseColor("#FFFFFF"));
        createListing.setOnClickListener(confirm);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {cancelAdvertisement();}
        });
        galleryImage = findViewById(R.id.galleryImage);
        galleryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageRequestHandler.getGalleryImage(v);}
        });
        takePhoto = findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {ImageRequestHandler.getCameraImage(v);}
        });
    }

    /**
     * populate text views
     */
    @Override
    public void populateInitialFields() {
        if(name != null && band !=null && name.getText() != band.get("name") && name.getText() == "")
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    name.setText(band.get("name").toString());
                }
            });
        }
        if (chosenPic != null && image != null)
        {
            image.setImageDrawable(chosenPic);
        }
        if(distance != null && previousListing !=null)
        {
            distance.setText(previousListing.get("distance").toString());
        }
        else if (distance != null)
        {
            distance.setText(band.get("distance").toString());
        }
    }

    /**
     * handles activity results
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        image = ImageRequestHandler.handleResponse(requestCode, resultCode, data, image);
        chosenPic = image.getDrawable();
    }

    /**
     * create advertisement, begin final checks before posting to database
     */
    @Override
    public void createAdvertisement() {
        listingDataMap();
        if (chosenPic == null)
        {
            chosenPic = image.getDrawable();
        }
        if (validateDataMap()) {
            finalCheck = true;
            listingManager.getUserInfo(this);
        } else {
            createListing.setOnClickListener(confirm);
            Toast.makeText(PerformerAdvertisementEditor.this,
                    "Advertisement " + editType + " unsuccessful.  Ensure all fields are complete " +
                            "and try again",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Post advert to database
     * @param data advert data
     */
    public void postToDatabase(Map<String, Object> data)
    {
        if (data != null)
        {
            listing.put("genres",data.get("genres"));
            listing.put("rating",data.get("rating"));
            listingManager.postDataToDatabase(listing, chosenPic, this);
        }
        else
        {
            createListing.setOnClickListener(confirm);
            Toast.makeText(PerformerAdvertisementEditor.this,
                    "Advertisement " + editType + " unsuccessful.  Check your connection " +
                            "and try again",
                    Toast.LENGTH_SHORT).show();
            finalCheck = false;
        }
    }

    /**
     * handle response from posting to database
     * @param creationResult Enum representing success/failure
     */
    @Override
    public void handleDatabaseResponse(Enum creationResult) {
        if (creationResult == ListingManager.CreationResult.SUCCESS) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(PerformerAdvertisementEditor.this,"Advertisement " + editType + " successful",
                            Toast.LENGTH_SHORT).show();
                }
            });
            Intent intent = new Intent(this, PerformanceListingDetailsActivity.class);
            intent.putExtra("EXTRA_PERFORMANCE_LISTING_ID", listingManager.getListingRef());
            startActivity(intent);
            finish();
        } else if (creationResult == ListingManager.CreationResult.LISTING_FAILURE) {
            createListing.setOnClickListener(confirm);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(PerformerAdvertisementEditor.this,
                            "Advertisement " + editType + " failed.  Check your connection " +
                                    "and try again",
                            Toast.LENGTH_SHORT).show();
                }
            });

        } else if (creationResult == ListingManager.CreationResult.IMAGE_FAILURE) {
            createListing.setOnClickListener(confirm);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(PerformerAdvertisementEditor.this,
                            "Advertisement " + editType + " failed.  Check your connection " +
                                    "and try again",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * cancel advertisement creation
     */
    @Override
    public void cancelAdvertisement()
    {
        finish();
    }

    /**
     * Finish activity on back pressed
     */
    @Override
    public void onBackPressed()
    {
        finish();
    }

    /**
     * populate listing map with combination of values from text views and map generated from database
     */
    public void listingDataMap() {
        if (listing == null) {
            listing = new HashMap<>();
            listing.put("performer-ref", performerRef);
            listing.put("performer-type", performerType);
            listing.put("listing-owner", FirebaseAuth.getInstance().getUid());
        }
        String distanceText = distance.getText().toString();
        for (int i = 0; i < distanceText.length(); i++)
        {
            char digit = distanceText.charAt(i);
            if (digit!= '0')
            {
                distanceText = distanceText.substring(i);
                break;
            }
            if (i == distanceText.length()-1)
            {
                distanceText = "0";
            }
        }
        distance.setText(distanceText);
        listing.put("distance", distanceText);
        listing.put("latitude", performerLatitude);
        listing.put("longitude", performerLongitude);
    }

    /**
     * validate data in listing map
     * @return true if valid
     */
    @Override
    public boolean validateDataMap() {
        for (Map.Entry element : listing.entrySet()) {
            String val = element.getValue().toString();
            if (val == null || val.trim().isEmpty()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PerformerAdvertisementEditor.this,
                                "Advertisement " + editType + " unsuccessful.  Ensure all fields are complete " +
                                        "and try again",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
        }
        int leadingZeros = 0;
        if (distance != null)
        {
            String distanceValue = distance.getText().toString();
            while (true)
            {
                if (distanceValue.length() != 0 && distanceValue.length() > leadingZeros && distanceValue.charAt(leadingZeros) == '0')
                {
                    leadingZeros++;
                }
                else
                {
                    break;
                }
            }
            String actualNumber = distanceValue.substring(leadingZeros,distanceValue.length());
            if (actualNumber.length() == 0)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PerformerAdvertisementEditor.this,
                                "Advertisement " + editType + " unsuccessful.  Distance cannot be '0'",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
        }
        return true;
    }

    /**
     * @return image
     */
    public ImageView getImageView() {
        return image;
    }

    /**
     * @return performer
     */
    public Map<String, Object> getPerformer() {
        return band;
    }

    /**
     * @return previousListing
     */
    public Map<String, Object> getPreviousListing() {
        return previousListing;
    }

    /**
     * @param listingManager listingManager to set
     */
    public void setListingManager(ListingManager listingManager) {
        this.listingManager = listingManager;
    }

    /**
     * @param listing listing to set
     */
    public void setListing(HashMap<String, Object> listing) {
        this.listing = listing;
    }

    /**
     * @param band band to set
     */
    public void setPerformer(Map<String, Object> band) {
        this.band = band;
    }

    /**
     * @param previousListing previousListing to set
     */
    public void setPreviousListing(Map<String, Object> previousListing) {
        this.previousListing = previousListing;
    }

    /**
     * Get location of performer
     */
    private void getPerformerLocation()
    {
        getPerformerLocation.whereEqualTo("user-ref", fAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                Log.d(TAG, "Successfully obtained Venue reference.");

                List<DocumentSnapshot> venues = queryDocumentSnapshots.getDocuments();

                if(!venues.isEmpty())
                {
                    Log.d(TAG, "Successful get of venue.");
                    DocumentSnapshot venue = venues.get(0);

                    performerLatitude = Double.parseDouble(venue.get("latitude").toString());
                    performerLongitude = Double.parseDouble(venue.get("longitude").toString());
                }
                else
                {
                    Log.d(TAG, "Unsuccessful get of venue.");
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.d(TAG, "Failed to get Venue reference.");
            }
        });
    }

    /**
     * Jandle menu item selection
     * @param item item selected
     * @return true if item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}

