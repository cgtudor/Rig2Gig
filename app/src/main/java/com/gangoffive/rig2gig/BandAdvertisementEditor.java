package com.gangoffive.rig2gig;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.gangoffive.rig2gig.ui.TabbedView.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BandAdvertisementEditor extends AppCompatActivity implements CreateAdvertisement, TabbedViewReferenceInitialiser, SearchView.OnQueryTextListener {

    private TextView name, position, description, searchHint;
    private Button createListing, cancel, galleryImage, takePhoto;
    private ImageView image;
    private String bandRef, type;
    private HashMap<String, Object> listing;
    private Map<String, Object> band, previousListing;
    private ListingManager listingManager;
    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_create_band_advertisement_image,
            R.layout.fragment_positions_search_bar,
            R.layout.fragment_create_band_advertisement_details};
    private GridView gridView;
    private int [] icons = {R.drawable.ic_close_red_24dp};
    private ArrayList<String> positions = new ArrayList<>(Arrays.asList(Positions.getPositions()));
    private List bandPositions;
    private ArrayAdapter<Button> chosenPositionsAdapter;
    private Drawable chosenPic;
    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;
    private SearchView searchBar;
    private ListView listResults;
    private ArrayAdapter<String> resultsAdapter;
    private CharSequence query = null;
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
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            validateButton();
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_band_advertisement);
        tabTitles = new int[]{R.string.image, R.string.position, R.string.details};
        Collections.sort(positions);
        sectionsPagerAdapter = new SectionsPagerAdapter
                (this, getSupportFragmentManager(), tabTitles, fragments);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        bandPositions = new ArrayList();
        bandRef = getIntent().getStringExtra("EXTRA_BAND_ID");
        String listingRef = getIntent().getStringExtra("EXTRA_LISTING_ID");
        type = "Band";
        listingManager = new ListingManager(bandRef, type, listingRef);
        listingManager.getUserInfo(this);
    }

    /**
     * validate current data and grey out create button if necessary
     */
    public void validateButton()
    {
        if (createListing != null && description!= null
            &&  (bandPositions.size() == 0
            || description.getText().toString().trim().length() == 0))
        {
            createListing.setBackgroundColor(Color.parseColor("#B2BEB5"));
            createListing.setTextColor(Color.parseColor("#4D4D4E"));
        }
        else if (createListing != null && description!= null
            && description.getText().toString().trim().length() > 0
            && bandPositions.size() > 0)
        {
            createListing.setBackgroundColor(Color.parseColor("#008577"));
            createListing.setTextColor(Color.parseColor("#FFFFFF"));
        }
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
        band = data;
        listingManager.getImage(this);
    }

    /**
     * Populate view if database request was successful
     * @param data band data
     * @param listingData existing listing data
     */
    @Override
    public void onSuccessFromDatabase(Map<String, Object> data, Map<String, Object> listingData) {
        setViewReferences();
        band = data;
        previousListing = listingData;
        bandPositions = (ArrayList)previousListing.get("position");
        for (Object pos : bandPositions)
        {
            positions.remove(pos.toString());
        }
        setupGridView();
        searchHint.setVisibility(View.INVISIBLE);
        listingManager.getImage(this);
    }

    /**
     * Populate view if database request was successful
     */
    @Override
    public void onSuccessfulImageDownload() {
        initialiseSearchBar();
        populateInitialFields();
        saveTabs();
    }

    /**
     * set references to text and image views and buttons
     */
    @Override
    public void setViewReferences() {
        searchHint = findViewById(R.id.searchHint);
        name = findViewById(R.id.name);
        image = findViewById(R.id.image);

        if (image != null)
        {
            image.setImageDrawable(null);
        }
        position = findViewById(R.id.position);
        description = findViewById(R.id.description);
        if (description != null)
        {
            description.addTextChangedListener(textWatcher);
            description.setOnFocusChangeListener(editTextFocusListener);
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
                    searchBar.clearFocus();
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
                    searchBar.clearFocus();
                }
            });
        }
        if (gridView == null)
        {
            gridView = findViewById(R.id.gridView);
        }
        if (listResults == null)
        {
            listResults = (ListView) findViewById(R.id.list_results);
        }
    }

    /**
     * Initialises the search bar for position tab
     */
    public void initialiseSearchBar()
    {
        if(listResults != null)
        {
            listResults.setTextFilterEnabled(true);
            listResults.setAdapter(resultsAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,
                    positions));
            listResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    bandPositions.add(((TextView) v).getText().toString());
                    Collections.sort(bandPositions);
                    positions.remove(((TextView) v).getText().toString());
                    Collections.sort(positions);
                    searchHint.setVisibility(View.INVISIBLE);
                    initialiseSearchBar();
                    setupGridView();
                }
            });
        }
        if (searchBar == null)
        {
            searchBar = findViewById(R.id.search_bar);
            searchBar.setIconifiedByDefault(false);
            searchBar.setOnQueryTextListener(this);
            searchBar.setSubmitButtonEnabled(false);
            searchBar.setQueryHint("Enter band position");
        }
        if (searchBar != null)
        {
            query = searchBar.getQuery();
        }
        if (query != null)
        {
            listResults.setFilterText(query.toString());
            listResults.dispatchDisplayHint(View.INVISIBLE);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String typedText)
    {
        return false;
    }

    /**
     * filter search bar list based on typed text
     * @param typedText text enterd in search bar
     * @return false
     */
    @Override
    public boolean onQueryTextChange(String typedText) {
        Filter filter = resultsAdapter.getFilter();
        filter.filter(typedText);
        return true;
    }

    /**
     * set up grid view containing all positions selected by user
     */
    public void setupGridView()
    {
        DeleteInstrumentAdapter customAdapter = new DeleteInstrumentAdapter(bandPositions, this);
        gridView.setAdapter(customAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                positions.add(bandPositions.get(position).toString());
                Collections.sort(positions);
                bandPositions.remove(position);
                Collections.sort(bandPositions);
                if (bandPositions.size() == 0)
                {
                    searchHint.setVisibility(View.VISIBLE);
                }
                initialiseSearchBar();
                setupGridView();
            }
        });
        validateButton();
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
        if (description != null && previousListing != null
                && (listing != null && listing.get("description").toString() == null))
        {
            description.setText(previousListing.get("description").toString());
        }
        else if (description != null && previousListing != null)
        {
            if (listing == null)
            {
                listing = new HashMap<>();
                listing.put("description",previousListing.get("description"));
            }
            description.setText(listing.get("description").toString());
        }
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
            Toast.makeText(BandAdvertisementEditor.this,
                    "Advertisement not created.  Ensure all fields are complete " +
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
            Toast.makeText(this,"Advertisement created successfully",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(BandAdvertisementEditor.this, BandListingDetailsActivity.class);
            intent.putExtra("EXTRA_BAND_LISTING_ID", listingManager.getListingRef());
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            sectionsPagerAdapter = null;
            viewPager = null;
            startActivity(intent);
            finish();
        } else if (creationResult == ListingManager.CreationResult.LISTING_FAILURE) {
            Toast.makeText(BandAdvertisementEditor.this,
                    "Listing creation failed.  Check your connection " +
                            "and try again",
                    Toast.LENGTH_LONG).show();
        } else if (creationResult == ListingManager.CreationResult.IMAGE_FAILURE) {
            Toast.makeText(BandAdvertisementEditor.this,
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
        Intent backToMain = new Intent(BandAdvertisementEditor.this,
                MainActivity.class);
        startActivity(backToMain);
    }

    /**
     * populate listing map with combination of values from text views and map generated from database
     */
    public void listingDataMap() {
        if (listing == null) {
            listing = new HashMap<>();
            listing.put("band-ref", bandRef);
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

    @Override
    public void beginTabPreservation() {
        tabPreserver.preserveTabState();
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
        if (description != null && description.getText() != null && listing != null)
        {
            listing.put("description",description.getText().toString());
        }
        reinitialiseTabs();
    }
}