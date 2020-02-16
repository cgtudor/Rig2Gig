package com.gangoffive.rig2gig;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class CreatePerformerListing extends Fragment {


    private TextView name, location, genres, charge, distance, description;
    private Button createListing;
    private String bandRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bandRef = "TvuDGJwqX13vJ6LWZYB2";
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("bands").document(bandRef);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map <String, Object> band = document.getData();
                        name.setText((String)band.get("name"));
                        location.setText((String)band.get("location"));
                        String genreString = band.get("genres").toString();
                        genres.setText(genreString.substring(1,genreString.length()-1));
                        charge.setText(String.valueOf(band.get("chargePerHour")));
                        distance.setText(String.valueOf(band.get("travelDistance")));
                        description.setText((String)band.get("description"));
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_create_performer_listing, container, false);
        name = view.findViewById(R.id.name);
        location = view.findViewById(R.id.location);
        genres = view.findViewById(R.id.genres);
        charge = view.findViewById(R.id.charge);
        distance = view.findViewById(R.id.distance);
        description = view.findViewById(R.id.description);
        createListing = view.findViewById(R.id.createPerformerListing);
        createListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                FirebaseFirestore db = FirebaseFirestore.getInstance();
                //need to check if listing exists, perhaps by a start and end date
                Map<String, Object> listing = new HashMap<>();
                listing.put("bandRef",bandRef);
                listing.put("name",name.getText().toString());
                listing.put("location",location.getText().toString());
                listing.put("genres",genres.getText().toString());
                listing.put("chargePerHour",(charge.getText()).toString());
                listing.put("distance",(distance.getText()).toString());
                listing.put("description",description.getText().toString());
                Boolean valid = true;
                String invalidFields = "";
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
                if (valid)
                {
                    db.collection("performer-listings")
                            .add(listing)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
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
                else
                {
                    Toast.makeText(getActivity(), "Listing not created.  The following fields " +
                            "are incomplete:\n" + invalidFields, Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }
}
