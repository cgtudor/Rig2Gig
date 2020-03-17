package com.gangoffive.rig2gig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MusicianSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, CreateAdvertisement{

    private SearchView searchBar;
    private ListView listResults;
    private ArrayAdapter<String> resultsAdapter;
    private ArrayList<String> musicians, currentMemberRefs, searchRefs, searchNames, names, gridRefs, userRefs;
    private ArrayList<Boolean> invitesSent;
    private FirebaseFirestore db;
    private CollectionReference musicianDb;
    private Activity activityRef;
    private ArrayList <ListingManager> musicianManagers;
    private int membersDownloaded, remainingHeight, addPosition, invitesChecked;
    private GridView gridView;
    private ScrollView scroll;
    private String bandRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musician_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        currentMemberRefs = (ArrayList<String>)getIntent().getSerializableExtra("EXTRA_CURRENT_MEMBERS");
        bandRef = getIntent().getStringExtra("EXTRA_BAND_ID");
        db = FirebaseFirestore.getInstance();
        musicianDb = db.collection("musicians");
        resetLists();
        membersDownloaded = 0;
        invitesChecked = 0;
        activityRef = this;
        addPosition = -1;
        setupSearchBar();
    }

    public void setupSearchBar()
    {
        listResults = findViewById(R.id.list_results);
        searchBar = findViewById(R.id.search_bar);
        searchBar.setIconifiedByDefault(false);
        searchBar.setOnQueryTextListener(this);
        searchBar.setSubmitButtonEnabled(false);
        searchBar.setQueryHint("Enter musician name");
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int searchBarHeight = searchBar.getHeight();
        remainingHeight = (metrics.heightPixels) - searchBarHeight;
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
        if (typedText.length() > 0)
        {
            if (listResults.getAdapter() == null)
            {
                resetLists();
                membersDownloaded = 0;
                invitesChecked = 0;
                if (gridView != null)
                {
                    gridView.setMinimumHeight(0);
                    gridView.setAdapter(null);
                }
                if (scroll != null)
                {
                    scroll.setMinimumHeight(0);
                }
                listResults.setAdapter(resultsAdapter = new ArrayAdapter<String>(activityRef,
                        android.R.layout.simple_list_item_1,
                        musicians));
            }
            queryDatabase(typedText);

        }
        else
        {
            listResults.setAdapter(null);
        }
        return true;
    }

    public void queryDatabase(String query)
    {
        musicianDb.whereGreaterThanOrEqualTo("name",query.substring(0,1).toUpperCase() + query.substring(1))
                .whereLessThanOrEqualTo("name", query.substring(0,1).toUpperCase() + query.substring(1) + '\uf8ff')
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            resetLists();
                            listResults.setAdapter(null);
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                if (document.exists())
                                {
                                    if (!currentMemberRefs.contains(document.getId()))
                                    {
                                        searchRefs.add(document.getId());
                                        userRefs.add(document.getData().get("user-ref").toString());
                                        searchNames.add(document.getData().get("name").toString());
                                        if (!musicians.contains(document.getData().get("name").toString()))
                                        {
                                            musicians.add(document.getData().get("name").toString());
                                        }
                                    }
                                }
                            }
                            listResults.setAdapter(resultsAdapter = new ArrayAdapter<String>(activityRef,
                                    android.R.layout.simple_list_item_1,
                                    musicians));
                            listResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View v,
                                                        int position, long id) {
                                    generateResults(((TextView) v).getText().toString());
                                    ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                                            .hideSoftInputFromWindow(getCurrentFocus()
                                                    .getWindowToken(),0);
                                    listResults.setAdapter(null);
                                }
                            });
                        } else {
                            musicians = new ArrayList<>();
                            listResults.setAdapter(null);
                        }
                    }
                });
    }

    public void generateResults(String name)
    {
        for (int i = 0; i < searchNames.size(); i++)
        {
            if (searchNames.get(i).equals(name))
            {
                gridRefs.add(searchRefs.get(i));
            }
        }
        //use search refs to generate grid view of results
        if (gridRefs.size() > 0)
        {
            names = new ArrayList<>();
            musicianManagers = new ArrayList<>();
            for (int i = 0; i < gridRefs.size(); i++)
            {
                musicianManagers.add(new ListingManager(gridRefs.get(i).toString(),"Musician",""));
            }
            musicianManagers.get(membersDownloaded).getUserInfo(this);
        }
        else
        {
            checkInvitesSent();
        }
    }

    public void checkInvitesSent()
    {
        if (gridRefs.size() > 0)
        {
            CollectionReference sentMessages = db.collection("communications").document(FirebaseAuth.getInstance().getUid()).collection("sent");
            for (int i = 0; i < gridRefs.size(); i++)
            {
                sentMessages.whereEqualTo("sent-to", userRefs.get(i)).whereEqualTo("type", "join-request").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    QuerySnapshot query = task.getResult();
                                    if(!query.isEmpty())
                                    {
                                        invitesSent.add(true);
                                    }
                                    else
                                    {
                                        invitesSent.add(false);
                                    }
                                    invitesChecked++;
                                    if (invitesChecked == gridRefs.size())
                                    {
                                        populateInitialFields();
                                    }
                                }
                                else
                                {
                                    Log.e("FIREBASE", "Sent messages failed with ", task.getException());
                                }
                            }
                        });
            }
        }
    }

    @Override
    public void onSuccessFromDatabase(Map<String, Object> data) {
        if (membersDownloaded != gridRefs.size() - 1)
        {
            names.add(data.get("name").toString());
            membersDownloaded++;
            musicianManagers.get(membersDownloaded).getUserInfo(this);
        }
        else
        {
            names.add(data.get("name").toString());
            checkInvitesSent();
        }
    }

    @Override
    public void populateInitialFields() {
        gridView = (GridView) findViewById( R.id.gridView);
        scroll = findViewById( R.id.scroll);
        scroll.setMinimumHeight(remainingHeight);
        gridView.setMinimumHeight(remainingHeight);
        BandMemberAdderAdapter customAdapter = new BandMemberAdderAdapter(names, gridRefs, userRefs, invitesSent, this);
        for (int i = 0; i < gridRefs.size();i++)
        {
            if (invitesSent.get(i) == true)
            {

            }
        }
        gridView.setAdapter(customAdapter);
    }

    public void confirmAddMember(String member, int position)
    {
        searchBar.clearFocus();
        Intent intent =  new Intent(this, AddMemberConfirmation.class);
        intent.putExtra("EXTRA_NAME", member);
        intent.putExtra("EXTRA_POSITION", position);
        intent.putExtra("EXTRA_MUSICIAN_ID",searchRefs.get(position));
        intent.putExtra("EXTRA_BAND_ID",bandRef);
        intent.putExtra("EXTRA_USER_ID",userRefs.get(position));
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK)
        {
            addPosition = data.getIntExtra("EXTRA_POSITION", -1);
            if (addPosition != -1)
            {
                inviteMember();
            }
            else
            {
                searchBar.clearFocus();
            }
        }
    }

    public void inviteMember()
    {
        membersDownloaded = 0;
        invitesChecked = 0;
        invitesSent = new ArrayList<>();
        checkInvitesSent();
        searchBar.clearFocus();
    }

    public void resetLists()
    {
        musicians = new ArrayList<>();
        searchRefs = new ArrayList<>();
        searchNames = new ArrayList<>();
        names = new ArrayList<>();
        musicianManagers = new ArrayList<>();
        gridRefs = new ArrayList<>();
        userRefs = new ArrayList<>();
        invitesSent = new ArrayList<>();
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, ManageBandMembersActivity.class);
        intent.putExtra("EXTRA_BAND_ID", bandRef);
        startActivity(intent);
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

    @Override
    public void handleDatabaseResponse(Enum creationResult) {

    }

    @Override
    public void onSuccessfulImageDownload() {

    }
}
