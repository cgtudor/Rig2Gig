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

public class PositionSelectorActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private int height, width;
    private ArrayList<String> selectedPositions;
    private GridView gridView;
    private TextView searchHint;
    private Button ok;
    private ArrayList<String> selectablePositions = new ArrayList<>(Arrays.asList(Positions.getPositions()));
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
        String positionsExtra = getIntent().getStringExtra("EXTRA_POSITIONS");
        setContentView(R.layout.genre_selector);
        gridView = findViewById(R.id.gridView);
        gridView.setNumColumns(2);
        searchHint = findViewById(R.id.searchHint);
        searchBar = findViewById(R.id.search_bar);
        ok = findViewById(R.id.ok);
        searchBar.setIconifiedByDefault(false);
        searchBar.setOnQueryTextListener(this);
        searchBar.setSubmitButtonEnabled(false);
        searchBar.setQueryHint("Enter position");
        listResults = findViewById(R.id.list_results);
        if (positionsExtra != null && !positionsExtra.equals(""))
        {
            selectedPositions = new ArrayList<String>(Arrays.asList(positionsExtra.split(",")));
            for (int i = 0; i < selectedPositions.size(); i++)
            {
                selectedPositions.set(i,selectedPositions.get(i).trim());
            }
            for (String position : selectedPositions)
            {
                if(selectablePositions.contains(position))
                {
                    selectablePositions.remove(position);
                }
            }
        }
        else
        {
            searchHint.setVisibility(View.VISIBLE);
            selectedPositions = new ArrayList<String>();
        }
        setupGridView();
        initialiseSearchBar();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnSelectedPositions();
            }
        });
    }

    /**
     * Handle back button press
     */
    @Override
    public void onBackPressed()
    {
        returnSelectedPositions();
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
            returnSelectedPositions();
        }
    }

    /**
     * Setup grid view for deletable positions
     */
    public void setupGridView()
    {
        DeleteInstrumentAdapter customAdapter = new DeleteInstrumentAdapter(selectedPositions, this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gridView.setAdapter(customAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        selectablePositions.add(selectedPositions.get(position).toString());
                        Collections.sort(selectablePositions);
                        selectedPositions.remove(position);
                        Collections.sort(selectedPositions);
                        if (selectedPositions.size() == 0)
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
                selectablePositions));
        listResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                selectedPositions.add(((TextView) v).getText().toString());
                Collections.sort(selectedPositions);
                selectablePositions.remove(((TextView) v).getText().toString());
                Collections.sort(selectablePositions);
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
     * Return selected positions as string
     */
    public void returnSelectedPositions()
    {
        String positions = selectedPositions.toString();
        positions = positions.substring(1, positions.length() - 1);
        Intent result = new Intent();
        result.putExtra("EXTRA_SELECTED_POSITIONS", positions);
        setResult(RESULT_OK, result);
        finish();
    }
}