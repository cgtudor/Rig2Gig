package com.gangoffive.rig2gig;

import android.content.Intent;
import android.os.Bundle;

import com.gangoffive.rig2gig.ui.TabbedView.IndexSectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

public class VenueAdvertIndexActivity extends AppCompatActivity {

    private String currentUserType;

    private Toolbar toolbar;
    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_view_venues,
            R.layout.fragment_saved_venues};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_advert_index);





        tabTitles = new int[]{R.string.all, R.string.favourites};

        Fragment[] frags = new Fragment[2];

        Bundle bundle = new Bundle();

        Intent intent = getIntent();
        currentUserType = intent.getStringExtra("CURRENT_USER_TYPE");
        bundle.putString("CURRENT_USER_TYPE", currentUserType);

        Bundle extras = intent.getExtras();
        if(extras != null) {
            if(extras.containsKey("CURRENT_BAND_ID")) {
                bundle.putString("CURRENT_BAND_ID", intent.getStringExtra("CURRENT_BAND_ID"));
            }
        }

        Fragment viewAdvertFragment = new ViewVenuesFragment();
        viewAdvertFragment.setArguments(bundle);
        frags[0] = viewAdvertFragment;

        Fragment savedAdvertFragment = new SavedVenuesFragment();
        savedAdvertFragment.setArguments(bundle);
        frags[1] = savedAdvertFragment;

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