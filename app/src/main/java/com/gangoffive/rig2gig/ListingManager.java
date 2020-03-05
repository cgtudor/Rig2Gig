package com.gangoffive.rig2gig;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
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
    private DocumentReference docRef, listRef;
    private StorageReference storageRef, imageRef, listingImage;
    private Map<String, Object> userInfo,listingInfo;
    private String collectionPath, imagePath, listingRef, listingPath;;



    /**
     * Constructor for ListingManager
     * @param userRef reference of user
     * @param type type of user (set up with enums once known)
     */
    ListingManager(String userRef, String type, String adRef)
    {
        listingRef = adRef;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        if (type.equals("Band Performer"))
        {
            docRef = db.collection("bands").document(userRef);
            imageRef = storageRef.child("/images/bands/" + userRef + ".jpg");
            collectionPath = "performer-listings";
            imagePath = "performance-listings";
        }
        else if (type.equals("Musician Performer"))
        {
            docRef = db.collection("musicians").document(userRef);
            imageRef = storageRef.child("/images/musicians/" + userRef + ".jpg");
            collectionPath = "performer-listings";
            imagePath = "performance-listings";
        }
        else if (type.equals("Band"))
        {
            docRef = db.collection("bands").document(userRef);
            imageRef = storageRef.child("/images/bands/" + userRef + ".jpg");
            collectionPath = "band-listings";
            imagePath = collectionPath;
        }
        else if (type.equals("Musician"))
        {
            docRef = db.collection("musicians").document(userRef);
            imageRef = storageRef.child("/images/musicians/" + userRef + ".jpg");
            collectionPath = "musician-listings";
            imagePath = collectionPath;
        }
        else if (type.equals("Venue"))
        {
            docRef = db.collection("venues").document(userRef);
            if (listingRef.equals("profileEdit"))
            {
                collectionPath = "venues";
                imagePath = collectionPath;
                imageRef = storageRef.child("/images/venues/" + userRef + ".jpg");
                listRef = db.collection(collectionPath).document(userRef);
                listingImage = storageRef.child("/images/" + imagePath +
                        "/" + userRef + ".jpg");
            }
            else
            {
                collectionPath = "venue-listings";
                imagePath = collectionPath;
                if (listingRef.equals(""))
                {
                    imageRef = storageRef.child("/images/venues/" + userRef + ".jpg");
                }
                else
                {
                    imageRef = storageRef.child("/images/"+ imagePath +"/" + listingRef + ".jpg");
                    listRef = db.collection(collectionPath).document(listingRef);
                    listingImage = storageRef.child("/images/" + imagePath +
                            "/" + listingRef + ".jpg");
                }
            }

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
                        if (listingRef.equals("") || listingRef.equals("profileEdit"))
                        {
                            activity.onSuccessFromDatabase(userInfo);
                        }
                        else
                        {
                            getListing(activity);
                        }
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

    public void getListing(CreateAdvertisement activity)
    {
        listRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            /**
             * on successful database query, return data and image to calling activity
             */
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        listingInfo = document.getData();
                        activity.onSuccessFromDatabase(userInfo,listingInfo);
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

    public void getImage(CreateAdvertisement activity)
    {
        GlideApp.with((Activity)activity)
                .load(imageRef)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(activity.getImageView());
        activity.onSuccessfulImageDownload();
    }

    /**
     * Post data and image to database
     * @param listing advertisement details to be uploaded
     * @param image ImageView containing image to be uploaded
     * @param activity interface of activity calling this method
     */
    public void postDataToDatabase(HashMap<String, Object> listing, Drawable image, CreateAdvertisement activity)
    {
        if (listingRef.equals(""))
        {
            createAdvertisement(listing, image, activity);
        }
        else
        {
            editAdvertisement(listing, image, activity);
        }
    }

    public void createAdvertisement (HashMap<String, Object> listing, Drawable image, CreateAdvertisement activity)
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
                        listingImage = storageRef.child("/images/" + imagePath +
                                "/" + listingRef + ".jpg");
                        Log.d(TAG, "DocumentSnapshot written with ID: " + listingRef);
                        uploadImage(image, activity);
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

    public void editAdvertisement (HashMap<String, Object> listing, Drawable image, CreateAdvertisement activity)
    {
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(listRef);
                for (String key : listing.keySet())
                {
                    transaction.update(listRef, key, listing.get(key));
                }
                return null;
            }
        })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                uploadImage(image, activity);
                Log.d(TAG, "Transaction success!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        activity.handleDatabaseResponse(CreationResult.LISTING_FAILURE);
                        Log.w(TAG, "Transaction failure.", e);
                    }
                });
    }

    public void uploadImage(Drawable image, CreateAdvertisement activity)
    {
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

    /**
     * convert image of ImageView to byte array for uploading to database
     * @return byte array of image
     */
    public byte[] imageToByteArray(Drawable image)
    {
        Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
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
