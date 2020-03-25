package com.gangoffive.rig2gig;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class MusicianDetailsEditor extends AppCompatActivity implements CreateAdvertisement, TabbedViewReferenceInitialiser {

    private Geocoder geocoder;
    private TextView name, distance, genres;
    private AutoCompleteTextView location;
    private Button createListing, cancel, galleryImage, takePhoto;
    private ImageView image;
    private String musicianRef, type;
    private Map<String, Object> musician;
    private ListingManager listingManager;
    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_image_changer,
            R.layout.fragment_musician_details_changer};
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
                    && location.getText().toString().trim().length() > 0
                    && genres.getText().toString().trim().length() > 0
            )
            {
                createListing.setBackgroundColor(Color.parseColor("#008577"));
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
        tabTitles = new int[]{R.string.image, R.string.details};
                //, R.string.description};
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter
                (this, getSupportFragmentManager(), tabTitles, fragments);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        musicianRef = getIntent().getStringExtra("EXTRA_MUSICIAN_ID");
        String listingRef = "profileEdit";
        type = "Musician";
        listingManager = new ListingManager(musicianRef, type, listingRef);
        listingManager.getUserInfo(this);
        geocoder = new Geocoder(this, Locale.getDefault());
    }

    /**
     * Populate view if database request was successful
     * @param data band data
     */
    @Override
    public void onSuccessFromDatabase(Map<String, Object> data)
    {
        setViewReferences();
        musician = data;
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
        image = findViewById(R.id.image);
        if (image != null)
        {
            image.setImageDrawable(null);
        }
        name = findViewById(R.id.name);
        if (name != null)
        {
            name.setOnFocusChangeListener(editTextFocusListener);
            name.addTextChangedListener(textWatcher);

        }
        location = findViewById(R.id.musician_location);
        if (location != null)
        {
            if(location.getAdapter() == null)
            {
                location.setAdapter(new GooglePlacesAutoSuggestAdapter(MusicianDetailsEditor.this, android.R.layout.simple_list_item_1));
            }

            location.setOnFocusChangeListener(editTextFocusListener);
            location.addTextChangedListener(textWatcher);
        }
        distance = findViewById(R.id.distance);
        if (distance != null)
        {
            distance.setOnFocusChangeListener(editTextFocusListener);
            distance.addTextChangedListener(textWatcher);
        }
        genres = findViewById(R.id.genres);
        if (genres != null)
        {
            genres.setOnFocusChangeListener(editTextFocusListener);
            genres.addTextChangedListener(textWatcher);
        }
        createListing = findViewById(R.id.createListing);
        createListing.setBackgroundColor(Color.parseColor("#008577"));
        createListing.setTextColor(Color.parseColor("#FFFFFF"));
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
        if (chosenPic != null && image != null)
        {
            image.setImageDrawable(chosenPic);
        }
        if(name != null && musician !=null)
        {
            name.setText(musician.get("name").toString());
        }
        if(location != null && musician !=null)
        {
            try
            {
                List<Address> getMusicianCity = geocoder.getFromLocation(Double.parseDouble(musician.get("latitude").toString()), Double.parseDouble(musician.get("longitude").toString()), 20);

                if(getMusicianCity != null && getMusicianCity.size() > 0)
                {
                    for(Address adr : getMusicianCity)
                    {
                        if(adr.getSubLocality() != null)
                        {
                            location.setText(adr.getSubLocality() + ", " + adr.getCountryCode());
                            break;
                        }
                        else if(adr.getLocality() != null)
                        {
                            location.setText(adr.getLocality() + ", " + adr.getCountryCode());
                            break;
                        }
                    }
                }
            }
            catch(IOException io)
            {
                System.out.println(io.getMessage());
            }
        }
        if(distance != null && musician !=null)
        {
            distance.setText(musician.get("distance").toString());
        }
        if(genres != null && musician !=null)
        {
            genres.setText(musician.get("genres").toString());
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
    }

    @Override
    public void beginTabPreservation() {
        tabPreserver.preserveTabState();
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
        if (chosenPic != null)
        {
            chosenPic = image.getDrawable();
        }
        if (validateDataMap()) {
            listingManager.postDataToDatabase((HashMap)musician, chosenPic, this);
        } else {
            Toast.makeText(MusicianDetailsEditor.this,
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
            Toast.makeText(this,"Details updated successfully",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MusicianDetailsEditor.this, NavBarActivity.class);
            Toast.makeText(MusicianDetailsEditor.this,
                    "Details successfully updated",
                    Toast.LENGTH_LONG).show();
            intent.putExtra("EXTRA_VENUE_LISTING_ID", listingManager.getListingRef());
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
        } else if (creationResult == ListingManager.CreationResult.LISTING_FAILURE) {
            Toast.makeText(MusicianDetailsEditor.this,
                    "Listing creation failed.  Check your connection " +
                            "and try again",
                    Toast.LENGTH_LONG).show();
        } else if (creationResult == ListingManager.CreationResult.IMAGE_FAILURE) {
            Toast.makeText(MusicianDetailsEditor.this,
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
        finish();
    }

    /**
     * populate map with data from textviews
     */
    @Override
    public void listingDataMap() {
        if(name != null && name.getText() != null && musician != null)
        {
            musician.put("name",name.getText().toString());
        }
        if(location != null && location.getText() != null && musician != null)
        {
            try
            {
                String musicianName = location.getText().toString();
                List<Address> postMusicianAddress = geocoder.getFromLocationName(musicianName, 1);

                if(postMusicianAddress.size() > 0)
                {
                    Address address = postMusicianAddress.get(0);
                    musician.put("latitude", address.getLatitude());
                    musician.put("longitude", address.getLongitude());

                    if(postMusicianAddress.get(0).getLocality() != null)
                    {
                        musician.put("location", address.getLocality());
                    }
                    else if(postMusicianAddress.get(0).getSubLocality() != null)
                    {
                        musician.put("location", address.getSubLocality());
                    }
                }
            }
            catch(IOException io)
            {
                System.out.println(io.getMessage());
            }
        }
        if(distance != null && distance.getText() != null && musician != null)
        {
            musician.put("distance",distance.getText().toString());
        }
        if(genres != null && genres.getText() != null && musician != null)
        {
            musician.put("genres",genres.getText().toString());
        }
    }

    /**
     * validate data in listing map
     * @return true if valid
     */
    @Override
    public boolean validateDataMap() {
        for (Map.Entry element : musician.entrySet()) {
            if (!(element.getValue().toString().equals("bands")))
            {
                String val = element.getValue().toString();
                if (val == null || val.trim().isEmpty()) {
                    return false;
                }
            }
        }
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
        if (actualNumber.length() == 0)
        {
            return false;
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