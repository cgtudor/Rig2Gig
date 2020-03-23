package com.gangoffive.rig2gig;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filterable;

import androidx.annotation.NonNull;

public class GooglePlacesAutoSuggestAdapter extends ArrayAdapter implements Filterable
{


    public GooglePlacesAutoSuggestAdapter(@NonNull Context context, int resource, int textViewResourceId)
    {
        super(context, resource, textViewResourceId);
    }
}
