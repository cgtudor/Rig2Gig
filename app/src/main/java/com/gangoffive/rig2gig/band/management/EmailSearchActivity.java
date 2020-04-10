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

    public void checkIfInBand()
    {
        bandManager.getUserInfo(this);
    }

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

    public void setupSearchBar()
    {
        searchBar.setIconifiedByDefault(false);
        searchBar.setOnQueryTextListener(this);
        searchBar.setSubmitButtonEnabled(true);
        searchBar.setQueryHint("Enter musician email address");
    }

    @Override
    public boolean onQueryTextSubmit(String typedText)
    {
        query = typedText.toLowerCase();
        submittingQuery = true;
        checkIfInBand();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

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

    public void displayMusician()
    {
        infoText.setText("");
        invite.setVisibility(View.VISIBLE);
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginConfirmAddMember();
            }
        });
        nameLabel.setTextColor(Color.BLACK);
        userLabel.setTextColor(Color.BLACK);
        locationLabel.setTextColor(Color.BLACK);
        ratingLabel.setTextColor(Color.BLACK);
        name.setText(musicianData.get("name").toString());
        user.setText(userData.get("username").toString());
        location.setText(musicianData.get("location").toString());
        rating.setText(musicianData.get("rating").toString());
    }

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

    @Override
    public void populateInitialFields() {

    }

    public void beginConfirmAddMember ()
    {
        confirmingAdd = true;
        checkIfInBand();
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        fader.setVisibility(View.GONE);
        if (requestCode == 1 && resultCode == RESULT_OK)
        {
            invite.setOnClickListener(null);
            invite.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed()
    {
        handleBack();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        handleBack();
        return true;
    }

    public void handleBack()
    {
        backClicked = true;
        checkIfInBand();
    }

    public void goBack()
    {
        Intent intent = new Intent(this, ManageBandMembersActivity.class);
        intent.putExtra("EXTRA_BAND_ID", bandRef);
        startActivity(intent);
        finish();
    }

    @Override
    public void createAdvertisement() {

    }

    @Override
    public void cancelAdvertisement() {

    }

    @Override
    public void listingDataMap() {

    }

    @Override
    public boolean validateDataMap() {
        return false;
    }

    @Override
    public void onSuccessFromDatabase(Map<String, Object> data, Map<String, Object> listingData) {

    }

    @Override
    public ImageView getImageView() {
        return null;
    }

    @Override
    public void handleDatabaseResponse(Enum creationResult) {
    }

    @Override
    public void onSuccessfulImageDownload() {

    }
}