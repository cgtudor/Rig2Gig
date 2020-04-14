package com.gangoffive.rig2gig.advert.management;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import com.gangoffive.rig2gig.utils.Positions;
import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.utils.TabStatePreserver;
import com.gangoffive.rig2gig.utils.TabbedViewReferenceInitialiser;
import com.gangoffive.rig2gig.advert.details.MusicianListingDetailsActivity;
import com.gangoffive.rig2gig.firebase.ListingManager;
import com.gangoffive.rig2gig.utils.ImageRequestHandler;
import com.gangoffive.rig2gig.utils.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
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
import com.gangoffive.rig2gig.ui.TabbedView.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicianAdvertisementEditor extends AppCompatActivity  implements CreateAdvertisement, TabbedViewReferenceInitialiser, SearchView.OnQueryTextListener {

    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.SYSTEM_ALERT_WINDOW","android.permission.CAMERA"};
    private TextView name, position, description, searchHint;
    private Button createListing, cancel, galleryImage, takePhoto;
    private ImageView image;
    private String musicianRef, type, listingRef, editType;
    private boolean finalCheck;
    private HashMap<String, Object> listing;
    private Map<String, Object> musician, previousListing;
    private ListingManager listingManager;
    private int[] tabTitles = {R.string.image, R.string.position, R.string.details};
    private int[] fragments = {R.layout.fragment_create_musician_advertisement_image,
            R.layout.fragment_positions_search_bar,
            R.layout.fragment_create_musician_advertisement_details};
    private GridView gridView;
    private ArrayList<String> positions = new ArrayList<>(Arrays.asList(Positions.getPositions()));
    private List bandPositions = new ArrayList();
    private Drawable chosenPic;
    private SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter
            (this, getSupportFragmentManager(), tabTitles, fragments);
    private ViewPager viewPager;
    private View.OnClickListener confirm = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createListing.setOnClickListener(null);
            createAdvertisement();
        }
    };
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

    private double musicianLatitude;
    private double musicianLongitude;
    private FirebaseAuth fAuth;
    private FirebaseFirestore FSTORE;
    private CollectionReference musicianReference;
    private Query getMusicianLocation;
    private final String TAG = "@@@@@@@@@@@@@@@@@@@@@@@";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fAuth = FirebaseAuth.getInstance();
        FSTORE = FirebaseFirestore.getInstance();
        musicianReference = FSTORE.collection("musicians");
        getMusicianLocation = musicianReference;
        setContentView(R.layout.activity_create_musician_advertisement);
        Collections.sort(positions);
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        finalCheck = false;
        musicianRef = getIntent().getStringExtra("EXTRA_MUSICIAN_ID");
        listingRef = getIntent().getStringExtra("EXTRA_LISTING_ID");
        type = "Musician";
        if (listingRef != null && listingRef.equals(""))
        {
            editType = "creation";
        }
        else
        {
            editType = "edit";
        }
        listingManager = new ListingManager(musicianRef, type, listingRef);
        listingManager.getUserInfo(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, 2);
        }
        getMusicianLocation();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Advertise yourself to bands");
        /*Setting the support action bar to the newly created toolbar*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Advertise yourself to bands
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
            if (createListing != null)
            {
                setInitialColours();
            }
            musician = data;
            listingManager.getImage(this);
        }

    }

    /**
     * Set initial colours for confirm button
     */
    public void setInitialColours()
    {
        createListing.setBackgroundColor(Color.parseColor("#a6a6a6"));
        createListing.setTextColor(Color.parseColor("#FFFFFF"));
    }

    /**
     * Populate view if database request was successful
     * @param data band data
     * @param listingData existing listing data
     */
    @Override
    public void onSuccessFromDatabase(Map<String, Object> data, Map<String, Object> listingData) {
        if (finalCheck)
        {
            postToDatabase(data);
        }
        else
        {
            setViewReferences();
            musician = data;
            previousListing = listingData;
            bandPositions = (ArrayList)previousListing.get("position");
            for (Object pos : bandPositions)
            {
                positions.remove(pos.toString());
            }
            setupGridView();
            if (searchHint != null)
            {
                searchHint.setVisibility(View.INVISIBLE);
            }
            listingManager.getImage(this);
        }
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
        name = findViewById(R.id.firstName);
        image = findViewById(R.id.image);
        if (image != null)
        {
            image.setImageDrawable(null);
        }
        position = findViewById(R.id.position);
        description = findViewById(R.id.venue_description_final);
        if (description != null)
        {
            description.addTextChangedListener(textWatcher);
            description.setOnFocusChangeListener(editTextFocusListener);
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

    /**
     * Not used
     * @param typedText
     * @return false
     */
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
        });


    }

    /**
     * validate current data and grey out create button if necessary
     */
    public void validateButton()
    {
        if (createListing != null && description!= null
                &&  (bandPositions.size() == 0
                || description.getText().toString().trim().length() == 0)) {
            createListing.setBackgroundColor(Color.parseColor("#a6a6a6"));
            createListing.setTextColor(Color.parseColor("#FFFFFF"));
        }
        else if (createListing != null && description!= null
                && description.getText().toString().trim().length() > 0
                && bandPositions.size() > 0)
        {
            createListing.setBackgroundColor(Color.parseColor("#12c2e9"));
            createListing.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    /**
     * populate text views
     */
    @Override
    public void populateInitialFields() {
        if(name != null && musician !=null && name.getText() != musician.get("name") && name.getText() == "")
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    name.setText(musician.get("name").toString());
                }
            });
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
     * Begin process of preserving tab states
     */
    @Override
    public void beginTabPreservation() {
        tabPreserver.preserveTabState();
    }

    @Override
    public void setMapping(boolean isMapping) {

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
     * create advertisement, start final checks before posting to database
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
            Toast.makeText(MusicianAdvertisementEditor.this,
                    "Advertisement " + editType + " unsuccessful.  Ensure all fields are complete " +
                            "and try again",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Post advert to database
     * @param data advert data
     */
    public void postToDatabase(Map<String, Object> data)
    {
        if (data != null)
        {
            listing.put("genres",data.get("genres"));
            listing.put("rating",data.get("rating"));
            listingManager.postDataToDatabase(listing, chosenPic, this);
        }
        else
        {
            createListing.setOnClickListener(confirm);
            Toast.makeText(MusicianAdvertisementEditor.this,
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
                    Toast.makeText(MusicianAdvertisementEditor.this,"Advertisement " + editType + " successful",
                            Toast.LENGTH_SHORT).show();
                }
            });
            Intent intent = new Intent(MusicianAdvertisementEditor.this,
                    MusicianListingDetailsActivity.class);
            intent.putExtra("EXTRA_MUSICIAN_LISTING_ID", listingManager.getListingRef());
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            sectionsPagerAdapter = null;
            viewPager = null;
            startActivity(intent);
            finish();
        } else if (creationResult == ListingManager.CreationResult.LISTING_FAILURE) {
            createListing.setOnClickListener(confirm);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MusicianAdvertisementEditor.this,
                            "Advertisement " + editType + " failed.  Check your connection " +
                                    "and try again",
                            Toast.LENGTH_SHORT).show();
                }
            });

        } else if (creationResult == ListingManager.CreationResult.IMAGE_FAILURE) {
            createListing.setOnClickListener(confirm);
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MusicianAdvertisementEditor.this,
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
        Intent backToMain = new Intent(MusicianAdvertisementEditor.this,
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
            listing.put("musician-ref", musicianRef);
        }

        listing.put("position", bandPositions);
        if(description != null)
        {
            listing.put("description", description.getText().toString());
        }

        listing.put("latitude", musicianLatitude);
        listing.put("longitude", musicianLongitude);
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

    /**
     * @return musician
     */
    public Map<String, Object> getMusician() {
        return musician;
    }

    /**
     * @return previousListing
     */
    public Map<String, Object> getPreviousListing() {
        return previousListing;
    }

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
     * @param bandPositions bandPositions to set
     */
    public void setBandPositions(List bandPositions) {
        this.bandPositions = bandPositions;
    }

    /**
     * @param tabPreserver tabPreserver to set
     */
    public void setTabPreserver(TabStatePreserver tabPreserver) {
        this.tabPreserver = tabPreserver;
    }

    /**
     * @param musician musician to set
     */
    public void setMusician(Map<String, Object> musician) {
        this.musician = musician;
    }

    /**
     * @param previousListing previousListing to set
     */
    public void setPreviousListing(Map<String, Object> previousListing) {
        this.previousListing = previousListing;
    }

    /**
     * @param fAuth fAuth to set
     */
    public void setfAuth(FirebaseAuth fAuth) {
        this.fAuth = fAuth;
    }

    /**
     * @param FSTORE FSTORE to set
     */
    public void setFSTORE(FirebaseFirestore FSTORE) {
        this.FSTORE = FSTORE;
    }

    /**
     * @param musicianReference musicianReference to set
     */
    public void setMusicianReference(CollectionReference musicianReference) {
        this.musicianReference = musicianReference;
    }

    /**
     * Get location of musician
     */
    private void getMusicianLocation()
    {
        getMusicianLocation.whereEqualTo("user-ref", fAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                Log.d(TAG, "Successfully obtained Venue reference.");

                List<DocumentSnapshot> venues = queryDocumentSnapshots.getDocuments();

                if(!venues.isEmpty())
                {
                    Log.d(TAG, "Successful get of venue.");
                    DocumentSnapshot venue = venues.get(0);

                    musicianLatitude = Double.parseDouble(venue.get("latitude").toString());
                    musicianLongitude = Double.parseDouble(venue.get("longitude").toString());
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