package com.gangoffive.rig2gig;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class VenueConsoleFragment extends Fragment
{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        if(checkListing())
        {
            return inflater.inflate(R.layout.fragment_venue_console_ad_false, container, false);
        }
        else
        {
            return inflater.inflate(R.layout.fragment_venue_console_ad_true, container, false);
        }
    }

    private boolean checkListing()
    {
        return false;
    }
}
