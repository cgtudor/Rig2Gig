package com.gangoffive.rig2gig.musician.management;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.gangoffive.rig2gig.band.management.DeleteMemberConfirmation;
import com.gangoffive.rig2gig.navbar.NavBarActivity;
import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.utils.GenreSelectorActivity;
import com.gangoffive.rig2gig.utils.PositionSelectorActivity;
import com.gangoffive.rig2gig.utils.TabStatePreserver;
import com.gangoffive.rig2gig.utils.TabbedViewReferenceInitialiser;
import com.gangoffive.rig2gig.advert.management.CreateAdvertisement;
import com.gangoffive.rig2gig.advert.management.GooglePlacesAutoSuggestAdapter;
import com.gangoffive.rig2gig.firebase.ListingManager;
import com.gangoffive.rig2gig.ui.TabbedView.SectionsPagerAdapter;
import com.gangoffive.rig2gig.utils.ImageRequestHandler;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MusicianDetailsEditor extends AppCompatActivity implements CreateAdvertisement, TabbedViewReferenceInitialiser {

    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.SYSTEM_ALERT_WINDOW","android.permission.CAMERA"};
    private Geocoder geocoder;
    private LinearLayout linearLayout;
    private TextView name, distance, genres, fader;
    private AutoCompleteTextView location;
    private Button createListing, cancel, galleryImage, takePhoto, selectGenre;
    private ImageView image;
    private String musicianRef, type, distanceText;
    private Map<String, Object> musician;
    private ListingManager listingManager;
    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_image_changer,
            R.layout.fragment_musician_details_changer};
    private Drawable chosenPic;
    private ConstraintLayout constraintLayout;
    private TabStatePreserver tabPreserver = new TabStatePreserver(this);
    private boolean mapping;
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
                createListing.setTextColor(Color.parseColor("#ffffff"));
            }
            else if (before == 0 && count >= 1 && createListing != null
                    && name.getText().toString().trim().length() > 0
                    && location.getText().toString().trim().length() > 0
                    && genres.getText().toString().trim().length() > 0
            )
            {
                createListing.setBackgroundColor(Color.parseColor("#12c2e9"));
                createListing.setTextColor(Color.parseColor("#ffffff"));
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 2);
        }
        mapping = false;
        geocoder = new Geocoder(this, Locale.getDefault());
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Edit Details");
        /*Setting the support action bar to the newly created toolbar*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listingManager = new ListingManager(musicianRef, type, listingRef);
        listingManager.getUserInfo(this);
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
        if (musician != null)
        {
            String currentGenres = musician.get("genres").toString();
            currentGenres = currentGenres.substring(1, currentGenres.length() - 1);
            musician.put("genres",currentGenres);
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
        name = findViewById(R.id.venue_name_final);
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
     * Start popup activty to select/edit genres
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
     * Change genre button text to be relevant to situation
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
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

                                    location.setText(adr.getLocality() + ", " + adr.getCountryCode());


                                }
                            });
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    distance.setText(musician.get("distance").toString());
                }
            });
        }
        if(genres != null && musician !=null)
        {
            String currentGenres = musician.get("genres").toString();
            if (currentGenres.charAt(0) == '[')
            {
                currentGenres = currentGenres.substring(1, currentGenres.length() - 1);
            }
            genres.setText(currentGenres);
            setGenreButton();
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
     * Begin tab preservation process
     */
    @Override
    public void beginTabPreservation() {
        tabPreserver.preserveTabState();
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
            listingManager.postDataToDatabase((HashMap)musician, chosenPic, this);
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
                @Override
                public void run() {

                    Toast.makeText(MusicianDetailsEditor.this,"Details updated successfully",
                            Toast.LENGTH_SHORT).show();
                }
            });
            Intent intent = new Intent(MusicianDetailsEditor.this, NavBarActivity.class);
            intent.putExtra("EXTRA_VENUE_LISTING_ID", listingManager.getListingRef());
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            finish();
        } else if (creationResult == ListingManager.CreationResult.LISTING_FAILURE) {
            createListing.setOnClickListener(confirm);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MusicianDetailsEditor.this,
                            "Updating details failed.  Check your connection " +
                                    "and try again",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else if (creationResult == ListingManager.CreationResult.IMAGE_FAILURE) {
            createListing.setOnClickListener(confirm);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MusicianDetailsEditor.this,
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
     * populate map with data from textviews
     */
    @Override
    public void listingDataMap() {
        if(name != null && name.getText() != null && musician != null)
        {
            musician.put("name",name.getText().toString());
            musician.put("index-name",name.getText().toString().toLowerCase());
        }
        if(location != null && location.getText() != null && musician != null && mapping == true)
        {
            mapping = false;
            try
            {
                String musicianName = location.getText().toString();
                List<Address> postMusicianAddress = geocoder.getFromLocationName(musicianName, 20);

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
            musician.put("distance", distanceText);
        }
        if(genres != null && genres.getText() != null && musician != null)
        {
            String genresText = genres.getText().toString();
            ArrayList<String> selectedGenres = new ArrayList<String>(Arrays.asList(genresText.split(",")));
            for (int i = 0; i < selectedGenres.size(); i++)
            {
                selectedGenres.set(i,selectedGenres.get(i).trim());
            }
            musician.put("genres",selectedGenres);
        }
    }

    /**
     * validate data in listing map
     * @return true if valid
     */
    @Override
    public boolean validateDataMap() {
        for (Map.Entry element : musician.entrySet()) {
            if (!(element.getKey().equals("bands")))
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
                            @Override
                            public void run() {
                                Toast.makeText(MusicianDetailsEditor.this,
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
                    if (val == null || val.trim().isEmpty()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MusicianDetailsEditor.this,
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
                    @Override
                    public void run() {
                        Toast.makeText(MusicianDetailsEditor.this,
                                "Details not updated.  Distance cannot be '0'.",
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
     * @param listingManager listingMnager to set
     */
    public void setListingManager(ListingManager listingManager) {
        this.listingManager = listingManager;
    }

    /**
     * @return musician
     */
    public Map<String, Object> getMusician() {
        return musician;
    }

    /**
     * @param musician musician to set
     */
    public void setMusician(Map<String, Object> musician) {
        this.musician = musician;
    }

    /**
     * @param tabPreserver tabPreserver to set
     */
    public void setTabPreserver(TabStatePreserver tabPreserver) {
        this.tabPreserver = tabPreserver;
    }

    /**
     * @param isMapping isMapping to set
     */
    public void setMapping(boolean isMapping) {
        mapping = isMapping;
    }

    /**
     * @param image image to set
     */
    public void setImage(ImageView image) {
        this.image = image;
    }

    /**
     * Handle menu item selection
     * @param item item selected
     * @return if item was seleceted
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