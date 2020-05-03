package com.gangoffive.rig2gig.advert.management;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * This class is used to suggest addresses based upon user input.
 */
public class GooglePlacesAutoSuggestAdapter extends ArrayAdapter implements Filterable
{
    private ArrayList<String> results;
    private Context context;
    private int resource;
    private GooglePlacesAPIReader googlePlacesAPIReader = new GooglePlacesAPIReader();

    /**
     * Constructor used to set up  context and resource variables.
     * @param context References the passed in environment.
     * @param resourceId References the passed in resource.
     */
    public GooglePlacesAutoSuggestAdapter(Context context, int resourceId)
    {
        super(context, resourceId);
        this.context = context;
        this.resource = resourceId;
    }

    /**
     * Constructor used to set up context, resource and textViewResourceId variables.
     * @param context References the passed in environment.
     * @param resource References the passed in resource.
     * @param textViewResourceId References the passed in Text View.
     */
    public GooglePlacesAutoSuggestAdapter(@NonNull Context context, int resource, int textViewResourceId)
    {
        super(context, resource, textViewResourceId);
    }

    /**
     * This method is used to get the count of the results ArrayList.
     * @return Returns the size of the results ArrayList as an int.
     */
    @Override
    public int getCount()
    {
        return results.size();
    }

    /**
     * This method is used to get an address item from the results ArrayList.
     * @param position References the position of the selected item in the results ArrayList.
     * @return Returns a selected address as a String.
     */
    @Override
    public String getItem(int position)
    {
        return results.get(position);
    }

    /**
     * Filter will populate Result in AutoCompleteTextView.
     * @return Returns the created filter.
     */
    @Override
    public Filter getFilter()
    {
        Filter filter = new Filter()
        {
            /**
             * This method is sued to perform the filtering of optional addresses.
             * @param constraint Represents the characters to match.
             * @return Returns the filter results.
             */
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                FilterResults filterResults = new FilterResults();

                if(constraint != null)
                {
                    results = googlePlacesAPIReader.autoComplete(constraint.toString());

                    filterResults.values = results;
                    filterResults.count = results.size();
                }

                return filterResults;
            }

            /**
             * This method is used to publish the results of the filtered addresses.
             * @param constraint Represents the characters to match.
             * @param results Represents the results passed in from the filtering method.
             */
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                if(results != null && results.count > 0)
                {
                    notifyDataSetChanged();
                }
                else
                {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }
}
