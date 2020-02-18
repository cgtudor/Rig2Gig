package com.gangoffive.rig2gig;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class CreatePerformerListing extends Fragment {

    private TextView name, location, genres, charge, distance, description;
    private ImageView image;
    private Button createListing, changeImage;
    private String bandRef;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference bandImageRef;
    private Map<String, Object> listing;
    private String invalidFields;
    private Map <String, Object> band;
    private static final int ACTION_PERFORMER_LISTING_PHOTO = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bandRef = "TvuDGJwqX13vJ6LWZYB2";
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        bandImageRef = storageRef.child("/images/bands/" + bandRef + "/profile.jpg");
        DocumentReference docRef = db.collection("bands").document(bandRef);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        band = document.getData();
                        populateFields();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_performer_listing, container, false);
        setInputReferences(view);
        createListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //need to check if listing exists, perhaps by a start and end date
                Map<String, Object> listing = bandDataMap();
                Boolean valid = validateDataMap();
                if (valid) {
                    postDataToDatabase();
                } else {
                    Toast.makeText(getActivity(), "Listing not created.  The following fields " +
                            "are incomplete:\n" + invalidFields, Toast.LENGTH_LONG).show();
                }
            }
        });
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, ACTION_PERFORMER_LISTING_PHOTO);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_PERFORMER_LISTING_PHOTO && resultCode == Activity.RESULT_OK && data != null)
        {
            try
            {
                InputStream iStream = getContext().getContentResolver().openInputStream(data.getData());
                image.setImageDrawable(Drawable.createFromStream(iStream, data.getData().toString()));
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }




    public void setInputReferences(View view)
    {
        name = view.findViewById(R.id.name);
        location = view.findViewById(R.id.location);
        genres = view.findViewById(R.id.genres);
        charge = view.findViewById(R.id.charge);
        distance = view.findViewById(R.id.distance);
        description = view.findViewById(R.id.description);
        createListing = view.findViewById(R.id.createPerformerListing);
        changeImage = view.findViewById(R.id.changeImage);
        image = view.findViewById(R.id.image);
    }

    public void populateFields()
    {
        name.setText((String)band.get("name"));
        location.setText((String)band.get("location"));
        String genreString = band.get("genres").toString();
        genres.setText(genreString.substring(1,genreString.length()-1));
        charge.setText(String.valueOf(band.get("chargePerHour")));
        distance.setText(String.valueOf(band.get("travelDistance")));
        description.setText((String)band.get("description"));
        GlideApp.with(this)
                .load(bandImageRef)
                .into(image);
    }

    public Map<String, Object> bandDataMap()
    {
        listing = new HashMap<>();
        listing.put("bandRef",bandRef);
        listing.put("name",name.getText().toString());
        listing.put("location",location.getText().toString());
        listing.put("genres",genres.getText().toString());
        listing.put("chargePerHour",(charge.getText()).toString());
        listing.put("distance",(distance.getText()).toString());
        listing.put("description",description.getText().toString());
        return listing;
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

    public void postDataToDatabase()
    {
        db.collection("performer-listings")
                .add(listing)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        StorageReference listingImage = storageRef.child("/images/performance-listings/" + documentReference.getId() + ".jpg");

// Get the data from an ImageView as bytes
                        image.setDrawingCacheEnabled(true);
                        image.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = listingImage.putBytes(data);
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
