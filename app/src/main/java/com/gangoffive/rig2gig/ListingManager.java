package com.gangoffive.rig2gig;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;
import androidx.annotation.NonNull;
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
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class ListingManager
{
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private DocumentReference docRef;
    private StorageReference storageRef;
    private StorageReference bandImageRef;
    private Map<String, Object> userInfo;
    private String collectionPath, imagePath, listingRef;
    private Boolean listingFailure;
    private Boolean imageFailure;

    /**
     * Constructor for ListingManager
     * @param userRef reference of user
     * @param type type of user (set up with enums once known)
     */
    ListingManager(String userRef, String type)
    {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        if (type.equals("Band Performer"))
        {
            docRef = db.collection("bands").document(userRef);
            bandImageRef = storageRef.child("/images/bands/" + userRef + ".jpg");
            collectionPath = "performer-listings";
            imagePath = "performance-listings";
        }
        else if (type.equals("Musician Performer"))
        {
            docRef = db.collection("musicians").document(userRef);
            bandImageRef = storageRef.child("/images/musicians/" + userRef + ".jpg");
            collectionPath = "performer-listings";
            imagePath = "performance-listings";
        }
        else if (type.equals("Band"))
        {
            docRef = db.collection("bands").document(userRef);
            bandImageRef = storageRef.child("/images/bands/" + userRef + ".jpg");
            collectionPath = "band-listings";
            imagePath = collectionPath;
        }
        else if (type.equals("Musician"))
        {
            docRef = db.collection("musicians").document(userRef);
            bandImageRef = storageRef.child("/images/musicians/" + userRef + ".jpg");
            collectionPath = "musician-listings";
            imagePath = collectionPath;
        }
        else if (type.equals("Venue"))
        {
            docRef = db.collection("venues").document(userRef);
            bandImageRef = storageRef.child("/images/venues/" + userRef + ".jpg");
            collectionPath = "venue-listings";
            imagePath = collectionPath;
        }
    }

    /**
     * Enums used to track upload success
     */
    public enum CreationResult
    {
        SUCCESS, IMAGE_FAILURE, LISTING_FAILURE
    };

    /**
     * download user details from database
     * @param activity interface of activity calling this method
     */
    public void getUserInfo(CreateAdvertisement activity)
    {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            /**
             * on successful database query, return data and image to calling activity
             */
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userInfo = document.getData();
                        activity.onSuccessFromDatabase(userInfo);
                        GlideApp.with((Activity)activity)
                                .load(bandImageRef)
                                .into(activity.getImageView());
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                    } else {
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
     * Post data and image to database
     * @param listing advertisement details to be uploaded
     * @param image ImageView containing image to be uploaded
     * @param activity interface of activity calling this method
     */
    public void postDataToDatabase(HashMap<String, Object> listing, ImageView image, CreateAdvertisement activity)
    {
        listing.put("expiry-date", new Timestamp(getExpiryDate()));
        db.collection(collectionPath)
                .add(listing)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    /**
                     * After successful creation of advertisement, attempt image upload
                     * @param documentReference database reference of created advertisement
                     */
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        listingRef = documentReference.getId();
                        Log.d(TAG, "DocumentSnapshot written with ID: " + listingRef);
                        StorageReference listingImage = storageRef.child("/images/" + imagePath +
                                                                         "/" + listingRef + ".jpg");
                        UploadTask uploadTask = listingImage.putBytes(imageToByteArray(image));
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            /**
                             * After successful image upload, send success message to the calling activity
                             * @param taskSnapshot snapshot of image upload attempt
                             */
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                activity.handleDatabaseResponse(CreationResult.SUCCESS);
                            }
                        });
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            /**
                             * After failed image upload, send image failure message to the calling activity
                             * @param exception exception
                             */
                            @Override
                            public void onFailure(@NonNull Exception exception)
                            {
                                activity.handleDatabaseResponse(CreationResult.IMAGE_FAILURE);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    /**
                     * After failed advertisement creation, send image failure message to the calling activity
                     * @param e exception
                     */
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        activity.handleDatabaseResponse(CreationResult.LISTING_FAILURE);
                    }
                });
    }

    /**
     * convert image of ImageView to byte array for uploading to database
     * @return byte array of image
     */
    public byte[] imageToByteArray(ImageView image)
    {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * get listing reference
     * @return listing reference
     */
    public String getListingRef()
    {
        return listingRef;
    }

    /**
     * calculate expiry date of listing (31 days, rounded up to midnight)
     */
    public Date getExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        if (Calendar.HOUR_OF_DAY == 0
                && Calendar.MINUTE == 0
                && Calendar.SECOND == 0
                && Calendar.MILLISECOND == 0) {
            calendar.add(Calendar.DAY_OF_YEAR, 31);
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, 32);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
        return calendar.getTime();
    }
}
