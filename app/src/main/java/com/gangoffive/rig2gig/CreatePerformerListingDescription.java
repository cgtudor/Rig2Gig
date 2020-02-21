package com.gangoffive.rig2gig;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;

public class CreatePerformerListingDescription extends Fragment
{
    private TextView description;
    private Button back, next;
    private String bandRef;
    private HashMap<String, Object> listing;
    private String invalidFields;

    /**
     * Initialise variables from bundled arguments
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        listing = (HashMap)getArguments().getSerializable("listing");
        bandRef = listing.get("bandRef").toString();
    }


    /**
     * Setup references, populate fields and set on click listener for next and back button
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return inflated view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_performer_listing_description, container, false);
        setInputReferences(view);
        populateFields();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                bandDataMap();
                Boolean valid = validateDataMap();
                if (valid)
                {
                    sendToNextListingFragment();
                } else
                {
                    Toast.makeText(getActivity(), "Listing not created.  The following fields " +
                            "are incomplete:\n" + invalidFields, Toast.LENGTH_LONG).show();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                bandDataMap();
                backToPreviousListingFragment();
            }
        });
        return view;
    }

    /**
     * set references to text views and buttons
     * @param view
     */
    public void setInputReferences(View view)
    {
        description = view.findViewById(R.id.description);
        next = view.findViewById(R.id.next);
        back = view.findViewById(R.id.back);
    }

    /**
     * populate text views
     */
    public void populateFields()
    {
        description.setText(String.valueOf(listing.get("description")));
    }

    /**
     * populate listing map with new data
     */
    public void bandDataMap()
    {
        listing.put("description",description.getText().toString());
    }

    /**
     * validate data in listing map
     * @return true if valid
     */
    public boolean validateDataMap()
    {
        Boolean valid = true;
        invalidFields = "";
        for (Map.Entry element : listing.entrySet())
        {
            String key = (String)element.getKey();
            String val = (String)element.getValue();
            if(val == null || val.trim().isEmpty())
            {
                valid = false;
                invalidFields += (key + "\n");
            }
        }
        return valid;
    }

    /**
     * perform transaction to next fragment and pass relevant data in bundle
     */
    public void sendToNextListingFragment()
    {
        CreatePerformerListingAvailability fragment = new CreatePerformerListingAvailability();
        Bundle bandInfo = new Bundle();
        bandInfo.putSerializable("listing",listing);
        fragment.setArguments(bandInfo);
        FragmentTransaction fTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fTransaction.replace(R.id.fragment_container,fragment);
        fTransaction.addToBackStack("CreatePerformerListingDescription");
        fTransaction.commit();
    }

    /**
     * perform transaction to previous fragment and pass relevant data in bundle
     */
    public void backToPreviousListingFragment()
    {
        CreatePerformerListing fragment = new CreatePerformerListing();
        Bundle bandInfo = new Bundle();
        bandInfo.putSerializable("listing",listing);
        fragment.setArguments(bandInfo);
        FragmentTransaction fTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fTransaction.replace(R.id.fragment_container,fragment);
        fTransaction.commit();
    }

}
