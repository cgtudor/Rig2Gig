package com.gangoffive.rig2gig.utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.advert.management.DeleteInstrumentAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GenreSelectorActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private int height, width;
    private ArrayList<String> selectedGenres;
    private GridView gridView;
    private TextView searchHint;
    private Button ok;
    private ArrayList<String> selectableGenres = new ArrayList<>(Arrays.asList(Genres.getGenres()));
    private SearchView searchBar;
    private ListView listResults;
    private ArrayAdapter<String> resultsAdapter;
    private CharSequence query = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = (metrics.heightPixels) /100 * 80;
        width = (metrics.widthPixels) /100 * 80;
        getWindow().setLayout(width,height);
        String genresExtra = getIntent().getStringExtra("EXTRA_GENRES");
        String layoutType = getIntent().getStringExtra("EXTRA_LAYOUT_TYPE");
        if (layoutType.equals("Login"))
        {
            setContentView(R.layout.login_genre_selector);
        }
        else if (layoutType.equals("Not Login"))
        {
            setContentView(R.layout.genre_selector);
        }
        gridView = findViewById(R.id.gridView);
        searchHint = findViewById(R.id.searchHint);
        searchBar = findViewById(R.id.search_bar);
        ok = findViewById(R.id.ok);
        searchBar.setIconifiedByDefault(false);
        searchBar.setOnQueryTextListener(this);
        searchBar.setSubmitButtonEnabled(false);
        searchBar.setQueryHint("Enter genre");
        listResults = findViewById(R.id.list_results);
        if (genresExtra != null && !genresExtra.equals(""))
        {
            selectedGenres = new ArrayList<String>(Arrays.asList(genresExtra.split(",")));
            for (int i = 0; i < selectedGenres.size(); i++)
            {
                selectedGenres.set(i,selectedGenres.get(i).trim());
            }
            for (String genre : selectedGenres)
            {
                if(selectableGenres.contains(genre))
                {
                    selectableGenres.remove(genre);
                }
            }
        }
        else
        {
            searchHint.setVisibility(View.VISIBLE);
            selectedGenres = new ArrayList<String>();
        }
        setupGridView();
        initialiseSearchBar();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnSelectedGenres();
            }
        });
    }

    /**
     * Handle back button press
     */
    @Override
    public void onBackPressed()
    {
        returnSelectedGenres();
    }

    /**
     * Handle if top activity has changed
     * @param isTopResumedActivity true if currently the top activity
     */
    @Override
    public void onTopResumedActivityChanged (boolean isTopResumedActivity)
    {
        if(!isTopResumedActivity)
        {
            returnSelectedGenres();
        }
    }

    /**
     * Setup grid view for deletable genres
     */
    public void setupGridView()
    {
        DeleteInstrumentAdapter customAdapter = new DeleteInstrumentAdapter(selectedGenres, this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gridView.setAdapter(customAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        selectableGenres.add(selectedGenres.get(position).toString());
                        Collections.sort(selectableGenres);
                        selectedGenres.remove(position);
                        Collections.sort(selectedGenres);
                        if (selectedGenres.size() == 0)
                        {
                            searchHint.setVisibility(View.VISIBLE);
                        }
                        initialiseSearchBar();
                        setupGridView();
                    }
                });
            }
        });
    }

    /**
     * Initialises the search bar for position tab
     */
    public void initialiseSearchBar()
    {
        listResults.setTextFilterEnabled(true);
        listResults.setAdapter(resultsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                selectableGenres));
        listResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                selectedGenres.add(((TextView) v).getText().toString());
                Collections.sort(selectedGenres);
                selectableGenres.remove(((TextView) v).getText().toString());
                Collections.sort(selectableGenres);
                searchHint.setVisibility(View.INVISIBLE);
                initialiseSearchBar();
                setupGridView();
            }
        });
        if (searchBar != null)
        {
            query = searchBar.getQuery();
        }
        if (query != null)
        {
            listResults.setFilterText(query.toString());
            listResults.dispatchDisplayHint(View.INVISIBLE);
        }
    }

    /**
     * Not used
     * @param typedText typed text
     * @return false
     */
    @Override
    public boolean onQueryTextSubmit(String typedText)
    {
        return false;
    }

    /**
     * filter search bar list based on typed text
     * @param typedText text enterd in search bar
     * @return false
     */
    @Override
    public boolean onQueryTextChange(String typedText) {
        Filter filter = resultsAdapter.getFilter();
        filter.filter(typedText);
        return true;
    }

    /**
     * Return selected genres as string
     */
    public void returnSelectedGenres()
    {
        String genres = selectedGenres.toString();
        genres = genres.substring(1, genres.length() - 1);
        Intent result = new Intent();
        result.putExtra("EXTRA_SELECTED_GENRES", genres);
        setResult(RESULT_OK, result);
        finish();
    }
}
