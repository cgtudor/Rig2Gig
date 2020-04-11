package com.gangoffive.rig2gig.band.management;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gangoffive.rig2gig.firebase.GlideApp;
import com.gangoffive.rig2gig.navbar.NavBarActivity;
import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.advert.management.CreateAdvertisement;
import com.gangoffive.rig2gig.firebase.ListingManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, CreateAdvertisement {

    private FirebaseStorage storage;
    private StorageReference storageRef, imageRef;
    private SearchView searchBar;
    private TextView name, user, location, rating, nameLabel, userLabel, locationLabel, ratingLabel, infoText, fader;
    private ImageView image;
    private Button invite;
    private HashMap<String, Object> userData, musicianData;
    private FirebaseFirestore db;
    private CollectionReference musicianDb, userDb;
    private Activity activityRef;
    private ListingManager bandManager;
    private String searchedMusicianId, searchedUserRef, bandRef, bandName, userName, usersMusicianRef, query;
    private boolean submittingQuery, backClicked, confirmingAdd;
    private String notFound = "User not found.  Check email is correct.";
    private String alreadyInBand = "User is already in your band.";
    private String userInvited = "You have already invited this person to join your band.";
    private ArrayList<String> members;
    private View.OnClickListener sendInvite = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            invite.setOnClickListener(null);
            beginConfirmAddMember();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_search_activity);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Invite musicians");
        bandRef = getIntent().getStringExtra("EXTRA_BAND_ID");
        bandName = getIntent().getStringExtra("EXTRA_BAND_NAME");
        userName = getIntent().getStringExtra("EXTRA_USER_NAME");
        usersMusicianRef = getIntent().getStringExtra("EXTRA_USERS_MUSICIAN_ID");
        db = FirebaseFirestore.getInstance();
        userDb = db.collection("users");
        musicianDb = db.collection("musicians");
        submittingQuery = false;
        backClicked = false;
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        bandManager = new ListingManager(bandRef, "Band", "");
        bandManager.getUserInfo(this);
        setViewReferences();
        setupSearchBar();
    }

    /**
     * Obtain user info from database, to check if in band
     */
    public void checkIfInBand()
    {
        bandManager.getUserInfo(this);
    }

    /**
     * handle response from database
     * @param data map of user data
     */
    @Override
    public void onSuccessFromDatabase(Map<String, Object> data) {
        if (!((List)data.get("members")).contains(usersMusicianRef))
        {
            Intent intent = new Intent(this, NavBarActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            members = (ArrayList)data.get("members");
            if (submittingQuery)
            {
                submittingQuery = false;
                queryUserDatabase();
            }
            if(confirmingAdd)
            {
                confirmingAdd = false;
                confirmAddMember();
            }
            if(backClicked)
            {
                backClicked = false;
                goBack();
            }
        }
    }

    /**
     * Set view references
     */
    @Override
    public void setViewReferences()
    {
        invite = findViewById(R.id.invite);
        searchBar = findViewById(R.id.search_bar);
        name = findViewById(R.id.name);
        user = findViewById(R.id.userName);
        location = findViewById(R.id.location);
        rating = findViewById(R.id.rating);
        nameLabel = findViewById(R.id.nameLabel);
        userLabel = findViewById(R.id.userNameLabel);
        locationLabel = findViewById(R.id.locationLabel);
        ratingLabel = findViewById(R.id.ratingLabel);
        image = findViewById(R.id.image);
        infoText = findViewById(R.id.infoText);
        fader = findViewById(R.id.fader);
    }

    /**
     * Setup search bar
     */
    public void setupSearchBar()
    {
        searchBar.setIconifiedByDefault(false);
        searchBar.setOnQueryTextListener(this);
        searchBar.setSubmitButtonEnabled(true);
        searchBar.setQueryHint("Enter musician email address");
    }

    /**
     * Handle query submission on search bar
     * @param typedText text typed
     * @return false
     */
    @Override
    public boolean onQueryTextSubmit(String typedText)
    {
        query = typedText.toLowerCase();
        submittingQuery = true;
        checkIfInBand();
        return false;
    }

    /**
     * Not used
     * @param newText typed text
     * @return false
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /**
     * Query database for typed email address
     */
    public void queryUserDatabase()
    {
        Query first = userDb
                .limit(1)
                .whereEqualTo("index-email-address", query);
        first.get()
             .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                if (document.exists())
                                {
                                    searchedUserRef = document.getId();
                                    userData = (HashMap)document.getData();
                                    checkInvitesSent();

                                }
                                else
                                {
                                    failedToFind(notFound);
                                }
                            }
                            if(task.getResult().isEmpty())
                            {
                                failedToFind(notFound);
                            }
                        }
                        else
                        {
                            failedToFind(notFound);
                        }
                    }
                })
             .addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     failedToFind(notFound);
                 }
    });
    }

    /**
     * Check if the email address search for has an associated, active invite already
     */
    public void checkInvitesSent()
    {
        CollectionReference sentMessages = db.collection("communications").document(searchedUserRef).collection("received");
        sentMessages.whereEqualTo("sent-from", FirebaseAuth.getInstance().getUid())
                .whereIn("type", Arrays.asList("join-request"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful())
                        {
                            QuerySnapshot query = task.getResult();
                            if(!query.isEmpty())
                            {
                                failedToFind(userInvited);
                            }
                            else
                            {
                                queryMusicianDatabase(searchedUserRef);
                            }

                        }
                        else
                        {
                            Log.e("FIREBASE", "Sent messages failed with ", task.getException());
                        }
                    }
                });
    }

    /**
     * Obtain search for musicians data and image to display
     * @param userRef reference of searched musician
     */
    public void queryMusicianDatabase(String userRef)
    {
        Query first = musicianDb
                .limit(1)
                .whereEqualTo("user-ref", userRef);
        first.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                if (document.exists())
                                {
                                    searchedMusicianId = document.getId();
                                    if(members.contains(searchedMusicianId))
                                    {
                                        failedToFind(alreadyInBand);
                                    }
                                    else
                                    {
                                        imageRef = storageRef.child("/images/musicians/" + document.getId() + ".jpg");
                                        image = findViewById(R.id.image);
                                        GlideApp.get((Context)EmailSearchActivity.this)
                                                .with(EmailSearchActivity.this)
                                                .load(imageRef)
                                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                .skipMemoryCache(true)
                                                .into(image);
                                        musicianData = (HashMap)document.getData();
                                        displayMusician();
                                    }
                                }
                                else
                                {
                                    failedToFind(notFound);
                                }
                            }
                            if(task.getResult().isEmpty())
                            {
                                failedToFind(notFound);
                            }
                        }
                        else
                        {
                            failedToFind(notFound);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        failedToFind(notFound);
                    }
                });
    }

    /**
     * Display musician information obtained from database
     */
    public void displayMusician()
    {
        infoText.setText("");
        invite.setVisibility(View.VISIBLE);
        invite.setOnClickListener(sendInvite);
        nameLabel.setTextColor(Color.BLACK);
        userLabel.setTextColor(Color.BLACK);
        locationLabel.setTextColor(Color.BLACK);
        ratingLabel.setTextColor(Color.BLACK);
        name.setText(musicianData.get("name").toString());
        user.setText(userData.get("username").toString());
        location.setText(musicianData.get("location").toString());
        rating.setText(musicianData.get("rating").toString());
    }

    /**
     * Handle when musician data is not found, or should not be found (ie already invited or in band)
     * @param text relevant message to display to user
     */
    public void failedToFind(String text)
    {
        infoText.setText(text);
        image.setImageDrawable(null);
        invite.setVisibility(View.GONE);
        nameLabel.setTextColor(Color.WHITE);
        userLabel.setTextColor(Color.WHITE);
        locationLabel.setTextColor(Color.WHITE);
        ratingLabel.setTextColor(Color.WHITE);
        name.setText("");
        user.setText("");
        location.setText("");
        rating.setText("");
    }

    /**
     * Begin the process of inviting the musician, checking if user is still in band
     */
    public void beginConfirmAddMember ()
    {
        confirmingAdd = true;
        checkIfInBand();
    }

    /**
     * Start popup intent to confirm if the musician is to be invited
     */
    public void confirmAddMember()
    {
        searchBar.clearFocus();
        fader = findViewById(R.id.fader);
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.darkerMain));
        fader.setVisibility(View.VISIBLE);
        Intent intent =  new Intent(this, AddMemberConfirmation.class);
        intent.putExtra("EXTRA_NAME", musicianData.get("name").toString());
        intent.putExtra("EXTRA_POSITION", 0);
        intent.putExtra("EXTRA_MUSICIAN_ID",searchedMusicianId);
        intent.putExtra("EXTRA_BAND_ID",bandRef);
        intent.putExtra("EXTRA_USER_ID",searchedUserRef);
        intent.putExtra("EXTRA_INVITER_NAME", userName);
        intent.putExtra("EXTRA_BAND_NAME", bandName);
        intent.putExtra("EXTRA_USER_MUSICIAN_REF", usersMusicianRef);
        startActivityForResult(intent, 1);
    }

    /**
     * Handle activity results, namely if the musician was confirmed to be invited
     * @param requestCode request code
     * @param resultCode result code
     * @param data intent data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        invite.setOnClickListener(sendInvite);
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        fader.setVisibility(View.GONE);
        if (requestCode == 1 && resultCode == RESULT_OK)
        {
            invite.setOnClickListener(null);
            invite.setVisibility(View.GONE);
        }
    }

    /**
     * Handle phone back button press
     */
    @Override
    public void onBackPressed()
    {
        handleBack();
    }

    /**
     * Handle app bar back button press
     * @param item item pressed
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        handleBack();
        return true;
    }

    /**
     * begin navigate back process, first checking if user remains in band
     */
    public void handleBack()
    {
        backClicked = true;
        checkIfInBand();
    }

    /**
     * Return to band manager after confirmation user remains in band
     */
    public void goBack()
    {
        Intent intent = new Intent(this, ManageBandMembersActivity.class);
        intent.putExtra("EXTRA_BAND_ID", bandRef);
        startActivity(intent);
        finish();
    }

    /**
     * Not used
     */
    @Override
    public void createAdvertisement() {}

    /**
     * Not used
     */
    @Override
    public void cancelAdvertisement() {}

    /**
     * Not used
     */
    @Override
    public void listingDataMap() {}

    /**
     * Not used
     */
    @Override
    public boolean validateDataMap() {return false;}

    /**
     * Not used
     */
    @Override
    public void onSuccessFromDatabase(Map<String, Object> data, Map<String, Object> listingData) {}

    /**
     * Not used
     */
    @Override
    public ImageView getImageView() {return null;}

    /**
     * Not used
     */
    @Override
    public void handleDatabaseResponse(Enum creationResult) {}

    /**
     * Not used
     */
    @Override
    public void onSuccessfulImageDownload() {}

    /**
     * Not used
     */
    @Override
    public void populateInitialFields() {}
}