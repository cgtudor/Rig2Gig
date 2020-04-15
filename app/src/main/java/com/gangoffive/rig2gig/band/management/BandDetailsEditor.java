package com.gangoffive.rig2gig.band.management;

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
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.util.PatternsCompat;
import androidx.viewpager.widget.ViewPager;

import com.gangoffive.rig2gig.advert.management.CreateAdvertisement;
import com.gangoffive.rig2gig.advert.management.GooglePlacesAutoSuggestAdapter;
import com.gangoffive.rig2gig.musician.management.MusicianDetailsEditor;
import com.gangoffive.rig2gig.utils.GenreSelectorActivity;
import com.gangoffive.rig2gig.utils.ImageRequestHandler;
import com.gangoffive.rig2gig.firebase.ListingManager;
import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.utils.TabStatePreserver;
import com.gangoffive.rig2gig.utils.TabbedViewReferenceInitialiser;
import com.gangoffive.rig2gig.ui.TabbedView.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BandDetailsEditor extends AppCompatActivity implements CreateAdvertisement, TabbedViewReferenceInitialiser {

    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.SYSTEM_ALERT_WINDOW","android.permission.CAMERA"};
    private boolean mapping;
    private Geocoder geocoder;
    private TextView name, distance, genres, email, phone, fader;
    private AutoCompleteTextView location;
    private Button createListing, cancel, galleryImage, takePhoto, selectGenre;
    private ImageView image;
    private String bandRef, type, distanceText;
    private Map<String, Object> band;
    private ListingManager listingManager;
    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_image_changer,
            R.layout.fragment_band_details_changer};
    private Drawable chosenPic;
    private TabStatePreserver tabPreserver = new TabStatePreserver(this);
    private View.OnClickListener genreSelect = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selectGenre.setOnClickListener(null);
            selectGenres();
        }
    };
    private View.OnClickListener confirm = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createListing.setOnClickListener(null);
            createAdvertisement();
        }
    };
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
                createListing.setBackgroundColor(Color.parseColor("#a6a6a6"));
                createListing.setTextColor(Color.parseColor("#FFFFFF"));
            }
            else if (before == 0 && count >= 1 && createListing != null
                    && name.getText().toString().trim().length() > 0
                    && location.getText().toString().trim().length() > 0
                    && genres.getText().toString().trim().length() > 0
                    && email.getText().toString().trim().length() > 0
                    && phone.getText().toString().trim().length() > 0
            )
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
        tabTitles = new int[]{R.string.image, R.string.details};
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter
                (this, getSupportFragmentManager(), tabTitles, fragments);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        bandRef = getIntent().getStringExtra("EXTRA_BAND_ID");
        String listingRef = "profileEdit";
        type = "Band";
        mapping = false;
        listingManager = new ListingManager(bandRef, type, listingRef);
        listingManager.getUserInfo(this);
        geocoder = new Geocoder(this, Locale.getDefault());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 2);
        }
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Edit Band Details");
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
        band = data;
        if (band != null)
        {
            String currentGenres = band.get("genres").toString();
            currentGenres = currentGenres.substring(1, currentGenres.length() - 1);
            band.put("genres",currentGenres);
        }
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
        name = findViewById(R.id.name);
        if (name != null)
        {
            name.setOnFocusChangeListener(editTextFocusListener);
            name.addTextChangedListener(textWatcher);
        }
        location = findViewById(R.id.band_location);
        if (location != null)
        {
            if(location.getAdapter() == null)
            {
                location.setAdapter(new GooglePlacesAutoSuggestAdapter(BandDetailsEditor.this, android.R.layout.simple_list_item_1));
            }

            location.setOnFocusChangeListener(editTextFocusListener);
            location.addTextChangedListener(textWatcher);
        }
        distance = findViewById(R.id.venue_description_final);
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
        createListing.setBackgroundColor(Color.parseColor("#12c2e9"));
        createListing.setTextColor(Color.parseColor("#FFFFFF"));
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
        selectGenre = findViewById(R.id.selectGenres);
        if (selectGenre != null)
        {
            selectGenre.setOnClickListener(genreSelect);
        }
        fader = findViewById(R.id.fader);
    }

    /**
     * Start genre selector popup activity
     */
    public void selectGenres()
    {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.darkerMain));
        fader.setVisibility(View.VISIBLE);
        Intent intent =  new Intent(this, GenreSelectorActivity.class);
        intent.putExtra("EXTRA_LAYOUT_TYPE", "Not Login");
        intent.putExtra("EXTRA_GENRES", genres.getText().toString());
        startActivityForResult(intent, 99);
    }

    /**
     * Amend text of genre button accordingly
     */
    public void setGenreButton()
    {
        if (genres.getText().toString().equals(""))
        {
            selectGenre.setText("Select Genres");
        }
        else
        {
            selectGenre.setText("Edit Genres");
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
        if(name != null && band !=null)
        {
            name.setText(band.get("name").toString());
        }
        if(location != null && band !=null)
        {
            try
            {
                List<Address> getBandCity = geocoder.getFromLocation(Double.parseDouble(band.get("latitude").toString()), Double.parseDouble(band.get("longitude").toString()), 20);
                if (getBandCity != null && getBandCity.size() > 0)
                {
                    for (Address adr : getBandCity)
                    {
                        if (adr.getLocality() != null)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    location.setText(adr.getLocality() + ", " + adr.getCountryCode());
                                }
                            });
                            break;
                        }
                        else if (adr.getSubLocality() != null)
                        {
                            location.setText(adr.getSubLocality() + ", " + adr.getCountryCode());
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
        if(distance != null && band !=null)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    distance.setText(band.get("distance").toString());
                }
            });
        }
        if(genres != null && band !=null)
        {
            String currentGenres = band.get("genres").toString();
            if (currentGenres.charAt(0) == '[')
            {
                currentGenres = currentGenres.substring(1, currentGenres.length() - 1);
            }
            genres.setText(currentGenres);
            setGenreButton();
        }
        if(email != null && band !=null)
        {
            email.setText(band.get("email").toString());
        }
        if(phone != null && band !=null)
        {
            phone.setText(band.get("phone-number").toString());
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
    }

    /**
     * Begin process of preserving tab states
     */
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
        if (requestCode == 99)
        {
            selectGenre.setOnClickListener(genreSelect);
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
            fader.setVisibility(View.GONE);
            if (resultCode == RESULT_OK)
            {
                String genresExtra = data.getStringExtra("EXTRA_SELECTED_GENRES");
                genres.setText(genresExtra);
                setGenreButton();
            }
        }
        else
        {
            image = ImageRequestHandler.handleResponse(requestCode, resultCode, data, image);
            chosenPic = image.getDrawable();
        }
    }

    /**
     * create advertisement, posting to database
     */
    @Override
    public void createAdvertisement() {
        saveTabs();
        createListing.setOnClickListener(null);
        if (chosenPic != null)
        {
            chosenPic = image.getDrawable();
        }
        if (validateDataMap()) {
            listingManager.postDataToDatabase((HashMap)band, chosenPic, this);
        }
        else
        {
            createListing.setOnClickListener(confirm);
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
                    Toast.makeText(BandDetailsEditor.this,"Details updated successfully",
                            Toast.LENGTH_SHORT).show();
                }
            });
            finish();
        } else if (creationResult == ListingManager.CreationResult.LISTING_FAILURE) {
            createListing.setOnClickListener(confirm);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(BandDetailsEditor.this,
                            "Updating details failed.  Check your connection " +
                                    "and try again",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else if (creationResult == ListingManager.CreationResult.IMAGE_FAILURE) {
            createListing.setOnClickListener(confirm);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(BandDetailsEditor.this,
                            "Updating details failed.  Check your connection " +
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
        finish();
    }

    /**
     * Populate map with data in text views
     */
    @Override
    public void listingDataMap()
    {
        if(name != null && name.getText() != null && band != null)
        {
            band.put("name",name.getText().toString());
        }
        if(location != null && location.getText() != null && band != null && mapping == true)
        {
            mapping = false;
            try
            {
                String bandName = location.getText().toString();
                List<Address> postBandAddress = geocoder.getFromLocationName(bandName, 1);

                if(postBandAddress.size() > 0)
                {
                    Address address = postBandAddress.get(0);
                    band.put("latitude", address.getLatitude());
                    band.put("longitude", address.getLongitude());

                    if(postBandAddress.get(0).getLocality() != null)
                    {
                        band.put("location", address.getLocality());
                    }
                    else if(postBandAddress.get(0).getSubLocality() != null)
                    {
                        band.put("location", address.getSubLocality());
                    }
                }
            }
            catch(IOException io)
            {
                System.out.println(io.getMessage());
            }
        }
        if(distance != null && distance.getText() != null && band != null)
        {
            distanceText = distance.getText().toString();
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    distance.setText(distanceText);
                }
            });
            band.put("distance", distanceText);
        }
        if(genres != null && band !=null)
        {
            String genresText = genres.getText().toString();
            ArrayList<String> selectedGenres = new ArrayList<String>(Arrays.asList(genresText.split(",")));
            for (int i = 0; i < selectedGenres.size(); i++)
            {
                selectedGenres.set(i,selectedGenres.get(i).trim());
            }
            band.put("genres",selectedGenres);
        }
        if(email != null && genres.getText() != null && band != null)
        {
            band.put("email",email.getText().toString());
        }
        if(phone != null && genres.getText() != null && band != null)
        {
            band.put("phone-number",phone.getText().toString());
        }
    }

    /**
     * validate data in listing map
     * @return true if valid
     */
    @Override
    public boolean validateDataMap() {
        for (Map.Entry element : band.entrySet()) {
            if (!(element.getKey().toString().equals("members")))
            {
                String val = element.getValue().toString();
                if (element.getKey().equals("genres"))
                {
                    if (val != null && !val.equals(""))
                    {
                        val = val.substring(1, val.length() - 1);
                    }
                    if (val.trim().isEmpty())
                    {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(BandDetailsEditor.this,
                                        "Details not updated.  Ensure all fields are complete " +
                                                "and try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        return false;
                    }
                }
                else
                {
                    if (val == null || val.trim().isEmpty())
                    {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(BandDetailsEditor.this,
                                        "Details not updated.  Ensure all fields are complete " +
                                                "and try again",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        return false;
                    }
                }
            }
        }
        if (distance != null)
        {
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
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(BandDetailsEditor.this,
                                "Details not updated.  Distance cannot be '0'.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return false;
            }
        }
        if (!PatternsCompat.EMAIL_ADDRESS.matcher(band.get("email").toString()).matches())
        {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(BandDetailsEditor.this,
                            "Details not updated.  Email address is invalid.",
                            Toast.LENGTH_SHORT).show();
                }
            });
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

    /**
     * @param listingManager listingManager to set
     */
    public void setListingManager(ListingManager listingManager) {
        this.listingManager = listingManager;
    }

    /**
     * @return band
     */
    public Map<String, Object> getBand() {
        return band;
    }

    /**
     * @param band band to set
     */
    public void setBand(Map<String, Object> band) {
        this.band = band;
    }

    /**
     * @param tabPreserver tabPreserver to set
     */
    public void setTabPreserver(TabStatePreserver tabPreserver) {
        this.tabPreserver = tabPreserver;
    }

    /**
     * Handle menu item selection
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