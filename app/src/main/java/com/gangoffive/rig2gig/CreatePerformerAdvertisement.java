package com.gangoffive.rig2gig;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class CreatePerformerAdvertisement extends AppCompatActivity {

    private TextView distance, name;
    private ImageView image;
    private String performerRef, performerType;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private DocumentReference docRef;
    private StorageReference storageRef;
    private StorageReference bandImageRef;
    private HashMap<String, Object> listing;
    private Map<String, Object> band;
    private static final int REQUEST_GALLERY__PHOTO = 1;
    private static final int REQUEST_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        performerRef = "TvuDGJwqX13vJ6LWZYB2";
        performerType = "Band";

        setContentView(R.layout.activity_create_performer_advertisement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        if (performerType.equals("Band"))
        {
            docRef = db.collection("bands").document(performerRef);
            bandImageRef = storageRef.child("/images/bands/" + performerRef + "/profile.jpg");
        }
        else if (performerType.equals("Musician"))
        {
            docRef = db.collection("musicians").document(performerRef);
            bandImageRef = storageRef.child("/images/musicians/" + performerRef + "/profile.jpg");
        }
        setInputReferences();
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists())
                    {
                        band = document.getData();
                        populateFields();
                        downloadImage();
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    }
                    else
                    {
                        Log.d(TAG, "No such document");
                    }
                }
                else
                {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * get gallery image for advertisement
     */
    public void getGalleryImage(View view)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY__PHOTO);
    }

    /**
     * get camera image for advertisement
     */
    public void getCameraImage(View view)
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(intent, REQUEST_PHOTO);
        }
    }

    /**
     * create advertisement, posting to database
     */
    public void createAdvertisement(View view)
    {
        listingDataMap();
        if (validateDataMap())
        {
            postDataToDatabase();
        } else {
            Toast.makeText(CreatePerformerAdvertisement.this,
                    "Listing not created.  Ensure all fields are complete " +
                            "and try again",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * cancel advertisement creation
     */
    public void cancelAdvertisement(View view)
    {
        Intent backToMain = new Intent(CreatePerformerAdvertisement.this,
                MainActivity.class);
        startActivity(backToMain);
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
                InputStream iStream = getContentResolver().openInputStream(data.getData());
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
     */
    public void setInputReferences()
    {
        name = findViewById(R.id.name);
        image = findViewById(R.id.image);
        distance = findViewById(R.id.distance);
    }

    /**
     * populate text views
     */
    public void populateFields()
    {
        name.setText(band.get("name").toString());
        distance.setText(band.get("distance").toString());
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
     * populate listing map with combination of values from text views and map generated from database
     */
    public void listingDataMap()
    {
        if (listing == null)
        {
            listing = new HashMap<>();
            listing.put("performer-ref",performerRef);
            listing.put("type",performerType);
        }
        listing.put("distance",distance.getText().toString());
        listing.put("expiry-date",new Timestamp(getExpiryDate()));
    }

    /**
     * calculate expiry date of listing (31 days, rounded up to midnight)
     */
    public Date getExpiryDate()
    {
        Calendar calendar = Calendar.getInstance();
        if (Calendar.HOUR_OF_DAY == 0
                && Calendar.MINUTE == 0
                && Calendar.SECOND == 0
                && Calendar.MILLISECOND == 0)
        {
            calendar.add(Calendar.DAY_OF_YEAR, 31);
        }
        else
        {
            calendar.add(Calendar.DAY_OF_YEAR, 32);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
        return calendar.getTime();
    }

    /**
     * validate data in listing map
     * @return true if valid
     */
    public boolean validateDataMap()
    {
        for (Map.Entry element : listing.entrySet())
        {
            String val = element.getValue().toString();
            if(val == null || val.trim().isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    /**
     * convert image of ImageView to byte array for uploading to database
     * @return byte array of image
     */
    public byte[] imageToByteArray()
    {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * upload listing data and image to database
     */
    public void postDataToDatabase()
    {

        db.collection("performer-listings")
                .add(listing)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        StorageReference listingImage = storageRef.child("/images/performance-listings/" + documentReference.getId() + ".jpg");
                        UploadTask uploadTask = listingImage.putBytes(imageToByteArray());
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Intent intent = new Intent(CreatePerformerAdvertisement.this, PerformanceListingDetailsActivity.class);
                                intent.putExtra("EXTRA_PERFORMANCE_LISTING_ID",documentReference.getId());
                                startActivity(intent);
                            }
                        });
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

