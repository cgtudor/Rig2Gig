package com.gangoffive.rig2gig;

import android.os.Bundle;

import com.gangoffive.rig2gig.ui.TabbedView.IndexSectionsPagerAdapter;
import com.gangoffive.rig2gig.ui.TabbedView.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gangoffive.rig2gig.ui.TabbedView.SectionsPagerAdapter;

public class VenueAdvertIndexActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_view_venues,
            R.layout.fragment_saved_venues};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_advert_index);
        tabTitles = new int[]{R.string.all, R.string.favourites};
        IndexSectionsPagerAdapter sectionsPagerAdapter = new IndexSectionsPagerAdapter(this, getSupportFragmentManager(), tabTitles, fragments);
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