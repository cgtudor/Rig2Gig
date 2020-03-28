package com.gangoffive.rig2gig;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.gangoffive.rig2gig.ui.TabbedView.IndexSectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class BandAdvertIndexActivity extends AppCompatActivity {

    private String currentUserType;

    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_view_bands,
            R.layout.fragment_saved_bands};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_advert_index);
        tabTitles = new int[]{R.string.all, R.string.favourites};
        Fragment[] frags = new Fragment[2];
        frags[0] = new ViewBandsFragment();
        frags[1] = new SavedBandsFragment();
        IndexSectionsPagerAdapter sectionsPagerAdapter = new IndexSectionsPagerAdapter(this, getSupportFragmentManager(), tabTitles, fragments, frags);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }
}