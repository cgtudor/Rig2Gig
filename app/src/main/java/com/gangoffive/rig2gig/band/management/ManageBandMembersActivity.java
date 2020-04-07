package com.gangoffive.rig2gig.band.management;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gangoffive.rig2gig.navbar.NavBarActivity;
import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.advert.management.CreateAdvertisement;
import com.gangoffive.rig2gig.firebase.ListingManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageBandMembersActivity extends AppCompatActivity implements CreateAdvertisement {

    private String bandRef, type, removedRef, uID, userName, usersMusicianRef, removeMember, removeeUserRef;
    private Button addByEmail, addByName;
    private ListingManager bandInfoManager;
    private Map<String, Object> band;
    private List memberRefs;
    private ArrayList  names;
    private ArrayList<List> musicanBands;
    private ArrayList <ListingManager> musicianManagers;
    private ArrayList <Map<String, Object>> musicians;
    private int membersDownloaded, position, removePosition;
    private GridView gridView;
    private boolean firstDeletion, backClicked, stillInBand, checkIfInBand, searchingByName, searchingByEmail,
            removingMember, removingMemberConfirmed;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_band_members);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Manage your band");
        db = FirebaseFirestore.getInstance();
        uID = FirebaseAuth.getInstance().getUid();
        bandRef = getIntent().getStringExtra("EXTRA_BAND_ID");
        String listingRef = "profileEdit";
        type = "Band";
        position = -1;
        membersDownloaded = 0;
        firstDeletion = false;
        backClicked = false;
        stillInBand = true;
        checkIfInBand = false;
        searchingByName = false;
        searchingByEmail = false;
        removingMember = false;
        bandInfoManager = new ListingManager(bandRef, type, listingRef);
        bandInfoManager.getUserInfo(this);
        addByEmail = findViewById(R.id.add_by_email);
        addByEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                searchingByEmail = true;
                checkIfInBand();
            }
        });
        addByName = findViewById(R.id.add_by_name);
        addByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                searchingByName = true;
                checkIfInBand();
            }
        });
    }

    @Override
    public void onSuccessFromDatabase(Map<String, Object> data)
    {
        if(data != null)
        {
            if (checkIfInBand)
            {
                checkIfInBand = false;
                band = data;
                if (!((List)band.get("members")).contains(usersMusicianRef))
                {
                    Intent intent = new Intent(this, NavBarActivity.class);
                    startActivity(intent);
                    finish();
                }
                if (backClicked)
                {
                    goBack();
                }
                if (searchingByName)
                {
                    searchingByName = false;
                    searchForMembers();
                }
                if(searchingByEmail)
                {
                    searchingByEmail = false;
                    searchByEmail();
                }
                if (removingMember)
                {
                    removingMember = false;
                    areYouSureRemove();
                }
                if (removingMemberConfirmed)
                {
                    removingMemberConfirmed = false;
                    finaliseRemoveMember();
                }
            }
            else if (band == null)
            {
                band = data;
                memberRefs = (ArrayList)(band.get("members"));
                if (memberRefs.size() > 0)
                {
                    names = new ArrayList<>();
                    musicanBands = new ArrayList<>();
                    musicianManagers = new ArrayList<>();
                    musicians = new ArrayList<>();
                    for (int i = 0; i < memberRefs.size(); i++)
                    {
                        musicianManagers.add(new ListingManager(memberRefs.get(i).toString(),"Musician","profileEdit"));
                    }
                    musicianManagers.get(membersDownloaded).getUserInfo(this);
                }
                else
                {
                    populateInitialFields();
                }
            }
            else if (membersDownloaded != memberRefs.size() - 1)
            {
                names.add(data.get("name").toString());
                musicanBands.add((List)(data.get("bands")));
                musicians.add(data);
                membersDownloaded++;
                musicianManagers.get(membersDownloaded).getUserInfo(this);
            }
            else
            {
                names.add(data.get("name").toString());
                musicanBands.add((List)(data.get("bands")));
                musicians.add(data);
                for (int i = 0; i < musicians.size(); i++)
                {
                    if (musicians.get(i).get("user-ref").equals(uID))
                    {
                        userName = musicians.get(i).get("name").toString();
                        usersMusicianRef = memberRefs.get(i).toString();
                    }
                }
                for (Map<String, Object> musician: musicians)
                {
                    if (musician.get("user-ref").equals(uID))
                    {
                        userName = musician.get("name").toString();
                    }
                }
                populateInitialFields();
            }
        }
    }

    @Override
    public void populateInitialFields() {
        gridView = (GridView) findViewById( R.id.gridView);
        BandMemberRemoverAdapter customAdapter = new BandMemberRemoverAdapter(names, memberRefs, this);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                gridView.setAdapter(customAdapter);
            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent =  new Intent(ManageBandMembersActivity.this, BandMemberDetails.class);
                intent.putExtra("EXTRA_MUSICIAN_REF", memberRefs.get(position).toString());
                startActivity(intent);
            }
        });
    }

    public void confirmRemoveMember(String member, int position)
    {
        removeMember = member;
        removePosition = position;
        removingMember = true;
        checkIfInBand();
    }

    public void areYouSureRemove()
    {
        Intent intent =  new Intent(this, DeleteMemberConfirmation.class);
        intent.putExtra("EXTRA_NAME", removeMember);
        intent.putExtra("EXTRA_POSITION", removePosition);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK)
        {
            position = data.getIntExtra("EXTRA_POSITION", -1);
            if (position != -1)
            {
                beginRemoveMember();
            }
        }
    }

    public void beginRemoveMember()
    {
        removingMemberConfirmed = true;
        checkIfInBand();
    }

    public void finaliseRemoveMember()
    {
        removedRef = (String)memberRefs.get(position);
        removeeUserRef = musicians.get(position).get("user-ref").toString();
        memberRefs.remove(position);
        band.put("members",memberRefs);
        firstDeletion = true;
        bandInfoManager.postDataToDatabase((HashMap)band, null, this);
        musicanBands.get(position).remove(bandRef);
        musicians.get(position).put("bands",musicanBands.get(position));
        musicianManagers.get(position).postDataToDatabase((HashMap)musicians.get(position),null,this);
    }

    @Override
    public void handleDatabaseResponse(Enum creationResult)
    {
        if (!firstDeletion && creationResult == ListingManager.CreationResult.SUCCESS)
        {
            Toast.makeText(ManageBandMembersActivity.this,
                    names.get(position) + " has been removed",
                    Toast.LENGTH_LONG).show();
            updateJoinedCommunication();
            if(removedRef.equals(usersMusicianRef))
            {
                Intent intent = new Intent(this, NavBarActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                names.remove(position);
                band = null;
                gridView = null;
                position = -1;
                membersDownloaded = 0;
                bandInfoManager.getUserInfo(this);
            }
        }
        else if (creationResult == ListingManager.CreationResult.LISTING_FAILURE)
        {
            Toast.makeText(ManageBandMembersActivity.this,
                    "Failed to remove " + names.get(position) +" from band.  Check your " +
                            "connection and try again",
                    Toast.LENGTH_LONG).show();
            memberRefs.add(position,removedRef);
        }
        firstDeletion = false;

    }

    public void searchForMembers()
    {
        Intent intent = new Intent(this, MusicianSearchActivity.class);
        intent.putExtra("EXTRA_CURRENT_MEMBERS", (Serializable) memberRefs);
        intent.putExtra("EXTRA_BAND_ID", bandRef);
        intent.putExtra("EXTRA_BAND_NAME",band.get("name").toString());
        intent.putExtra("EXTRA_USER_NAME",userName);
        intent.putExtra("EXTRA_USERS_MUSICIAN_ID", usersMusicianRef);
        startActivity(intent);
        finish();
    }

    public void searchByEmail()
    {
        Intent intent = new Intent(this, EmailSearchActivity.class);
        intent.putExtra("EXTRA_CURRENT_MEMBERS", (Serializable) memberRefs);
        intent.putExtra("EXTRA_BAND_ID", bandRef);
        intent.putExtra("EXTRA_BAND_NAME",band.get("name").toString());
        intent.putExtra("EXTRA_USER_NAME",userName);
        intent.putExtra("EXTRA_USERS_MUSICIAN_ID", usersMusicianRef);
        startActivity(intent);
        finish();
    }

    public void checkIfInBand()
    {
        checkIfInBand = true;
        bandInfoManager.getUserInfo(this);
    }

    @Override
    public void onBackPressed()
    {
        backClicked = true;
        checkIfInBand();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        backClicked = true;
        checkIfInBand();
        return true;
    }

    public void goBack()
    {
        finish();
    }

    public void updateJoinedCommunication() {
        CollectionReference joinedBand = db.collection("communications")
                .document(removeeUserRef)
                .collection("received");
        joinedBand.whereEqualTo("type", "accepted-invite")
                .whereEqualTo("band-ref", bandRef)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot query = task.getResult();
                            if (!query.isEmpty()) {
                                List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                                for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                    String docRef = documentSnapshot.getId();
                                    DocumentReference receiverCommDoc = db.collection("communications")
                                            .document(removeeUserRef)
                                            .collection("received")
                                            .document(docRef);
                                    receiverCommDoc.update("type", "left-band")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("FIRESTORE", "Updated communication document with leaving band");
                                                    } else {
                                                        Log.d("FIRESTORE", "Failed to update communication document with leaving band: " + task.getException());
                                                    }
                                                }
                                            });

                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onSuccessfulImageDownload()
    {
    }

    @Override
    public void setViewReferences() {

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

    public void setUsersMusicianRef(String usersMusicianRef) {
        this.usersMusicianRef = usersMusicianRef;
    }
}
