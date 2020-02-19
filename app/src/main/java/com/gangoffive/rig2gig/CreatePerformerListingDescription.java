package com.gangoffive.rig2gig;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class CreatePerformerListingDescription extends Fragment
{
    private TextView description;
    private Button back, next;
    private String bandRef;
    private HashMap<String, Object> listing;
    private String invalidFields;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        listing = (HashMap)getArguments().getSerializable("listing");
        bandRef = listing.get("bandRef").toString();
    }



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
                    sendToNextListingFragment(listing);
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
                backToPreviousListingFragment(listing);
            }
        });
        return view;
    }

    public void setInputReferences(View view)
    {
        description = view.findViewById(R.id.description);
        next = view.findViewById(R.id.next);
        back = view.findViewById(R.id.back);
    }

    public void populateFields()
    {
        description.setText(String.valueOf(listing.get("description")));
    }

    public void bandDataMap()
    {
        listing.put("description",description.getText().toString());
    }

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

    public void sendToNextListingFragment(HashMap<String, Object> listing)
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

    public void backToPreviousListingFragment(HashMap<String, Object> listing)
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
