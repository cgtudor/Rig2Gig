package com.gangoffive.rig2gig;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageBandMembersActivity extends AppCompatActivity implements CreateAdvertisement{

    private String bandRef, type, removedRef, uID, userName;
    private ListingManager bandInfoManager;
    private Map<String, Object> band;
    private List memberRefs;
    private ArrayList  names;
    private ArrayList<List> musicanBands;
    private ArrayList <ListingManager> musicianManagers;
    private ArrayList <Map<String, Object>> musicians;
    private int membersDownloaded, position;
    private GridView gridView;
    private ImageView addImage;
    private TextView addMemberText;
    private boolean firstDeletion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_band_members);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        uID = FirebaseAuth.getInstance().getUid();
        bandRef = getIntent().getStringExtra("EXTRA_BAND_ID");
        String listingRef = "profileEdit";
        type = "Band";
        position = -1;
        membersDownloaded = 0;
        firstDeletion = false;
        bandInfoManager = new ListingManager(bandRef, type, listingRef);
        bandInfoManager.getUserInfo(this);
        addMemberText = findViewById(R.id.addMemberText);
        addMemberText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {searchForMembers();}
        });
        addImage = findViewById(R.id.addImage);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {searchForMembers();}
        });
    }

    @Override
    public void onSuccessFromDatabase(Map<String, Object> data)
    {
        if (band == null)
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

    @Override
    public void populateInitialFields() {
        gridView = (GridView) findViewById( R.id.gridView);
        BandMemberRemoverAdapter customAdapter = new BandMemberRemoverAdapter(names, memberRefs, this);
        gridView.setAdapter(customAdapter);
    }

    public void confirmRemoveMember(String member, int position)
    {
        Intent intent =  new Intent(this, DeleteMemberConfirmation.class);
        intent.putExtra("EXTRA_NAME", member);
        intent.putExtra("EXTRA_POSITION", position);
        startActivityForResult(intent, 1);
/*        Toast.makeText(ManageBandMembersActivity.this,
                member + " has been removed",
                Toast.LENGTH_LONG).show();*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK)
        {
            position = data.getIntExtra("EXTRA_POSITION", -1);
            if (position != -1)
            {
                removeMember(position);
            }
        }
    }

    public void removeMember(int pos)
    {
        position = pos;
        removedRef = (String)memberRefs.get(position);
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
            names.remove(position);
            band = null;
            gridView = null;
            position = -1;
            membersDownloaded = 0;
            bandInfoManager.getUserInfo(this);
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
        startActivity(intent);
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
}
