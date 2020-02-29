package com.gangoffive.rig2gig;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.gangoffive.rig2gig.ui.TabbedView.SectionsPagerAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateMusicianAdvertisement extends AppCompatActivity  implements CreateAdvertisement, TabbedViewReferenceInitialiser {


    private TextView name, position, description, currentPositions;
    private Button createListing, cancel, galleryImage, takePhoto;
    private ImageView image;
    private String musicianRef, type;
    private HashMap<String, Object> listing;
    private Map<String, Object> musician;
    private ListingManager listingManager;
    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_create_musician_advertisement_image,
            R.layout.fragment_create_advertisement_position,
            R.layout.fragment_create_musician_advertisement_details};
    private GridView gridView;
    static final String[] positions = new String[] {
            "Rhythm Guitar","Lead Guitar","Bass Guitar","Drums","Lead Vocals","Backing Vocals",
            "Keyboard","Trumpet","Saxophone","Oboe","Trombone","Cor","Clarinet","Gong","Triangle",
            "Harp","Piano","Accordian","Xylophone","Violin","Harmonica"};
    private List bandPositions;
    private ArrayAdapter<String> adapter;
    private Drawable chosenPic;
    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;
    private boolean editingText;
    private boolean savedOnFocus;
    private int saveLoopCount;
    private boolean breakout;
    private View.OnFocusChangeListener editTextFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus)
            {
                editingText = true;
                savedOnFocus = false;
            }
            else
            {
                editingText = false;
                savedOnFocus = false;
            }
        }
    };
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (createListing != null
                    &&  (bandPositions.size() == 0
                    || description.getText().toString().trim().length() == 0)) {
                createListing.setBackgroundColor(Color.parseColor("#B2BEB5"));
                createListing.setTextColor(Color.parseColor("#4D4D4E"));
            }
            else if (createListing != null
                    && description.getText().toString().trim().length() > 0
                    && bandPositions.size() > 0)
            {
                createListing.setBackgroundColor(Color.parseColor("#008577"));
                createListing.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_band_advertisement);
        tabTitles = new int[]{R.string.image, R.string.position, R.string.details};

        sectionsPagerAdapter = new SectionsPagerAdapter
                (this, getSupportFragmentManager(), tabTitles, fragments);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        bandPositions = new ArrayList();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, positions);
        editingText = false;
        savedOnFocus = false;
        saveLoopCount = 0;
        breakout = false;

        musicianRef = "eg2wI0UaYsnuSfIccKbR";
        type = "Musician";


        listingManager = new ListingManager(musicianRef, type, "");
        listingManager.getUserInfo(this);
    }


    /**
     * Populate view if database request was successful
     * @param data band data
     */
    @Override
    public void onSuccessFromDatabase(Map<String, Object> data) {
        setViewReferences();
        createListing.setBackgroundColor(Color.parseColor("#B2BEB5"));
        createListing.setTextColor(Color.parseColor("#4D4D4E"));
        musician = data;
        listingManager.getImage(this);
    }

    @Override
    public void onSuccessFromDatabase(Map<String, Object> data, Map<String, Object> listingData) {

    }

    /**
     * Populate view if database request was successful
     */
    @Override
    public void onSuccessfulImageDownload() {
        setupGridView();
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
        currentPositions = findViewById(R.id.currentPositions);
        if (currentPositions != null)
        {
            currentPositions.addTextChangedListener(textWatcher);
        }
        if (image != null)
        {
            image.setImageDrawable(null);
        }
        position = findViewById(R.id.position);
        description = findViewById(R.id.description);
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
        if (gridView == null)
        {
            gridView = findViewById(R.id.gridView);
        }
    }

    /**
     * Setup grid view for storing performer types
     */
    public void setupGridView()
    {
        if (gridView != null && gridView.getAdapter() == null)
        {
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    if(!bandPositions.contains(((TextView) v).getText()))
                    {
                        bandPositions.add(((TextView) v).getText());
                        String pos = bandPositions.toString();
                        currentPositions.setText("Current positions: " + pos.substring(1,pos.length()-1));
                    }
                    else if (bandPositions.contains(((TextView) v).getText()))
                    {
                        bandPositions.remove(((TextView) v).getText());
                        String pos = bandPositions.toString();
                        currentPositions.setText("Current positions: " + pos.substring(1,pos.length()-1));
                    }
                }
            });
        }
    }

    /**
     * populate text views
     */
    @Override
    public void populateInitialFields() {
        if(name != null && musician !=null && name.getText() != musician.get("name") && name.getText() == "")
        {
            name.setText(musician.get("name").toString());
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

    @Override
    public boolean editingText()
    {
        return editingText;
    }

    @Override
    public boolean savedOnFocus() {
        return savedOnFocus;
    }

    @Override
    public void setSavedOnFocus(boolean saved) {
        savedOnFocus = saved;
    }

    @Override
    public void breakOut() {
        saveLoopCount++;
        if (saveLoopCount > 20)
        {
            breakout = true;
            saveLoopCount = 0;
        }
    }

    @Override
    public boolean isBreakingOut() {
        return breakout;
    }

    @Override
    public void setBreakingOut(boolean isBreakingOut) {
        breakout = false;
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
            Toast.makeText(CreateMusicianAdvertisement.this,
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
            Intent intent = new Intent(CreateMusicianAdvertisement.this, MusicianListingDetailsActivity.class);
            intent.putExtra("EXTRA_MUSICIAN_LISTING_ID", listingManager.getListingRef());
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            sectionsPagerAdapter = null;
            viewPager = null;
            startActivity(intent);
            finish();
        } else if (creationResult == ListingManager.CreationResult.LISTING_FAILURE) {
            Toast.makeText(CreateMusicianAdvertisement.this,
                    "Listing creation failed.  Check your connection " +
                            "and try again",
                    Toast.LENGTH_LONG).show();
        } else if (creationResult == ListingManager.CreationResult.IMAGE_FAILURE) {
            Toast.makeText(CreateMusicianAdvertisement.this,
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
        Intent backToMain = new Intent(CreateMusicianAdvertisement.this,
                MainActivity.class);
        startActivity(backToMain);
    }

    /**
     * populate listing map with combination of values from text views and map generated from database
     */
    public void listingDataMap() {
        if (listing == null) {
            listing = new HashMap<>();
            listing.put("musician-ref", musicianRef);
        }

        listing.put("position", bandPositions);
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
        if (bandPositions == null || bandPositions.isEmpty())
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