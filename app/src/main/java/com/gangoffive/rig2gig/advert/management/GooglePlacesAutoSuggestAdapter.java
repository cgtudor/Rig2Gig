package com.gangoffive.rig2gig.advert.management;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class GooglePlacesAutoSuggestAdapter extends ArrayAdapter implements Filterable
{
    private ArrayList<String> results;
    private Context context;
    private int resource;
    private GooglePlacesAPIReader googlePlacesAPIReader = new GooglePlacesAPIReader();

    public GooglePlacesAutoSuggestAdapter(Context context, int resourceId)
    {
        super(context, resourceId);
        this.context = context;
        this.resource = resourceId;
    }

    public GooglePlacesAutoSuggestAdapter(@NonNull Context context, int resource, int textViewResourceId)
    {
        super(context, resource, textViewResourceId);
    }

    @Override
    public int getCount()
    {
        return results.size();
    }

    @Override
    public String getItem(int position)
    {
        return results.get(position);
    }

    /**
     * Filter will populate Result in AutoCompleteTextView.
     * @return
     */
    @Override
    public Filter getFilter()
    {
        Filter filter = new Filter()
        {
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
