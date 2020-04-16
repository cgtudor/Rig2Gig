package com.gangoffive.rig2gig.band.management;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.advert.management.DeleteInstrumentAdapter;
import com.gangoffive.rig2gig.utils.Genres;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BandGenreFragment extends Fragment implements SearchView.OnQueryTextListener {


    private GridView gridView;
    private ArrayList<String> genres = new ArrayList<>(Arrays.asList(Genres.getGenres()));
    private List bandGenre = new ArrayList();
    private SearchView searchBar;
    private ListView listResults;
    private ArrayAdapter<String> resultsAdapter;
    private CharSequence query = null;

    private TextView searchHint;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_band_genre, container, false);

        searchHint = v.findViewById(R.id.searchHint);
        searchBar = v.findViewById(R.id.search_bar);
        listResults = v.findViewById(R.id.list_results);
        gridView = v.findViewById(R.id.gridView);
        initialiseSearchBar();
        return v;
    }

    /**
     * Initialises the search bar for position tab
     */
    public void initialiseSearchBar()
    {
        if(listResults != null)
        {
            listResults.setTextFilterEnabled(true);
            listResults.setAdapter(resultsAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1,
                    genres));
            listResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    bandGenre.add(((TextView) v).getText().toString());
                    Collections.sort(bandGenre);
                    genres.remove(((TextView) v).getText().toString());
                    Collections.sort(genres);
                    searchHint.setVisibility(View.INVISIBLE);
                    initialiseSearchBar();
                    setupGridView();
                }
            });
        }
            searchBar.setIconifiedByDefault(false);
            searchBar.setOnQueryTextListener(this);
            searchBar.setSubmitButtonEnabled(false);
            searchBar.setQueryHint("Enter band genres");
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

    /**
     * set up grid view containing all positions selected by user
     */
    public void setupGridView()
    {
        DeleteInstrumentAdapter customAdapter = new DeleteInstrumentAdapter(bandGenre, getActivity());
        gridView.setAdapter(customAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                genres.add(bandGenre.get(position).toString());
                Collections.sort(genres);
                bandGenre.remove(position);
                Collections.sort(bandGenre);
                if (bandGenre.size() == 0)
                {
                    searchHint.setVisibility(View.VISIBLE);
                }
                initialiseSearchBar();
                setupGridView();
            }
        });
        validateButton();
    }

    /**
     * validate current data and grey out create button if necessary
     */
    public void validateButton()
    {
//        if (createListing != null && description!= null
//                &&  (bandPositions.size() == 0
//                || description.getText().toString().trim().length() == 0)) {
//            createListing.setBackgroundColor(Color.parseColor("#B2BEB5"));
//            createListing.setTextColor(Color.parseColor("#4D4D4E"));
//        }
//        else if (createListing != null && description!= null
//                && description.getText().toString().trim().length() > 0
//                && bandPositions.size() > 0)
//        {
//            createListing.setBackgroundColor(Color.parseColor("#008577"));
//            createListing.setTextColor(Color.parseColor("#FFFFFF"));
//        }
    }
}
