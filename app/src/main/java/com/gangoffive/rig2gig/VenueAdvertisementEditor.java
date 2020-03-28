package com.gangoffive.rig2gig;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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


    private TextView name, description;
    private Button createListing, cancel, galleryImage, takePhoto;
    private ImageView image;
    private String venueRef, type, listingRef;
    private HashMap<String, Object> listing;
    private Map<String, Object> venue, previousListing;
    private ListingManager listingManager;
    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_create_venue_advertisement_image,
            R.layout.fragment_create_venue_advertisement_details};
    private Drawable chosenPic;
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
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore FSTORE = FirebaseFirestore.getInstance();
    private final CollectionReference venueReference = FSTORE.collection("venues");
    private final Query getVenueLocation = venueReference;
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
        type = "Venue";
        listingManager = new ListingManager(venueRef, type, listingRef);
        listingManager.getUserInfo(this);
        getVenueLocation();
    }

    /**
     * Populate view if database request was successful
     * @param data band data
     */
    @Override
    public void onSuccessFromDatabase(Map<String, Object> data) {
        setViewReferences();
        if (description.getText().toString().trim().length() == 0 && createListing != null) {
            createListing.setBackgroundColor(Color.parseColor("#12c2e9"));
            createListing.setTextColor(Color.parseColor("#FFFFFF"));
        }
        venue = data;
        listingManager.getImage(this);
    }

    /**
     * Populate view if database request was successful
     * @param data band data
     */
    @Override
    public void onSuccessFromDatabase(Map<String, Object> data, Map<String, Object> listingData)
    {
        setViewReferences();
        venue = data;
        previousListing = listingData;
        listingManager.getImage(this);
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
        name = findViewById(R.id.venue_name_final);
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
        createListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAdvertisement();
            }
        });
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
            description.setText(previousListing.get("description").toString());
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
     * create advertisement, posting to database
     */
    @Override
    public void createAdvertisement() {
        listingDataMap();
        if (chosenPic == null)
        {
            chosenPic = image.getDrawable();
        }
        if (validateDataMap()) {
            listingManager.postDataToDatabase(listing, chosenPic, this);
        } else {
            Toast.makeText(VenueAdvertisementEditor.this,
                    "Listing not created.  Ensure all fields are complete " +
                            "and try again",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * handle response from posting to database
     * @param creationResult Enum representing success/failure
     */
    @Override
    public void handleDatabaseResponse(Enum creationResult) {
        if (creationResult == ListingManager.CreationResult.SUCCESS) {
            if (listingRef.equals(""))
            {
                Toast.makeText(this,"Advertisement created successfully",
                        Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this,"Advertisement edited successfully",
                        Toast.LENGTH_LONG).show();
            }
            Intent intent = new Intent(VenueAdvertisementEditor.this, VenueListingDetailsActivity.class);
            intent.putExtra("EXTRA_VENUE_LISTING_ID", listingManager.getListingRef());
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
        } else if (creationResult == ListingManager.CreationResult.LISTING_FAILURE) {
            Toast.makeText(VenueAdvertisementEditor.this,
                    "Listing creation failed.  Check your connection " +
                            "and try again",
                    Toast.LENGTH_LONG).show();
        } else if (creationResult == ListingManager.CreationResult.IMAGE_FAILURE) {
            Toast.makeText(VenueAdvertisementEditor.this,
                    "Listing creation failed.  Check your connection " +
                            "and try again",
                    Toast.LENGTH_LONG).show();
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
}