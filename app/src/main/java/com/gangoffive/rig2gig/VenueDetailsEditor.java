package com.gangoffive.rig2gig;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.gangoffive.rig2gig.ui.TabbedView.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VenueDetailsEditor extends AppCompatActivity implements CreateAdvertisement, TabbedViewReferenceInitialiser {

    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.SYSTEM_ALERT_WINDOW","android.permission.CAMERA"};
    private boolean mapping, tab2set;
    private Geocoder geocoder;
    private TextView name, description, venueType, email, phone;
    private AutoCompleteTextView location;
    private Button createListing, cancel, galleryImage, takePhoto;
    private ImageView image;
    private String venueRef, type;
    private Map<String, Object> venue;
    private ListingManager listingManager;
    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_image_changer,
                               R.layout.fragment_venue_details_changer,
                               R.layout.fragment_description_changer};
    private Drawable chosenPic;
    private TabStatePreserver tabPreserver = new TabStatePreserver(this);
    private View.OnFocusChangeListener editTextFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            tabPreserver.onFocusChange(hasFocus);
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
                    && name.getText().toString().trim().length() > 0
                    && location.getText().toString().trim().length() > 0
                    && venueType.getText().toString().trim().length() > 0
                    && email.getText().toString().trim().length() > 0
                    && phone.getText().toString().trim().length() > 0
                    && description.getText().toString().trim().length() > 0)
            {
                createListing.setBackgroundColor(Color.parseColor("#12c2e9"));
                createListing.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed_editor_layout);
        tabTitles = new int[]{R.string.image, R.string.details, R.string.description};
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter
                (this, getSupportFragmentManager(), tabTitles, fragments);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        venueRef = getIntent().getStringExtra("EXTRA_VENUE_ID");
        String listingRef = "profileEdit";
        type = "Venue";
        mapping = false;
        listingManager = new ListingManager(venueRef, type, listingRef);
        listingManager.getUserInfo(this);
        geocoder = new Geocoder(this, Locale.getDefault());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 2);
        }
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Edit Details");
        /*Setting the support action bar to the newly created toolbar*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Populate view if database request was successful
     * @param data band data
     */
    @Override
    public void onSuccessFromDatabase(Map<String, Object> data)
    {
        setViewReferences();
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
        //not required as not editing a listing
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
        image = findViewById(R.id.detailsImage);
        if (image != null)
        {
            image.setImageDrawable(null);
        }
        if(!tab2set)
        {
            name = findViewById(R.id.venue_name_final);
            if (name != null)
            {
                name.setOnFocusChangeListener(editTextFocusListener);
                name.addTextChangedListener(textWatcher);

            }
            location = findViewById(R.id.venue_location);
            if (location != null)
            {
                if(location.getAdapter() == null)
                {
                    location.setAdapter(new GooglePlacesAutoSuggestAdapter(VenueDetailsEditor.this, android.R.layout.simple_list_item_1));
                }

                location.setOnFocusChangeListener(editTextFocusListener);
                location.addTextChangedListener(textWatcher);
            }
            venueType = findViewById(R.id.type);
            if (venueType != null)
            {
                venueType.setOnFocusChangeListener(editTextFocusListener);
                venueType.addTextChangedListener(textWatcher);
            }
            email = findViewById(R.id.email);
            if (email != null)
            {
                email.setOnFocusChangeListener(editTextFocusListener);
                email.addTextChangedListener(textWatcher);
            }
            phone = findViewById(R.id.phone);
            if (phone != null)
            {
                phone.setOnFocusChangeListener(editTextFocusListener);
                phone.addTextChangedListener(textWatcher);

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
        }

        description = findViewById(R.id.venue_description_final);
        if (description != null)
        {
            description.setOnFocusChangeListener(editTextFocusListener);
            description.addTextChangedListener(textWatcher);
        }

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
        if (chosenPic != null && image != null)
        {
            image.setImageDrawable(chosenPic);
        }
        if(!tab2set) {
            if(name != null && venue !=null)
            {
                name.setText(venue.get("name").toString());
            }

            if(location != null && venue !=null)
            {
                try
                {
                    List<Address> getVenueAddress = geocoder.getFromLocation(Double.parseDouble(venue.get("latitude").toString()), Double.parseDouble(venue.get("longitude").toString()), 1);

                    if(getVenueAddress != null && getVenueAddress.size() > 0)
                    {
                        String street = getVenueAddress.get(0).getAddressLine(0);
                        location.setText(street);
                    }
                }
                catch(IOException io)
                {
                    System.out.println(io.getMessage());
                }


                //location.setText(venue.get("location").toString());
            }
            if(venueType != null && venue !=null)
            {
                venueType.setText(venue.get("venue-type").toString());
            }
            if(email != null && venue !=null)
            {
                email.setText(venue.get("email-address").toString());
            }
            if(phone != null && venue !=null)
            {
                phone.setText(venue.get("phone-number").toString());
                tab2set = true;
            }
        }
        if(description != null && venue !=null)
        {
            description.setText(venue.get("description").toString());

        }

    }

    /**
     * Save values of tabs that may be destroyed
     */
    @Override
    public void saveTabs()
    {
        mapping = true;
        if (image != null && image.getDrawable() != null)
        {
            chosenPic = (image.getDrawable());
        }
        listingDataMap();
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
            description.setText(venue.get("description").toString());
        }
    }

    @Override
    public void beginTabPreservation() {
        tabPreserver.preserveTabState();
    }

    @Override
    public void setMapping(boolean isMapping) {
        mapping = isMapping;
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
        saveTabs();
        if (chosenPic == null && image != null)
        {
            chosenPic = image.getDrawable();
        }
        if (validateDataMap()) {
            try
            {
                String venueName = location.getText().toString();
                List<Address> postVenueAddress = geocoder.getFromLocationName(venueName, 1);

                if(postVenueAddress.size() > 0)
                {
                    Address address = postVenueAddress.get(0);
                    venue.put("latitude", address.getLatitude());
                    venue.put("longitude", address.getLongitude());
                    venue.put("location", address.getSubAdminArea());
                }
                listingManager.postDataToDatabase((HashMap)venue, chosenPic, this);
            }
            catch(IOException io)
            {
                System.out.println(io.getMessage());
            }

        } else {
            Toast.makeText(VenueDetailsEditor.this,
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

                @Override
                public void run() {
                    Toast.makeText(VenueDetailsEditor.this,"Details updated successfully",
                            Toast.LENGTH_LONG).show();
                }
            });
            finish();
        } else if (creationResult == ListingManager.CreationResult.LISTING_FAILURE) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VenueDetailsEditor.this,
                            "Updating details failed.  Check your connection " +
                                    "and try again",
                            Toast.LENGTH_LONG).show();
                }
            });
        } else if (creationResult == ListingManager.CreationResult.IMAGE_FAILURE) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(VenueDetailsEditor.this,
                            "Updating details failed.  Check your connection " +
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
    public void cancelAdvertisement() {
        Intent backToMain = new Intent(VenueDetailsEditor.this, NavBarActivity.class);
        startActivity(backToMain);
        finish();
    }

    /**
     * Populate map with data from textviews
     */
    @Override
    public void listingDataMap() {
        if (description != null && description.getText() != null && !description.getText().equals("") && venue != null)
        {
            venue.put("description",description.getText().toString());
        }
        if(name != null && name.getText() != null && venue != null)
        {
            venue.put("name",name.getText().toString());
        }
        if(venueType != null && venueType.getText() != null && venue != null)
        {
            venue.put("venue-type",venueType.getText().toString());
        }
        if(email != null && email.getText() != null && venue != null)
        {
            venue.put("email-address",email.getText().toString());
        }
        if(phone != null && phone.getText() != null && venue != null)
        {
            venue.put("phone-number",phone.getText().toString());
        }
    }

    /**
     * validate data in listing map
     * @return true if valid
     */
    @Override
    public boolean validateDataMap() {
        for (Map.Entry element : venue.entrySet()) {
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

    public Map<String, Object> getVenue() {
        return venue;
    }

    public void setVenue(Map<String, Object> venue) {
        this.venue = venue;
    }

    public void setTabPreserver(TabStatePreserver tabPreserver) {
        this.tabPreserver = tabPreserver;
    }

    public void setListingManager(ListingManager listingManager) {
        this.listingManager = listingManager;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}