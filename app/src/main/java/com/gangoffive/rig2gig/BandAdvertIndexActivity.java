package com.gangoffive.rig2gig;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.gangoffive.rig2gig.ui.TabbedView.BandSectionsPagerAdapter;
import com.gangoffive.rig2gig.ui.TabbedView.VenueSectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class BandAdvertIndexActivity extends AppCompatActivity {

    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_view_bands,
            R.layout.fragment_saved_bands};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_advert_index);
        tabTitles = new int[]{R.string.all, R.string.favourites};
        BandSectionsPagerAdapter sectionsPagerAdapter = new BandSectionsPagerAdapter(this, getSupportFragmentManager(), tabTitles, fragments);
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