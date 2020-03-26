package com.gangoffive.rig2gig;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.gangoffive.rig2gig.ui.TabbedView.BandPagerAdapter;
import com.gangoffive.rig2gig.ui.TabbedView.MusicianPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class TabbedBandActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;

    Button test, back;

    private static final String TAG = "======================";
    static String musicianID;

    private int[] tabTitles;
    private int[] fragments = {R.layout.activity_create_band,
            R.layout.fragment_band_image};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        musicianID = getIntent().getStringExtra("EXTRA_MUSICIAN_ID");

        tabTitles = new int[]{R.string.bandInformation, R.string.bandImage};

        BandPagerAdapter bandPagerAdapter = new BandPagerAdapter
                (this, getSupportFragmentManager(), tabTitles, fragments);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(bandPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        test = findViewById(R.id.submitBtn);
        back = findViewById(R.id.cancel);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                TabbedBandActivity.super.onBackPressed();
            }
        });
    }

    public void confirmOnClick(View view)
    {
        CreateBandFragment.btn.performClick();
        System.out.println("clicked");
    }
}