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

public class VenueTypeSelectorActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private int height, width;
    private ArrayList<String> selectedTypes;
    private GridView gridView;
    private TextView searchHint;
    private Button ok;
    private ArrayList<String> selectableTypes = new ArrayList<>(Arrays.asList(VenueTypes.getTypes()));
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
        String typeExtra = getIntent().getStringExtra("EXTRA_TYPES");
        setContentView(R.layout.genre_selector);
        gridView = findViewById(R.id.gridView);
        gridView.setNumColumns(2);
        searchHint = findViewById(R.id.searchHint);
        searchBar = findViewById(R.id.search_bar);
        ok = findViewById(R.id.ok);
        searchBar.setIconifiedByDefault(false);
        searchBar.setOnQueryTextListener(this);
        searchBar.setSubmitButtonEnabled(false);
        searchBar.setQueryHint("Enter venue type");
        listResults = findViewById(R.id.list_results);
        if (typeExtra != null && !typeExtra.equals(""))
        {
            selectedTypes = new ArrayList<String>(Arrays.asList(typeExtra.split(",")));
            for (int i = 0; i < selectedTypes.size(); i++)
            {
                selectedTypes.set(i,selectedTypes.get(i).trim());
            }
            for (String type : selectedTypes)
            {
                if(selectableTypes.contains(type))
                {
                    selectableTypes.remove(type);
                }
            }
        }
        else
        {
            searchHint.setVisibility(View.VISIBLE);
            selectedTypes = new ArrayList<String>();
        }
        setupGridView();
        initialiseSearchBar();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnselectedTypes();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        returnselectedTypes();
    }

    @Override
    public void onTopResumedActivityChanged (boolean isTopResumedActivity)
    {
        if(!isTopResumedActivity)
        {
            returnselectedTypes();
        }
    }

    public void setupGridView()
    {
        DeleteInstrumentAdapter customAdapter = new DeleteInstrumentAdapter(selectedTypes, this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gridView.setAdapter(customAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View v,
                                            int position, long id) {
                        selectableTypes.add(selectedTypes.get(position).toString());
                        Collections.sort(selectableTypes);
                        selectedTypes.remove(position);
                        Collections.sort(selectedTypes);
                        if (selectedTypes.size() == 0)
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
     * Initialises the search bar
     */
    public void initialiseSearchBar()
    {
        listResults.setTextFilterEnabled(true);
        listResults.setAdapter(resultsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                selectableTypes));
        listResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                selectedTypes.add(((TextView) v).getText().toString());
                Collections.sort(selectedTypes);
                selectableTypes.remove(((TextView) v).getText().toString());
                Collections.sort(selectableTypes);
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

    public void returnselectedTypes()
    {
        String types = selectedTypes.toString();
        types = types.substring(1, types.length() - 1);
        Intent result = new Intent();
        result.putExtra("EXTRA_SELECTED_TYPES", types);
        setResult(RESULT_OK, result);
        finish();
    }
}