package com.gangoffive.rig2gig.advert.index;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.ui.TabbedView.IndexSectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class VenueAdvertIndexActivity extends AppCompatActivity {

    private final int LAUNCH_REFINED_SEARCH = 749;

    private String currentUserType;

    private Toolbar toolbar;
    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_view_venues,
            R.layout.fragment_saved_venues};

    private String sortBy, minRating, maxDistance;
    private ArrayList<String> venueTypes;

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
        bundle.putString("EXTRA_SORT_BY", sortBy);
        bundle.putString("EXTRA_MIN_RATING", minRating);
        bundle.putString("EXTRA_MAX_DISTANCE", maxDistance);
        bundle.putStringArrayList("EXTRA_VENUE_TYPES", venueTypes);

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

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Venue Adverts");
        /*Setting the support action bar to the newly created toolbar*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.index_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // handle arrow click here
        if (id == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        } else if (id == R.id.refineButton) {
            Intent intent =  new Intent(VenueAdvertIndexActivity.this, VenueRefineSearchActivity.class);
            if(sortBy != null) {
                intent.putExtra("EXTRA_SORT_BY", sortBy);
            }
            if(minRating != null) {
                intent.putExtra("EXTRA_MIN_RATING", minRating);
            }
            if(maxDistance != null) {
                intent.putExtra("EXTRA_MAX_DISTANCE", maxDistance);
            }
            if(venueTypes != null) {
                intent.putExtra("EXTRA_VENUE_TYPES", venueTypes);
            }
            startActivityForResult(intent, LAUNCH_REFINED_SEARCH);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_REFINED_SEARCH) {
            if(resultCode == VenueRefineSearchActivity.RESULT_OK){
                sortBy = data.getStringExtra("EXTRA_SORT_BY");
                minRating = data.getStringExtra("EXTRA_MIN_RATING");
                maxDistance = data.getStringExtra("EXTRA_MAX_DISTANCE");
                venueTypes = data.getStringArrayListExtra("EXTRA_VENUE_TYPES");
            }
            if (resultCode == VenueRefineSearchActivity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}