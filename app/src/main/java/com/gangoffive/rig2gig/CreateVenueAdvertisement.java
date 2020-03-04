package com.gangoffive.rig2gig;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.gangoffive.rig2gig.ui.TabbedView.SectionsPagerAdapter;
import java.util.HashMap;
import java.util.Map;

public class CreateVenueAdvertisement extends AppCompatActivity implements CreateAdvertisement, TabbedViewReferenceInitialiser {


    private TextView name, description;
    private Button createListing, cancel, galleryImage, takePhoto;
    private ImageView image;
    private String venueRef, type;
    private HashMap<String, Object> listing;
    private Map<String, Object> venue;
    private ListingManager listingManager;
    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_create_venue_advertisement_image,
            R.layout.fragment_create_venue_advertisement_details};
    private Drawable chosenPic;

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

        venueRef = "cavWo1C735Rft0NHvhcL";
        type = "Venue";


        listingManager = new ListingManager(venueRef, type);
        listingManager.getUserInfo(this);
    }

    /**
     * Populate view if database request was successful
     * @param data band data
     */
    @Override
    public void onSuccessFromDatabase(Map<String, Object> data) {
        setViewReferences();
        venue = data;
        listingManager.getImage(this);
    }

    /**
     * Populate view if database request was successful
     */
    @Override
    public void onSuccessfulImageDownload() {
        populateInitialFields();
        saveTabs();
    }

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
        description = findViewById(R.id.genres);
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
    }

    /**
     * Save values of tabs that may be destroyed
     */
    @Override
    public void saveTabs()
    {
        if (image != null && image.getDrawable() != null)
        {
            chosenPic = (image.getDrawable());
        }
        if (description != null && description.getText() == null)
        {
            listing.put("description",description.getText().toString());
        }
        reinitialiseTabs();
    }

    /**
     * Reinitialise values of tabs that may have been destroyed
     */
    @Override
    public void reinitialiseTabs() {
        setViewReferences();
        populateInitialFields();
        if (description != null && description.getText() == null)
        {
            description.setText(listing.get("description").toString());
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
        if (validateDataMap()) {
            listingManager.postDataToDatabase(listing, chosenPic, this);
        } else {
            Toast.makeText(CreateVenueAdvertisement.this,
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
            Intent intent = new Intent(CreateVenueAdvertisement.this, VenueListingDetailsActivity.class);
            intent.putExtra("EXTRA_VENUE_LISTING_ID", listingManager.getListingRef());
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
        } else if (creationResult == ListingManager.CreationResult.LISTING_FAILURE) {
            Toast.makeText(CreateVenueAdvertisement.this,
                    "Listing creation failed.  Check your connection " +
                            "and try again",
                    Toast.LENGTH_LONG).show();
        } else if (creationResult == ListingManager.CreationResult.IMAGE_FAILURE) {
            Toast.makeText(CreateVenueAdvertisement.this,
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
        Intent backToMain = new Intent(CreateVenueAdvertisement.this,
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
}