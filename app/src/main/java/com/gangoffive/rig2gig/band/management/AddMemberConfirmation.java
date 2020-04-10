package com.gangoffive.rig2gig.band.management;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.gangoffive.rig2gig.advert.management.CreateAdvertisement;
import com.gangoffive.rig2gig.firebase.ListingManager;
import com.gangoffive.rig2gig.navbar.NavBarActivity;
import com.gangoffive.rig2gig.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddMemberConfirmation extends Activity implements CreateAdvertisement {

    private int height, width;
    private Button yes, no;
    private TextView confirmationText;
    private FirebaseFirestore db;
    private int position;
    private String name;
    private String musicianRef, bandRef, userRef, bandName, inviterName, usersMusicianRef;
    private boolean sendingInvite, checkIfInBand;
    private ListingManager musicManager;
    private CollectionReference received;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_layout);
        db = FirebaseFirestore.getInstance();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = (metrics.heightPixels) /100 * 30;
        width = (metrics.widthPixels) /100 * 80;
        getWindow().setLayout(width,height);
        initVariables();
        yes = findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginSendInvite();
            }
        });
        no = findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Initialise variables onCreate
     */
    public void initVariables()
    {
        Intent intent = getIntent();
        name = intent.getStringExtra("EXTRA_NAME");
        position = intent.getIntExtra("EXTRA_POSITION", -1);
        musicianRef = intent.getStringExtra("EXTRA_MUSICIAN_ID");
        bandRef = intent.getStringExtra("EXTRA_BAND_ID");
        userRef = intent.getStringExtra("EXTRA_USER_ID");
        bandName = intent.getStringExtra("EXTRA_BAND_NAME");
        inviterName = intent.getStringExtra("EXTRA_INVITER_NAME");
        usersMusicianRef = intent.getStringExtra("EXTRA_USER_MUSICIAN_REF");
        musicManager = new ListingManager(usersMusicianRef,"Musician","");
        confirmationText = findViewById(R.id.confirmationText);
        confirmationText.setText("Are you sure you want to invite this person to your band?");
        sendingInvite = false;
        checkIfInBand = false;
    }

    /**
     * Begin checks before sending an invite
     */
    public void beginSendInvite()
    {
        sendingInvite = true;
        checkIfInBand();
    }

    /**
     * Check if the user is still in the band
     */
    public void checkIfInBand()
    {
        checkIfInBand = true;
        if (musicManager != null)
        {
            musicManager.getUserInfo(this);
        }
    }

    /**
     * handle success from database
     */
    @Override
    public void onSuccessFromDatabase(Map<String, Object> data) {
        if (checkIfInBand) {
            checkIfInBand = false;
            if (data != null)
            {
                if (!((List) data.get("bands")).contains(bandRef)) {
                    Intent intent = new Intent(this, NavBarActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if (sendingInvite) {
                    sendingInvite = false;
                    sendInvite();
                }
            }
            else
            {
                Toast.makeText(this,
                        "Invite not sent.  Check you connection and try again.",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    /**
     * @return user's id
     */
    public String getUserId()
    {
        return FirebaseAuth.getInstance().getUid();
    }

    /**
     * Generate a band invite to be posted to the database
     * @return generated invite
     */
    public HashMap generateInvite()
    {
        HashMap<String, Object> request = new HashMap<>();
        request.put("type", "join-request");
        request.put("posting-date", Timestamp.now());
        request.put("band-ref",bandRef);
        request.put("sent-from", getUserId());
        request.put("sent-from-type", "bands");
        request.put("sent-from-ref",bandRef);
        request.put("sent-to-type", "musicians");
        request.put("sent-to-ref", musicianRef);
        request.put("musician-ref", musicianRef);
        request.put("notification-title","You have been invited to join a band!");
        request.put("notification-message", inviterName + " would like you to join their band " + bandName + ".");
        return request;
    }

    /**
     * Post generated invite to the database
     */
    public void sendInvite()
    {
        if (userRef != null)
        {
            HashMap request = generateInvite();
            received = db.collection("communications")
                    .document(userRef)
                    .collection("received");
            received.add(request)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful())
                            {
                                Log.d("FIRESTORE", "Invite request added with info " + task.getResult().toString());
                                logInvite();

                            }
                            else
                            {
                                Log.d("FIRESTORE", "Invite request failed with ", task.getException());
                                Toast.makeText(AddMemberConfirmation.this, "Invitation not sent.  " +
                                        "Check your connection and try again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    /**
     * Generate a band invite log to be posted to the database
     * @return generated band invite log
     */
    public HashMap generateLoggedInvite()
    {
        HashMap<String, Object> request = new HashMap<>();
        request.put("type", "join-request");
        request.put("posting-date", Timestamp.now());
        request.put("sent-to", userRef);
        request.put("sent-from-ref",bandRef);
        request.put("sent-from-type", "bands");
        request.put("sent-to-ref", musicianRef);
        request.put("sent-to-type", "musicians");
        request.put("notification-title","You have been invited to join a band!");
        request.put("notification-message", inviterName + " would like you to join their band " + bandName + ".");
        return request;
    }

    /**
     * Post generated invite log to the database
     */
    public void logInvite()
    {
        HashMap<String, Object> request = generateLoggedInvite();
        received = db.collection("communications")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("sent");
        received.add(request)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful())
                        {
                            Log.d("FIRESTORE", "Invite request sent with info " + task.getResult().toString());
                        }
                        else
                        {
                            Log.d("FIRESTORE", "Invite request failed with ", task.getException());
                            Toast.makeText(AddMemberConfirmation.this, "Invitation sent to user" +
                                    "however not recorded in your notifications", Toast.LENGTH_LONG).show();
                        }
                        onSuccessfulInvite();
                    }
                });
    }

    /**
     * Handle a successful band invite
     */
    public void onSuccessfulInvite()
    {
        Toast.makeText(this, name + " has been invited to join your band.", Toast.LENGTH_LONG).show();
        Intent result = new Intent();
        result.putExtra("EXTRA_POSITION", position);
        setResult(RESULT_OK, result);
        finish();
    }

    /**
     * @return db
     */
    public FirebaseFirestore getDb() {
        return db;
    }

    /**
     * @return position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return musicianRef
     */
    public String getMusicianRef() {
        return musicianRef;
    }

    /**
     * @return bandRef
     */
    public String getBandRef() {
        return bandRef;
    }

    /**
     * @return userRef
     */
    public String getUserRef() {
        return userRef;
    }

    /**
     * @return bandName
     */
    public String getBandName() {
        return bandName;
    }

    /**
     * @return inviterName
     */
    public String getInviterName() {
        return inviterName;
    }

    /**
     * @return userMusicianRef
     */
    public String getUsersMusicianRef() {
        return usersMusicianRef;
    }

    /**
     * @return sendingInvite
     */
    public boolean isSendingInvite() {
        return sendingInvite;
    }

    /**
     * @return checkIfInBand
     */
    public boolean isCheckIfInBand() {
        return checkIfInBand;
    }

    /**
     * @return musicManager
     */
    public ListingManager getMusicManager() {
        return musicManager;
    }

    /**
     * @param position position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * @param name name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param musicianRef musicianRef to set
     */
    public void setMusicianRef(String musicianRef) {
        this.musicianRef = musicianRef;
    }

    /**
     * @param bandRef bandRef to set
     */
    public void setBandRef(String bandRef) {
        this.bandRef = bandRef;
    }

    /**
     * @param userRef userRef to set
     */
    public void setUserRef(String userRef) {
        this.userRef = userRef;
    }

    /**
     * @param bandName bandName to set
     */
    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    /**
     * @param inviterName inviterName to set
     */
    public void setInviterName(String inviterName) {
        this.inviterName = inviterName;
    }

    /**
     * @param usersMusicianRef userMusicianRef to set
     */
    public void setUsersMusicianRef(String usersMusicianRef) {
        this.usersMusicianRef = usersMusicianRef;
    }

    /**
     * @param sendingInvite sendingInvite to set
     */
    public void setSendingInvite(boolean sendingInvite) {
        this.sendingInvite = sendingInvite;
    }

    /**
     * @param checkIfInBand checkIfInBand to set
     */
    public void setCheckIfInBand(boolean checkIfInBand) {
        this.checkIfInBand = checkIfInBand;
    }

    /**
     * @param musicManager musicManager to set
     */
    public void setMusicManager(ListingManager musicManager) {
        this.musicManager = musicManager;
    }

    /**
     * @param db db to set
     */
    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }

    /**
     * @param received received to set
     */
    public void setReceived(CollectionReference received) {
        this.received = received;
    }

    /**
     * Not used
     */
    @Override
    public void setViewReferences() {}

    /**
     * Not used
     */
    @Override
    public void populateInitialFields() {}

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
}
