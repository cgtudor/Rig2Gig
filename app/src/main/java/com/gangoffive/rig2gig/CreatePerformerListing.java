package com.gangoffive.rig2gig;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class CreatePerformerListing extends Fragment {

    private TextView name, location;
    private ImageView image;
    private Button changeImage, takePhoto, next;
    private String bandRef;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference bandImageRef;
    private HashMap<String, Object> listing;
    private String invalidFields;
    private Map <String, Object> band;
    private static final int REQUEST_GALLERY__PHOTO = 1;
    private static final int REQUEST_PHOTO = 2;

    /**
     * Get band data from database and populate fields (or from map passed in)
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bandRef = "TvuDGJwqX13vJ6LWZYB2";
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        bandImageRef = storageRef.child("/images/bands/" + bandRef + "/profile.jpg");
        if(getArguments() == null)
        {
            DocumentReference docRef = db.collection("bands").document(bandRef);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            band = document.getData();
                            populateFields();
                            downloadImage();
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
        else
        {
            band = (HashMap)getArguments().getSerializable("listing");
        }

    }

    /**
     * set references, populate fields if returning from previous fragment and set on click listeners
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return inflated view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_performer_listing, container, false);
        setInputReferences(view);
        if(getArguments() != null)
        {
            populateFields();
            getTempImage();
        }
        //on click listener for selecting from gallery
        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_GALLERY__PHOTO);
            }
        });
        //on click listener for taking a photo
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_PHOTO);
                }
            }
        });
        //on click listener for next button
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
        return view;
    }

    /**
     * handles activity results
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY__PHOTO && resultCode == Activity.RESULT_OK && data != null)
        {
            try
            {
                InputStream iStream = getContext().getContentResolver().openInputStream(data.getData());
                image.setImageDrawable(Drawable.createFromStream(iStream, data.getData().toString()));
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        else if (requestCode == REQUEST_PHOTO && resultCode == Activity.RESULT_OK && data != null)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(photo);
        }
    }

    /**
     * set references to text and image views and buttons
     * @param view
     */
    public void setInputReferences(View view)
    {
        name = view.findViewById(R.id.name);
        location = view.findViewById(R.id.location);
        changeImage = view.findViewById(R.id.changeImage);
        takePhoto = view.findViewById(R.id.takePhoto);
        next = view.findViewById(R.id.next);
        image = view.findViewById(R.id.image);
    }

    /**
     * populate text views
     */
    public void populateFields()
    {
        name.setText((String)band.get("name"));
        location.setText((String)band.get("location"));
    }

    /**
     * download band image from database
     */
    public void downloadImage()
    {
        GlideApp.with(this)
                .load(bandImageRef)
                .into(image);
    }

    /**
     * set image view to local file if returning from a different listing fragment
     */
    public void getTempImage()
    {
        Bitmap bandImage= BitmapFactory.decodeFile(System.getProperty("java.io.tmpdir") + bandRef + ".tmp");
        image.setImageBitmap(bandImage);
    }

    /**
     * populate listing map with combination of values from text views and map generated from database
     */
    public void bandDataMap()
    {
        if (listing == null)
        {
            listing = new HashMap<>();
        }
        listing.put("bandRef",bandRef);
        listing.put("name",name.getText().toString());
        listing.put("location",location.getText().toString());
        listing.put("description",band.get("description").toString());
        listing.put("genres",band.get("genres").toString());
        listing.put("charge",band.get("charge").toString());
        listing.put("distance",band.get("distance").toString());
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
     * convert image to temporary bitmap file to be referenced later
     */
    public void imageToBitmapFile() {
        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        File temp = new File(System.getProperty("java.io.tmpdir") + bandRef + ".tmp");
        try {
            FileOutputStream fStream = new FileOutputStream(temp);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * perform transaction to next fragment and pass relevant data in bundle
     */
    public void sendToNextListingFragment()
    {
        CreatePerformerListingDescription fragment = new CreatePerformerListingDescription();
        Bundle bandInfo = new Bundle();
        bandInfo.putSerializable("listing",listing);
        imageToBitmapFile();
        fragment.setArguments(bandInfo);
        FragmentTransaction fTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fTransaction.replace(R.id.fragment_container,fragment);
        fTransaction.commit();
    }

}
