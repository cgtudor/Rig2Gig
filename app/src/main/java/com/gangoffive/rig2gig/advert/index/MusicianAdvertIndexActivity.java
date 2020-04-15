package com.gangoffive.rig2gig.advert.index;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.ui.TabbedView.IndexSectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MusicianAdvertIndexActivity extends AppCompatActivity {

    private final int LAUNCH_REFINED_SEARCH = 1248;

    private String currentBandId;

    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_view_musicians,
            R.layout.fragment_saved_musicians};

    private TextView fader;

    private String sortBy, minRating, maxDistance;
    private ArrayList<String> positions, genres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musician_advert_index);

        tabTitles = new int[]{R.string.all, R.string.favourites};

        Fragment[] frags = new Fragment[2];

        Intent receivedIntent = getIntent();

        sortBy = receivedIntent.getStringExtra("EXTRA_SORT_BY");
        minRating = receivedIntent.getStringExtra("EXTRA_MIN_RATING");
        maxDistance = receivedIntent.getStringExtra("EXTRA_MAX_DISTANCE");
        positions = receivedIntent.getStringArrayListExtra("EXTRA_MUSICIAN_POSITIONS");
        genres = receivedIntent.getStringArrayListExtra("EXTRA_MUSICIAN_GENRES");

        Bundle bundle = new Bundle();

        Intent intent = getIntent();
        bundle.putString("EXTRA_SORT_BY", sortBy);
        bundle.putString("EXTRA_MIN_RATING", minRating);
        bundle.putString("EXTRA_MAX_DISTANCE", maxDistance);
        bundle.putStringArrayList("EXTRA_MUSICIAN_POSITIONS", positions);
        bundle.putStringArrayList("EXTRA_MUSICIAN_GENRES", genres);

        currentBandId = intent.getStringExtra("CURRENT_BAND_ID");
        bundle.putString("CURRENT_BAND_ID", currentBandId);

        Fragment viewAdvertFragment = new ViewMusiciansFragment();
        viewAdvertFragment.setArguments(bundle);
        frags[0] = viewAdvertFragment;

        Fragment savedAdvertFragment = new SavedMusiciansFragment();
        savedAdvertFragment.setArguments(bundle);
        frags[1] = savedAdvertFragment;

        IndexSectionsPagerAdapter sectionsPagerAdapter = new IndexSectionsPagerAdapter(this, getSupportFragmentManager(), tabTitles, fragments, frags);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Musician Adverts");
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
            fader = findViewById(R.id.fader);
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.darkerMain));
            fader.setVisibility(View.VISIBLE);
            Intent intent =  new Intent(MusicianAdvertIndexActivity.this, MusicianRefineSearchActivity.class);
            if(sortBy != null) {
                intent.putExtra("EXTRA_SORT_BY", sortBy);
            }
            if(minRating != null) {
                intent.putExtra("EXTRA_MIN_RATING", minRating);
            }
            if(maxDistance != null) {
                intent.putExtra("EXTRA_MAX_DISTANCE", maxDistance);
            }
            if(positions != null) {
                intent.putExtra("EXTRA_MUSICIAN_POSITIONS", positions);
            }
            if(genres != null) {
                intent.putExtra("EXTRA_MUSICIAN_GENRES", genres);
            }
            startActivityForResult(intent, LAUNCH_REFINED_SEARCH);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_REFINED_SEARCH) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
            fader.setVisibility(View.GONE);
            if (resultCode == VenueRefineSearchActivity.RESULT_OK) {
                sortBy = data.getStringExtra("EXTRA_SORT_BY");
                minRating = data.getStringExtra("EXTRA_MIN_RATING");
                maxDistance = data.getStringExtra("EXTRA_MAX_DISTANCE");
                positions = data.getStringArrayListExtra("EXTRA_MUSICIAN_POSITIONS");
                genres = data.getStringArrayListExtra("EXTRA_MUSICIAN_GENRES");
                refreshActivity();
            }
            if (resultCode == VenueRefineSearchActivity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void refreshActivity()
    {
        Intent refreshIntent = new Intent(MusicianAdvertIndexActivity.this, MusicianAdvertIndexActivity.class);
        refreshIntent.putExtra("EXTRA_SORT_BY", sortBy);
        refreshIntent.putExtra("EXTRA_MIN_RATING", minRating);
        refreshIntent.putExtra("EXTRA_MAX_DISTANCE", maxDistance);
        refreshIntent.putStringArrayListExtra("EXTRA_MUSICIAN_POSITIONS", positions);
        refreshIntent.putStringArrayListExtra("EXTRA_MUSICIAN_GENRES", genres);
        refreshIntent.putExtra("CURRENT_BAND_ID", currentBandId);
        finish();
        startActivity(refreshIntent);
    }
}