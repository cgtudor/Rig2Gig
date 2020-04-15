package com.gangoffive.rig2gig.band.management;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gangoffive.rig2gig.advert.management.CreateAdvertisement;
import com.gangoffive.rig2gig.firebase.ListingManager;
import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.profile.MusicianProfileActivity;

import java.util.Map;

public class BandMemberDetails extends AppCompatActivity implements CreateAdvertisement {

    private int height, width;
    private Button ok, profile;
    private TextView name, userName, location, phone, email, rating;
    private String musicianRef, userRef;
    private ListingManager userManager, musicianManager;
    private Map<String, Object> musician, user;
    private boolean musicianDownloaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_member_details);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = (metrics.heightPixels) /100 * 80;
        width = (metrics.widthPixels) /100 * 80;
        getWindow().setLayout(width,height);
        Intent intent = getIntent();
        musicianRef = intent.getStringExtra("EXTRA_MUSICIAN_REF");
        musicianDownloaded = false;
        musicianManager = new ListingManager(musicianRef,"Musician","profileEdit");
        musicianManager.getUserInfo(this);
        ok = findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Jandle success from database
     * @param data map of band member data
     */
    @Override
    public void onSuccessFromDatabase(Map<String, Object> data) {
        if (data != null)
        {
            if (!musicianDownloaded)
            {
                musicianDownloaded = true;
                musician = data;
                userRef = musician.get("user-ref").toString();
                userManager = new ListingManager(userRef,"User","profileEdit");
                userManager.getUserInfo(this);
            }
            else
            {
                user = data;
                setViewReferences();
            }
        }
    }

    /**
     * set view references
     */
    @Override
    public void setViewReferences()
    {
        name = findViewById(R.id.name);
        userName = findViewById(R.id.userName);
        location = findViewById(R.id.location);
        phone = findViewById(R.id.phone);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringMember();
            }
        });
        email = findViewById(R.id.email);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        emailMember();
                    }
                });
            }
        });
        rating = findViewById(R.id.rating);
        profile = findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BandMemberDetails.this, MusicianProfileActivity.class);
                intent.putExtra("EXTRA_MUSICIAN_ID", musicianRef);
                startActivity(intent);
                finish();
            }
        });
        populateInitialFields();
    }

    /**
     * populate initial fields with band member data
     */
    @Override
    public void populateInitialFields() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                name.setText(musician.get("name").toString());
                userName.setText(user.get("username").toString());
                location.setText(musician.get("location").toString());
                phone.setText(user.get("phone-number").toString());
                phone.setPaintFlags(phone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                email.setText(user.get("email-address").toString());
                email.setPaintFlags(email.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                rating.setText(musician.get("rating").toString());
            }
        });
    }

    /**
     * Start activity to contact band member by phone
     */
    public void ringMember()
    {
        if (!phone.getText().equals(""))
        {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone.getText()));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    /**
     * Start activity to contact band member by email
     */
    public void emailMember()
    {
        if (!email.getText().equals(""))
        {
            String[] emailAddress = {email.getText().toString()};
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
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
}
