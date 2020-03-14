package com.gangoffive.rig2gig;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageBandMembersActivity extends AppCompatActivity implements CreateAdvertisement{

    private String bandRef, type, removedRef;
    private ListingManager bandInfoManager;
    private Map<String, Object> band;
    private List memberRefs;
    private ArrayList  names;
    private ArrayList <ListingManager> musicianManagers;
    private int membersDownloaded;
    private GridView gridView;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_band_members);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bandRef = getIntent().getStringExtra("EXTRA_BAND_ID");
        String listingRef = "profileEdit";
        type = "Band";
        position = -1;
        membersDownloaded = 0;
        bandInfoManager = new ListingManager(bandRef, type, listingRef);
        bandInfoManager.getUserInfo(this);
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
                musicianManagers = new ArrayList<>();
                for (int i = 0; i < memberRefs.size(); i++)
                {
                    musicianManagers.add(new ListingManager(memberRefs.get(i).toString(),"Musician",""));
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
            membersDownloaded++;
            musicianManagers.get(membersDownloaded).getUserInfo(this);
        }
        else
        {
            names.add(data.get("name").toString());
            populateInitialFields();
        }
    }

    @Override
    public void populateInitialFields() {
        gridView = (GridView) findViewById( R.id.gridView);
        BandMemberAdapter customAdapter = new BandMemberAdapter(names, memberRefs, this);
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
        bandInfoManager.postDataToDatabase((HashMap)band, null, this);
    }

    @Override
    public void handleDatabaseResponse(Enum creationResult)
    {
        if (creationResult == ListingManager.CreationResult.SUCCESS)
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
