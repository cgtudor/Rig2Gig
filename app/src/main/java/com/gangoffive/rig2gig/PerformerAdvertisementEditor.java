package com.gangoffive.rig2gig;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerformerAdvertisementEditor extends AppCompatActivity implements CreateAdvertisement {

    private TextView distance, name;
    private Button createListing, cancel, galleryImage, takePhoto;
    private ImageView image;
    private String performerRef, performerType, listingRef;
    private HashMap<String, Object> listing;
    private Map<String, Object> band, previousListing;
    private ListingManager listingManager;
    private Drawable chosenPic;
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
                createListing.setBackgroundColor(Color.parseColor("#B2BEB5"));
                createListing.setTextColor(Color.parseColor("#4D4D4E"));
            }
            else if (before == 0 && count == 1 && createListing != null
                    && name.getText().toString().trim().length() > 0
            )
            {
                createListing.setBackgroundColor(Color.parseColor("#008577"));
                createListing.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };


    /**
     * setup view and listing manager, initiating getting performer info from database
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        performerRef = getIntent().getStringExtra("EXTRA_PERFORMER_ID");
        performerType = getIntent().getStringExtra("EXTRA_PERFORMER_TYPE");
        listingRef = getIntent().getStringExtra("EXTRA_LISTING_ID");

        setContentView(R.layout.activity_create_performer_advertisement);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listingManager = new ListingManager(performerRef, performerType + " Performer", listingRef);
        listingManager.getUserInfo(this);
    }

    /**
     * Populate view if database request was successful
     * @param data performer data
     */
    @Override
    public void onSuccessFromDatabase(Map<String, Object> data) {
        setViewReferences();
        band = data;
        listingManager.getImage(this);
    }

    /**
     * Populate view if database request was successful
     * @param data performer data
     * @param listingData existing listing data
     */
    @Override
    public void onSuccessFromDatabase(Map<String, Object> data, Map<String, Object> listingData) {

        setViewReferences();
        band = data;
        previousListing = listingData;
        listingManager.getImage(this);
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
        name = findViewById(R.id.name);
        image = findViewById(R.id.image);
        if (image != null)
        {
            image.setImageDrawable(null);
        }
        distance = findViewById(R.id.distance);
        if (distance != null)
        {
            distance.addTextChangedListener(textWatcher);
        }
        createListing = findViewById(R.id.createListing);
        createListing.setBackgroundColor(Color.parseColor("#008577"));
        createListing.setTextColor(Color.parseColor("#FFFFFF"));
        createListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {createAdvertisement();}
        });
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {cancelAdvertisement();}
        });
        galleryImage = findViewById(R.id.galleryImage);
        galleryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {ImageRequestHandler.getGalleryImage(v);}
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
            name.setText(band.get("name").toString());
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
            Toast.makeText(PerformerAdvertisementEditor.this,
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
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(PerformerAdvertisementEditor.this,"Advertisement created successfully",
                            Toast.LENGTH_LONG).show();
                }
            });
            Intent intent = new Intent(this, PerformanceListingDetailsActivity.class);
            intent.putExtra("EXTRA_PERFORMANCE_LISTING_ID", listingManager.getListingRef());
            startActivity(intent);
            finish();
        } else if (creationResult == ListingManager.CreationResult.LISTING_FAILURE) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(PerformerAdvertisementEditor.this,
                            "Listing creation failed.  Check your connection " +
                                    "and try again",
                            Toast.LENGTH_LONG).show();
                }
            });

        } else if (creationResult == ListingManager.CreationResult.IMAGE_FAILURE) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(PerformerAdvertisementEditor.this,
                            "Listing creation failed.  Check your connection " +
                                    "and try again",
                            Toast.LENGTH_LONG).show();
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
        listing.put("distance", distance.getText().toString());
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

    public Map<String, Object> getPerformer() {
        return band;
    }

    public Map<String, Object> getPreviousListing() {
        return previousListing;
    }

    public void setListingManager(ListingManager listingManager) {
        this.listingManager = listingManager;
    }

    public void setListing(HashMap<String, Object> listing) {
        this.listing = listing;
    }

    public void setPerformer(Map<String, Object> band) {
        this.band = band;
    }

    public void setPreviousListing(Map<String, Object> previousListing) {
        this.previousListing = previousListing;
    }
}

