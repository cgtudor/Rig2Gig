package com.gangoffive.rig2gig.advert.index;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.ui.TabbedView.IndexSectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class PerformerAdvertIndexActivity extends AppCompatActivity {

    private int[] tabTitles;
    private int[] fragments = {R.layout.fragment_view_performers,
            R.layout.fragment_saved_performers};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performer_advert_index);
        tabTitles = new int[]{R.string.all, R.string.favourites};
        Fragment[] frags = new Fragment[2];
        frags[0] = new ViewPerformersFragment();
        frags[1] = new SavedPerformersFragment();
        IndexSectionsPagerAdapter sectionsPagerAdapter = new IndexSectionsPagerAdapter(this, getSupportFragmentManager(), tabTitles, fragments, frags);
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Performer Adverts");
        /*Setting the support action bar to the newly created toolbar*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshActivity()
    {
        finish();
        startActivity(getIntent());
    }
}