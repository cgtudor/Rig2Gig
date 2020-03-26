package com.gangoffive.rig2gig;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gangoffive.rig2gig.ui.TabbedView.MusicianPagerAdapter;
import com.gangoffive.rig2gig.ui.TabbedView.VenuePagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class TabbedMusicianActivity extends AppCompatActivity {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;

    Button test, back;

    private static final String TAG = "======================";

    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_credential,
            R.layout.fragment_create_musician};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        tabTitles = new int[]{R.string.personalInformation, R.string.musicianInformation};

        MusicianPagerAdapter musicianPagerAdapter = new MusicianPagerAdapter
                (this, getSupportFragmentManager(), tabTitles, fragments);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(musicianPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        test = findViewById(R.id.submitBtn);
    }

    public void confirmOnClick(View view)
    {
        CredentialFragment.btn.performClick();
        System.out.println("clicked");
    }

    public void cancelOnClick(View view)
    {
        onBackPressed();
    }

    public void onBackPressed() {
        Intent backToMain = new Intent(this,
                LoginActivity.class);
        startActivity(backToMain);
    }
}