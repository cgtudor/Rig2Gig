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
        Intent intent = getIntent();
        name = intent.getStringExtra("EXTRA_NAME");
        position = intent.getIntExtra("EXTRA_POSITION", -1);
        musicianRef = intent.getStringExtra("EXTRA_MUSICIAN_ID");
        bandRef = intent.getStringExtra("EXTRA_BAND_ID");
        userRef = intent.getStringExtra("EXTRA_USER_ID");
        bandName = getIntent().getStringExtra("EXTRA_BAND_NAME");
        inviterName = getIntent().getStringExtra("EXTRA_INVITER_NAME");
        usersMusicianRef = getIntent().getStringExtra("EXTRA_USER_MUSICIAN_REF");
        musicManager = new ListingManager(usersMusicianRef,"Musician","");
        confirmationText = findViewById(R.id.confirmationText);
        confirmationText.setText("Are you sure you want to invite this person to your band?");
        sendingInvite = false;
        checkIfInBand = false;
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

    public void beginSendInvite()
    {
        sendingInvite = true;
        checkIfInBand();
    }

    public void checkIfInBand()
    {
        checkIfInBand = true;
        musicManager.getUserInfo(this);
    }

    @Override
    public void onSuccessFromDatabase(Map<String, Object> data) {
        if (checkIfInBand) {
            checkIfInBand = false;
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
    }

    public void sendInvite()
    {
        HashMap<String, Object> request = new HashMap<>();
        request.put("type", "join-request");
        request.put("posting-date", Timestamp.now());
        request.put("sent-from", FirebaseAuth.getInstance().getUid());
        request.put("band-ref",bandRef);
        request.put("musician-ref", musicianRef);
        request.put("notification-title","You have been invited to join a band!");
        request.put("notification-message", inviterName + " would like you to join their band " + bandName + ".");

        CollectionReference received = db.collection("communications")
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

    public void logInvite()
    {
        HashMap<String, Object> request = new HashMap<>();
        request.put("type", "join-request");
        request.put("posting-date", Timestamp.now());
        request.put("sent-to", userRef);
        request.put("band-ref",bandRef);
        request.put("musician-ref", musicianRef);
        request.put("notification-title","You have been invited to join a band!");
        request.put("notification-message", inviterName + " would like you to join their band " + bandName + ".");

        CollectionReference received = db.collection("communications")
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
