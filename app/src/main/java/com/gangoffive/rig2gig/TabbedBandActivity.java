package com.gangoffive.rig2gig;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.gangoffive.rig2gig.ui.TabbedView.BandPagerAdapter;
import com.gangoffive.rig2gig.ui.TabbedView.MusicianPagerAdapter;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class TabbedBandActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;

    Button test, back;

    EditText name, location, distance, genre, email, number, invis;
    ImageView image;

    private static final String TAG = "======================";
    static String musicianID;

    private int[] tabTitles;
    private int[] fragments = {R.layout.activity_create_band,
            R.layout.fragment_band_image};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_layout);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        musicianID = getIntent().getStringExtra("EXTRA_MUSICIAN_ID");

        tabTitles = new int[]{R.string.bandInformation, R.string.bandImage};

        test = findViewById(R.id.submitBtn);
        back = findViewById(R.id.cancel);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                TabbedBandActivity.super.onBackPressed();
            }
        });

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Create A Band");
        /*Setting the support action bar to the newly created toolbar*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BandPagerAdapter bandPagerAdapter = new BandPagerAdapter
                (this, getSupportFragmentManager(), tabTitles, fragments);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(bandPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }


    public void confirmOnClick(View view)
    {
        name = findViewById(R.id.BandName);
        String bandName = name.getText().toString();
        if (TextUtils.isEmpty(bandName)) {
            name.setError("Band name is required!");
            return;
        }
        location = findViewById(R.id.location3);
        String bandLocation = location.getText().toString();
        if (TextUtils.isEmpty(bandLocation)) {
            location.setError("Band location is required!");
            return;
        }
        distance = findViewById(R.id.bandDistance);
        String bandDistance = distance.getText().toString();
        if (TextUtils.isEmpty(bandDistance)) {
            distance.setError("Distance to travel is required!");
            return;
        }
        genre = findViewById(R.id.bandGenres);
        String bandGenre = genre.getText().toString();
        if (TextUtils.isEmpty(bandGenre)) {
            genre.setError("Distance to travel is required!");
            return;
        }
        email = findViewById(R.id.bandEmail);
        String bandEmail = email.getText().toString();
        if (TextUtils.isEmpty(bandEmail)) {
            email.setError("Band email is required!");
            return;
        }
        number = findViewById(R.id.bandPhoneNumber);
        String bandPhonenumber = number.getText().toString();
        if (TextUtils.isEmpty(bandPhonenumber)) {
            number.setError("Band email is required!");
            return;
        }

        image = findViewById(R.id.imageView);
        if (image.getDrawable() == null)
        {
            Toast.makeText(getApplicationContext(),"Please choose and image!", Toast.LENGTH_SHORT).show();
            return;
        }

        CreateBandFragment.btn.performClick();
        System.out.println("clicked");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}