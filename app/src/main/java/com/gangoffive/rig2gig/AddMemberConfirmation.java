package com.gangoffive.rig2gig;

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

public class AddMemberConfirmation extends Activity implements CreateAdvertisement{

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

    public void beginSendInvite()
    {
        sendingInvite = true;
        checkIfInBand();
    }

    public void checkIfInBand()
    {
        checkIfInBand = true;
        if (musicManager != null)
        {
            musicManager.getUserInfo(this);
        }
    }

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

    public String getUserId()
    {
        return FirebaseAuth.getInstance().getUid();
    }

    public HashMap generateInvite()
    {
        HashMap<String, Object> request = new HashMap<>();
        request.put("type", "join-request");
        request.put("posting-date", Timestamp.now());
        request.put("sent-from", getUserId());
        request.put("band-ref",bandRef);
        request.put("musician-ref", musicianRef);
        request.put("notification-title","You have been invited to join a band!");
        request.put("notification-message", inviterName + " would like you to join their band " + bandName + ".");
        return request;
    }

    public void sendInvite()
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

    public HashMap generateLoggedInvite()
    {
        HashMap<String, Object> request = new HashMap<>();
        request.put("type", "join-request");
        request.put("posting-date", Timestamp.now());
        request.put("sent-to", userRef);
        request.put("band-ref",bandRef);
        request.put("musician-ref", musicianRef);
        request.put("notification-title","You have been invited to join a band!");
        request.put("notification-message", inviterName + " would like you to join their band " + bandName + ".");
        return request;
    }

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

    public void onSuccessfulInvite()
    {
        Toast.makeText(this, name + " has been invited to join your band.", Toast.LENGTH_LONG).show();
        Intent result = new Intent();
        result.putExtra("EXTRA_POSITION", position);
        setResult(RESULT_OK, result);
        finish();
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public String getMusicianRef() {
        return musicianRef;
    }

    public String getBandRef() {
        return bandRef;
    }

    public String getUserRef() {
        return userRef;
    }

    public String getBandName() {
        return bandName;
    }

    public String getInviterName() {
        return inviterName;
    }

    public String getUsersMusicianRef() {
        return usersMusicianRef;
    }

    public boolean isSendingInvite() {
        return sendingInvite;
    }

    public boolean isCheckIfInBand() {
        return checkIfInBand;
    }

    public ListingManager getMusicManager() {
        return musicManager;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMusicianRef(String musicianRef) {
        this.musicianRef = musicianRef;
    }

    public void setBandRef(String bandRef) {
        this.bandRef = bandRef;
    }

    public void setUserRef(String userRef) {
        this.userRef = userRef;
    }

    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    public void setInviterName(String inviterName) {
        this.inviterName = inviterName;
    }

    public void setUsersMusicianRef(String usersMusicianRef) {
        this.usersMusicianRef = usersMusicianRef;
    }

    public void setSendingInvite(boolean sendingInvite) {
        this.sendingInvite = sendingInvite;
    }

    public void setCheckIfInBand(boolean checkIfInBand) {
        this.checkIfInBand = checkIfInBand;
    }

    public void setMusicManager(ListingManager musicManager) {
        this.musicManager = musicManager;
    }

    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }

    public void setReceived(CollectionReference received) {
        this.received = received;
    }

    @Override
    public void setViewReferences() {

    }

    @Override
    public void populateInitialFields() {

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
