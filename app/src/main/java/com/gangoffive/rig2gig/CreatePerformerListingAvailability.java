package com.gangoffive.rig2gig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class CreatePerformerListingAvailability extends Fragment
{
    private TextView genres, charge, distance;
    private Button createListing, back;
    private String bandRef;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference bandImageRef;
    private HashMap<String, Object> listing;
    private Bitmap image;
    private String invalidFields;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        listing = (HashMap)getArguments().getSerializable("listing");
        bandRef = listing.get("bandRef").toString();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        bandImageRef = storageRef.child("/images/bands/" + bandRef + "/profile.jpg");
        image = BitmapFactory.decodeFile(System.getProperty("java.io.tmpdir") + bandRef + ".tmp");
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_performer_listing_availability, container, false);
        setInputReferences(view);
        populateFields();

        createListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //need to check if listing exists, perhaps by a start and end date
                bandDataMap();
                Boolean valid = validateDataMap();
                if (valid)
                {
                    postDataToDatabase();
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
        genres = view.findViewById(R.id.genres);
        charge = view.findViewById(R.id.charge);
        distance = view.findViewById(R.id.distance);
        createListing = view.findViewById(R.id.createListing);
        back = view.findViewById(R.id.back);
    }

    public void populateFields()
    {
        genres.setText(listing.get("genres").toString());
        charge.setText(String.valueOf(listing.get("charge")));
        distance.setText(String.valueOf(listing.get("distance")));
    }

    public void bandDataMap()
    {
        listing.put("genres",genres.getText().toString());
        listing.put("charge",(charge.getText()).toString());
        listing.put("distance",(distance.getText()).toString());
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

    public byte[] BitMapToByteArray()
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public void backToPreviousListingFragment(HashMap<String, Object> listing)
    {
        CreatePerformerListingDescription fragment = new CreatePerformerListingDescription();
        Bundle bandInfo = new Bundle();
        bandInfo.putSerializable("listing",listing);
        fragment.setArguments(bandInfo);
        FragmentTransaction fTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fTransaction.replace(R.id.fragment_container,fragment);
        fTransaction.commit();
    }

    public void postDataToDatabase()
    {
        Calendar calendar = Calendar.getInstance();
        listing.put("date live",calendar);
        calendar.add(Calendar.DATE, 30);
        listing.put("date expires", calendar);
        db.collection("performer-listings")
                .add(listing)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        StorageReference listingImage = storageRef.child("/images/performance-listings/" + documentReference.getId() + ".jpg");

                        UploadTask uploadTask = listingImage.putBytes(BitMapToByteArray());
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                // ...
                            }
                        });

                        //need to pass to view listing activity to be called "PerformanceListingDetailsActivity"
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyBandFragment()).commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}
