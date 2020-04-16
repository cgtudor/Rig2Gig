package com.gangoffive.rig2gig.advert.management;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.advert.details.VenueListingDetailsActivity;
import com.gangoffive.rig2gig.firebase.ListingManager;
import com.gangoffive.rig2gig.utils.ImageRequestHandler;
import com.gangoffive.rig2gig.utils.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.gangoffive.rig2gig.ui.TabbedView.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VenueAdvertisementEditor extends AppCompatActivity implements CreateAdvertisement {

    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.SYSTEM_ALERT_WINDOW","android.permission.CAMERA"};
    private TextView name, description;
    private Button createListing, cancel, galleryImage, takePhoto;
    private ImageView image;
    private String venueRef, type, listingRef, editType;
    private HashMap<String, Object> listing;
    private Map<String, Object> venue, previousListing;
    private ListingManager listingManager;
    private boolean finalCheck;
    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_create_venue_advertisement_image,
            R.layout.fragment_create_venue_advertisement_details};
    private Drawable chosenPic;
    private View.OnClickListener confirm = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createListing.setOnClickListener(null);
            createAdvertisement();
        }
    };
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().trim().length() == 0 && createListing != null) {
                createListing.setBackgroundColor(Color.parseColor("#a6a6a6"));
                createListing.setTextColor(Color.parseColor("#FFFFFF"));
            }
            else if (before == 0 && count == 1 && createListing != null
                    && description.getText().toString().trim().length() > 0)
            {
                createListing.setBackgroundColor(Color.parseColor("#12c2e9"));
                createListing.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };
    private double venueLatitude;
    private double venueLongitude;
    private FirebaseAuth fAuth;
    private FirebaseFirestore FSTORE;
    private CollectionReference venueReference;
    private Query getVenueLocation;
    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_venue_advertisement);
        tabTitles = new int[]{R.string.image, R.string.details};
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter
                (this, getSupportFragmentManager(), tabTitles, fragments);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        venueRef = getIntent().getStringExtra("EXTRA_VENUE_ID");
        listingRef = getIntent().getStringExtra("EXTRA_LISTING_ID");
        if (listingRef != null && listingRef.equals(""))
        {
            editType = "creation";
        }
        else
        {
            editType = "edit";
        }
        type = "Venue";
        finalCheck = false;
        listingManager = new ListingManager(venueRef, type, listingRef);
        listingManager.getUserInfo(this);
        fAuth = FirebaseAuth.getInstance();
        FSTORE = FirebaseFirestore.getInstance();
        venueReference = FSTORE.collection("venues");
        getVenueLocation = venueReference;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 2);
        }

        getVenueLocation();
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Venue Advert");
        /*Setting the support action bar to the newly created toolbar*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Populate view if database request was successful
     * @param data band data
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
            setInitialColours();
            venue = data;
            listingManager.getImage(this);
        }
    }

    /**
     * Set initial colours of confirm button
     */
    public void setInitialColours()
    {
        if (createListing != null)
        {
            if (previousListing == null)
            {
                createListing.setBackgroundColor(Color.parseColor("#a6a6a6"));
                createListing.setTextColor(Color.parseColor("#FFFFFF"));
            }
            else
            {
                createListing.setBackgroundColor(Color.parseColor("#12c2e9"));
                createListing.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }
    }

    /**
     * Populate view if database request was successful
     * @param data band data
     */
    @Override
    public void onSuccessFromDatabase(Map<String, Object> data, Map<String, Object> listingData)
    {
        if (finalCheck)
        {
            postToDatabase(data);
        }
        else{
            setViewReferences();
            venue = data;
            previousListing = listingData;
            setInitialColours();
            listingManager.getImage(this);
        }
    }

    /**
     * Populate view if database request was successful
     */
    @Override
    public void onSuccessfulImageDownload() {
        populateInitialFields();
    }

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
        description = findViewById(R.id.venue_description_final);
        if (description != null)
        {
            description.addTextChangedListener(textWatcher);
        }
        createListing = findViewById(R.id.createListing);
        createListing.setOnClickListener(confirm);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAdvertisement();
            }
        });
        galleryImage = findViewById(R.id.galleryImage);
        if (galleryImage != null)
        {
            galleryImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageRequestHandler.getGalleryImage(v);
                }
            });
        }
        takePhoto = findViewById(R.id.takePhoto);
        if (takePhoto != null)
        {
            takePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageRequestHandler.getCameraImage(v);
                }
            });
        }
    }

    /**
     * populate text views
     */
    @Override
    public void populateInitialFields() {
        if(name != null && venue !=null && name.getText() != venue.get("name") && name.getText() == "")
        {
            name.setText(venue.get("name").toString());
        }
        if (chosenPic != null && image != null)
        {
            image.setImageDrawable(chosenPic);
        }
        if(description != null && previousListing !=null)
        {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    description.setText(previousListing.get("description").toString());

                }
            });
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
     * create advertisement, then begin final checks before posting to database
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
            Toast.makeText(VenueAdvertisementEditor.this,
                    "Advertisement " + editType + " unsuccessful.  Ensure all fields are complete " +
                            "and try again",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Post advert to database
     * @param data advert to be posted
     */
    public void postToDatabase(Map<String, Object> data)
    {
        if (data != null)
        {
            listing.put("venue-type",data.get("venue-type"));
            listing.put("rating",data.get("rating"));
            listingManager.postDataToDatabase(listing, chosenPic, this);
        }
        else
        {
            createListing.setOnClickListener(confirm);
            Toast.makeText(VenueAdvertisementEditor.this,
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
                    Toast.makeText(VenueAdvertisementEditor.this,"Advertisement " + editType + " successful",
                            Toast.LENGTH_SHORT).show();
                }
            });
            Intent intent = new Intent(VenueAdvertisementEditor.this, VenueListingDetailsActivity.class);
            intent.putExtra("EXTRA_VENUE_LISTING_ID", listingManager.getListingRef());
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
        } else if (creationResult == ListingManager.CreationResult.LISTING_FAILURE) {
            createListing.setOnClickListener(confirm);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(VenueAdvertisementEditor.this,
                            "Advertisement " + editType + " failed.  Check your connection " +
                                    "and try again",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else if (creationResult == ListingManager.CreationResult.IMAGE_FAILURE) {
            createListing.setOnClickListener(confirm);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(VenueAdvertisementEditor.this,
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
    public void cancelAdvertisement() {
        Intent backToMain = new Intent(VenueAdvertisementEditor.this,
                MainActivity.class);
        startActivity(backToMain);
        finish();
    }

    /**
     * populate listing map with combination of values from text views and map generated from database
     */
    public void listingDataMap() {
        if (listing == null) {
            listing = new HashMap<>();
            listing.put("venue-ref", venueRef);
        }
        if(description != null)
        {
            listing.put("description", description.getText().toString());
        }


        listing.put("latitude", venueLatitude);
        listing.put("longitude", venueLongitude);
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
                return false;
            }
        }
        return true;
    }

    /**
     * get image
     * @return image
     */
    public ImageView getImageView() {
        return image;
    }

    /**
     * @return venue
     */
    public Map<String, Object> getVenue() {return venue;}

    /**
     * @return previousListing
     */
    public Map<String, Object> getPreviousListing() {return previousListing;}

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
     * @param venue venue to set
     */
    public void setVenue(Map<String, Object> venue) {
        this.venue = venue;
    }

    /**
     * @param previousListing previousListing to set
     */
    public void setPreviousListing(Map<String, Object> previousListing) {
        this.previousListing = previousListing;
    }

    /**
     * @param listingRef listingRef to set
     */
    public void setListingRef(String listingRef) {
        this.listingRef = listingRef;
    }

    /**
     * get location of venue
     */
    private void getVenueLocation()
    {
        getVenueLocation.whereEqualTo("user-ref", fAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                Log.d(TAG, "Successfully obtained Venue reference.");

                List<DocumentSnapshot> venues = queryDocumentSnapshots.getDocuments();

                if(!venues.isEmpty())
                {
                    Log.d(TAG, "Successful get of venue.");
                    DocumentSnapshot venue = venues.get(0);

                    venueLatitude = Double.parseDouble(venue.get("latitude").toString());
                    venueLongitude = Double.parseDouble(venue.get("longitude").toString());
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
     * handle menu item selection
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