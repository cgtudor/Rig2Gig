package com.gangoffive.rig2gig.firebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gangoffive.rig2gig.advert.management.CreateAdvertisement;
import com.gangoffive.rig2gig.firebase.GlideApp;
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
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
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
    private String collectionPath, imagePath, listingRef;
    private boolean needPayment;
    private CreateAdvertisement activity;
    private Drawable image;
    private UploadTask uploadTask;
    private Bitmap bitmap;
    private Calendar calendar;

    /**
     * Constructor for ListingManager
     * @param userRef reference of user
     * @param type type of user (set up with enums once known)
     */
    public ListingManager(String userRef, String type, String adRef)
    {
        if (userRef != null)
        {
            if (type.equals("Band Performer") || type.equals("Musician Performer")
                    || type.equals("Venue"))
            {
                needPayment = true;
            }
            else
            {
                needPayment = false;
            }
            listingRef = adRef;
            getFirebaseInstances();
            storageRef = storage.getReference();
            if (type.equals("Band Performer"))
            {
                docRef = db.collection("bands").document(userRef);
                collectionPath = "performer-listings";
                imagePath = "performance-listings";
                if (listingRef.equals(""))
                {
                    imageRef = storageRef.child("/images/bands/" + userRef + ".jpg");
                }
                else
                {
                    imageRef = storageRef.child("/images/"+ imagePath +"/" + listingRef + ".jpg");
                    listRef = db.collection(collectionPath).document(listingRef);
                    listingImage = storageRef.child("/images/" + imagePath +
                            "/" + listingRef + ".jpg");
                }
            }
            else if (type.equals("Musician Performer"))
            {
                docRef = db.collection("musicians").document(userRef);
                collectionPath = "performer-listings";
                imagePath = "performance-listings";
                if (listingRef.equals("")){
                    imageRef = storageRef.child("/images/musicians/" + userRef + ".jpg");}
                else
                {
                    imageRef = storageRef.child("/images/"+ imagePath +"/" + listingRef + ".jpg");
                    listRef = db.collection(collectionPath).document(listingRef);
                    listingImage = storageRef.child("/images/" + imagePath +
                            "/" + listingRef + ".jpg");}
            }
            else if (type.equals("Band"))
            {
                docRef = db.collection("bands").document(userRef);
                if (listingRef.equals("profileEdit"))
                {
                    imageRef = storageRef.child("/images/bands/" + userRef + ".jpg");
                    collectionPath = "bands";
                    listRef = db.collection(collectionPath).document(userRef);
                    imagePath = collectionPath;
                    listingImage = storageRef.child("/images/" + imagePath +
                            "/" + userRef + ".jpg");
                }
                else
                {
                    collectionPath = "band-listings";
                    imagePath = collectionPath;
                    if (!adRef.equals(""))
                    {
                        imageRef = storageRef.child("/images/band-listings/" + adRef + ".jpg");
                        listingImage = storageRef.child("/images/" + imagePath +
                                "/" + adRef + ".jpg");
                        listRef = db.collection(collectionPath).document(adRef);
                    }
                    else
                    {
                        imageRef = storageRef.child("/images/bands/" + userRef + ".jpg");
                    }
                }
            }
            else if (type.equals("Musician"))
            {
                docRef = db.collection("musicians").document(userRef);
                if (listingRef.equals("profileEdit"))
                {
                    imageRef = storageRef.child("/images/musicians/" + userRef + ".jpg");
                    collectionPath = "musicians";
                    listRef = db.collection(collectionPath).document(userRef);
                    imagePath = collectionPath;
                    listingImage = storageRef.child("/images/" + imagePath +
                            "/" + userRef + ".jpg");
                }
                else
                {
                    collectionPath = "musician-listings";
                    imagePath = collectionPath;
                    if (!adRef.equals(""))
                    {
                        imageRef = storageRef.child("/images/musician-listings/" + adRef + ".jpg");
                        listingImage = storageRef.child("/images/" + imagePath +
                                "/" + adRef + ".jpg");
                        listRef = db.collection(collectionPath).document(adRef);
                    }
                    else
                    {
                        imageRef = storageRef.child("/images/musicians/" + userRef + ".jpg");
                    }
                }
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
                }            }
            else if (type.equals("User"))
            {
                docRef = db.collection("users").document(userRef);
                collectionPath = "users";}}
    }

    /**
     * Get firebase instances
     */
    public void getFirebaseInstances()
    {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
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
        this.activity = activity;
        if (docRef != null) {
            docRef.get().addOnCompleteListener(getInfoListener);
        }
        else
        {
            activity.onSuccessFromDatabase(userInfo);
        }
    }

    private OnCompleteListener getInfoListener = new OnCompleteListener<DocumentSnapshot>() {
        @Override
        /**
         * on successful database query, return data and image to calling activity
         */
        public void onComplete(@NonNull Task<DocumentSnapshot> task)
        {
            getUserInfoOnComplete(task);
        }
    };

    /**
     * Process successful retrieval of user data
     * @param task get task
     */
    public void getUserInfoOnComplete(@NonNull Task<DocumentSnapshot> task)
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

    /**
     * Download existing record from database
     * @param activity activity calling the method
     */
    public void getListing(CreateAdvertisement activity)
    {
        this.activity = activity;
        listRef.get().addOnCompleteListener(getListingListener);
    }

    private OnCompleteListener getListingListener = new OnCompleteListener<DocumentSnapshot>() {
        @Override
        /**
         * on successful database query, return data and image to calling activity
         */
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            getListingOnComplete(task);
        }
    };

    /**
     * Process successful retrieval of advert data
     * @param task get task
     */
    public void getListingOnComplete(@NonNull Task<DocumentSnapshot> task)
    {
        if (task.isSuccessful()) {
            DocumentSnapshot document = task.getResult();
            if (document.exists()) {
                listingInfo = document.getData();
                activity.onSuccessFromDatabase(userInfo,listingInfo);
                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

            }
            else {
                Log.d(TAG, "No such document");
            }
        }
        else
        {
            Log.d(TAG, "get failed with ", task.getException());
        }
    }

    private RequestListener imageListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            activity.onSuccessfulImageDownload();
            return false;
        }
    };

    /**
     * Download image from database and set to image view
     * @param activity calling activity
     */
    public void getImage(CreateAdvertisement activity)
    {
        if (imageRef != null)
        {
            GlideApp.get((Context)activity)
                    .with((Activity)activity)
                    .load(imageRef)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .listener(imageListener)
                    .into(activity.getImageView());
        }
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

    private OnSuccessListener createAdListener = new OnSuccessListener<DocumentReference>() {
        /**
         * After successful creation of advertisement, attempt image upload
         * @param documentReference database reference of created advertisement
         */
        @Override
        public void onSuccess(DocumentReference documentReference) {
            createAdvertisementOnComplete(documentReference);
        }
    };

    private OnFailureListener failureListener = new OnFailureListener() {
        /**
         * After failed advertisement creation, send image failure message to the calling activity
         * @param e exception
         */
        @Override
        public void onFailure(@NonNull Exception e)
        {
            activity.handleDatabaseResponse(CreationResult.LISTING_FAILURE);
            Log.w(TAG, "Transaction failure.", e);
        }
    };

    /**
     * Create new record in database
     * @param listing map of data to be created as document
     * @param image image to be uploaded
     * @param activity calling activity
     */
    public void createAdvertisement (HashMap<String, Object> listing, Drawable image, CreateAdvertisement activity)
    {
        this.image = image;
        calendar = Calendar.getInstance();
        listing.put("posting-date", new Timestamp(calendar.getTime()));
        getExpiryDate();
        listing.put("expiry-date", new Timestamp(calendar.getTime()));
        db.collection(collectionPath)
                .add(listing)
                .addOnSuccessListener(createAdListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Handle successful database put
     * @param documentReference reference of new document
     */
    public void createAdvertisementOnComplete(DocumentReference documentReference)
    {
        listingRef = documentReference.getId();
        listingImage = storageRef.child("/images/" + imagePath +
                "/" + listingRef + ".jpg");
        Log.d(TAG, "DocumentSnapshot written with ID: " + listingRef);
        uploadImage(image, activity);
    }

    private OnSuccessListener editAdListener = new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            editAdvertisementOnComplete();}
    };

    /**
     * Edit record in database
     * @param listing map of data to be edited
     * @param image new image to be uploaded
     * @param activity calling activity
     */
    public void editAdvertisement (HashMap<String, Object> listing, Drawable image, CreateAdvertisement activity)
    {
        this.image = image;
        setListingInfo(listing);
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                applyEdit(transaction);
                return null;
            }
        })
                .addOnSuccessListener(editAdListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Begin process of updating document in database
     * @param transaction database transaction
     * @throws FirebaseFirestoreException
     */
    public void applyEdit (Transaction transaction) throws FirebaseFirestoreException
    {
        DocumentSnapshot snapshot = transaction.get(listRef);
        for (String key : listingInfo.keySet())
        {
            transaction.update(listRef, key, listingInfo.get(key));
        }
    }

    /**
     * Handle completion of database document update
     */
    public void editAdvertisementOnComplete ()
    {
        if (image != null)
        {
            uploadImage(image, activity);
        }
        else
        {
            activity.handleDatabaseResponse(CreationResult.SUCCESS);
        }
        Log.d(TAG, "Transaction success!");
    }

    /**
     * Upload image to database
     * @param image image to be uploaded
     * @param activity calling activity
     */
    public void uploadImage(Drawable image, CreateAdvertisement activity)
    {
        if(image !=null)
        {
            uploadTask = listingImage.putBytes(imageToByteArray(image));
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
    }

    /**
     * convert image of ImageView to byte array for uploading to database
     * @param image image to be converted
     * @return byte array of image
     */
    public byte[] imageToByteArray(Drawable image)
    {
        if(image != null)
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try            {
                bitmap = ((BitmapDrawable) image).getBitmap();
            }
            catch (Exception e){ }
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            }
            catch (Exception e) { }
            return stream.toByteArray();
        }
        else
        {
            return new byte[] {};
        }
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
    public void getExpiryDate()
    {
        if (needPayment == true)
        {
            calendar.add(Calendar.DATE, -1);
        }
        else if (calendar.get(Calendar.HOUR_OF_DAY) == 0
                && calendar.get(Calendar.MINUTE) == 0
                && calendar.get(Calendar.SECOND) == 0
                && calendar.get(Calendar.MILLISECOND) == 0)
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
    }

    /**
     * @return storageRef
     */
    public StorageReference getStorageRef() {
        return storageRef;
    }

    /**
     * @param db db to set
     */
    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }

    /**
     * @param storage storage to set
     */
    public void setStorage(FirebaseStorage storage) {
        this.storage = storage;
    }

    /**
     * @param docRef docRef to set
     */
    public void setDocRef(DocumentReference docRef) {
        this.docRef = docRef;
    }


    /**
     * @param listRef listRef to set
     */
    public void setListRef(DocumentReference listRef) {
        this.listRef = listRef;
    }

    /**
     * @param storageRef storageRef to set
     */
    public void setStorageRef(StorageReference storageRef) {
        this.storageRef = storageRef;
    }

    /**
     * @return imageRef
     */
    public StorageReference getImageRef() {return imageRef;}

    /**
     * @param imageRef imageRef to set
     */
    public void setImageRef(StorageReference imageRef) {
        this.imageRef = imageRef;
    }

    /**
     * @param listingImage listingImage to set
     */
    public void setListingImage(StorageReference listingImage) {
        this.listingImage = listingImage;
    }

    /**
     * @param userInfo userInfo to set
     */
    public void setUserInfo(Map<String, Object> userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * @param listingInfo listingInfo to set
     */
    public void setListingInfo(Map<String, Object> listingInfo) {
        this.listingInfo = listingInfo;
    }

    /**
     * @return userInfo
     */
    public Map<String, Object> getUserInfo() {return userInfo;}

    /**
     * @return listingInfo
     */
    public Map<String, Object> getListingInfo() {return listingInfo;}

    /**
     * @return collectionPath
     */
    public String getCollectionPath() {return collectionPath;}

    /**
     * @return imagePath
     */
    public String getImagePath() {return imagePath;}

    /**
     * @return needPayment
     */
    public boolean isNeedPayment() {return needPayment;}

    /**
     * @return db
     */
    public FirebaseFirestore getDb() {return db;}

    /**
     * @return storage
     */
    public FirebaseStorage getStorage() {return storage;}

    /**
     * @param activity activity to set
     */
    public void setActivity(CreateAdvertisement activity) {
        this.activity = activity;
    }

    /**
     * @return listingImage
     */
    public StorageReference getListingImage() {return listingImage;}

    /**
     * @param image image to set
     */
    public void setImage(Drawable image) {
        this.image = image;
    }

    /**
     * @return listRef
     */
    public DocumentReference getListRef() {return listRef;}

    /**
     * @param bitmap bitmap to set
     */
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * @param calendar calendar to set
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    /**
     * @return calendar
     */
    public Calendar getCalendar() {return calendar;}
}
