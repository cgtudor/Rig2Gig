package com.gangoffive.rig2gig;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.gangoffive.rig2gig.advert.management.CreateAdvertisement;
import com.gangoffive.rig2gig.firebase.ListingManager;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ListingManagerTest{

    static HashMap<String, Object> userData, listingData;

    public void mockFirebase(ListingManager manager)
    {

    }

    @BeforeClass
    public static void setUp() {
        userData = new HashMap<>();
        userData.put("name","testName");
        listingData = new HashMap<>();
        listingData.put("data1","testData1");
        listingData.put("data2","testData2");
    }


    public ListingManager dummyConstructor(String ref, String type, String adRef)
    {
        return new ListingManager(ref,type,adRef)
        {
            @Override
            public void getFirebaseInstances()
            {
                //FirebaseApp mockApp = mock(FirebaseApp.class);
                FirebaseFirestore mockStore = mock(FirebaseFirestore.class);
                when(mockStore.collection(any())).thenReturn(mock(CollectionReference.class));
                setDb(mockStore);
                FirebaseStorage mockStorage = mock(FirebaseStorage.class);
                when(mockStorage.getReference()).thenReturn(mock(StorageReference.class));
                setStorage(mockStorage);
            }};
    }

    @Test
    public void testBandDetailsConstructor()
    {
        ListingManager manager = dummyConstructor("testRef", "Band", "profileEdit");
        assertThat(manager.getCollectionPath(),is(equalTo("bands")));
        assertThat(manager.getImagePath(),is(equalTo("bands")));
        assertThat(manager.isNeedPayment(),is(equalTo(false)));
    }

    @Test
    public void testBandNewAdConstructor()
    {
        ListingManager manager = dummyConstructor("testRef", "Band", "");
        assertThat(manager.getCollectionPath(),is(equalTo("band-listings")));
        assertThat(manager.getImagePath(),is(equalTo("band-listings")));
        assertThat(manager.isNeedPayment(),is(equalTo(false)));
    }

    @Test
    public void testBandExistingAdConstructor()
    {
        ListingManager manager = dummyConstructor("testRef", "Band", "testRef");
        assertThat(manager.getCollectionPath(),is(equalTo("band-listings")));
        assertThat(manager.getImagePath(),is(equalTo("band-listings")));
        assertThat(manager.isNeedPayment(),is(equalTo(false)));
    }

    @Test
    public void testMusicianDetailsConstructor()
    {
        ListingManager manager = dummyConstructor("testRef", "Musician", "profileEdit");
        assertThat(manager.getCollectionPath(),is(equalTo("musicians")));
        assertThat(manager.getImagePath(),is(equalTo("musicians")));
        assertThat(manager.isNeedPayment(),is(equalTo(false)));
    }

    @Test
    public void testMusicianNewAdConstructor()
    {
        ListingManager manager = dummyConstructor("testRef", "Musician", "");
        assertThat(manager.getCollectionPath(),is(equalTo("musician-listings")));
        assertThat(manager.getImagePath(),is(equalTo("musician-listings")));
        assertThat(manager.isNeedPayment(),is(equalTo(false)));
    }

    @Test
    public void testMusicianExistingAdConstructor()
    {
        ListingManager manager = dummyConstructor("testRef", "Musician", "testRef");
        assertThat(manager.getCollectionPath(),is(equalTo("musician-listings")));
        assertThat(manager.getImagePath(),is(equalTo("musician-listings")));
        assertThat(manager.isNeedPayment(),is(equalTo(false)));
    }

    @Test
    public void testVenueDetailsConstructor()
    {
        ListingManager manager = dummyConstructor("testRef", "Venue", "profileEdit");
        assertThat(manager.getCollectionPath(),is(equalTo("venues")));
        assertThat(manager.getImagePath(),is(equalTo("venues")));
        assertThat(manager.isNeedPayment(),is(equalTo(true)));
    }

    @Test
    public void testVenueNewAdConstructor()
    {
        ListingManager manager = dummyConstructor("testRef", "Venue", "");
        assertThat(manager.getCollectionPath(),is(equalTo("venue-listings")));
        assertThat(manager.getImagePath(),is(equalTo("venue-listings")));
        assertThat(manager.isNeedPayment(),is(equalTo(true)));
    }

    @Test
    public void testVenueExistingAdConstructor()
    {
        ListingManager manager = dummyConstructor("testRef", "Venue", "testRef");
        assertThat(manager.getCollectionPath(),is(equalTo("venue-listings")));
        assertThat(manager.getImagePath(),is(equalTo("venue-listings")));
        assertThat(manager.isNeedPayment(),is(equalTo(true)));
    }

    @Test
    public void testBandPerformerNewAdConstructor()
    {
        ListingManager manager = dummyConstructor("testRef", "Band Performer", "testRef");
        assertThat(manager.getCollectionPath(),is(equalTo("performer-listings")));
        assertThat(manager.getImagePath(),is(equalTo("performance-listings")));
        assertThat(manager.isNeedPayment(),is(equalTo(true)));
    }

    @Test
    public void testBandPerformerExistingAdConstructor()
    {
        ListingManager manager = dummyConstructor("testRef", "Band Performer", "");
        assertThat(manager.getCollectionPath(),is(equalTo("performer-listings")));
        assertThat(manager.getImagePath(),is(equalTo("performance-listings")));
        assertThat(manager.isNeedPayment(),is(equalTo(true)));
    }

    @Test
    public void testMusicianPerformerNewAdConstructor()
    {
        ListingManager manager = dummyConstructor("testRef", "Musician Performer", "testRef");
        assertThat(manager.getCollectionPath(),is(equalTo("performer-listings")));
        assertThat(manager.getImagePath(),is(equalTo("performance-listings")));
        assertThat(manager.isNeedPayment(),is(equalTo(true)));
    }

    @Test
    public void testMusicianPerformerExistingAdConstructor()
    {
        ListingManager manager = dummyConstructor("testRef", "Musician Performer", "");
        assertThat(manager.getCollectionPath(),is(equalTo("performer-listings")));
        assertThat(manager.getImagePath(),is(equalTo("performance-listings")));
        assertThat(manager.isNeedPayment(),is(equalTo(true)));
    }

    @Test
    public void testUserConstructor()
    {
        ListingManager manager = dummyConstructor("testRef", "User", "");
        assertThat(manager.getCollectionPath(),is(equalTo("users")));
        assertThat(manager.getImagePath(),is(equalTo(null)));
        assertThat(manager.isNeedPayment(),is(equalTo(false)));
    }

    @Test
    public void testGetUserInfo()
    {
        ListingManager manager = dummyConstructor("testRef", "Musician Performer", "");
        DocumentReference mockDoc = mock(DocumentReference.class);
        Task<DocumentSnapshot> getTask = mock(Task.class);
        when(mockDoc.get()).thenReturn(getTask);
        Task<DocumentSnapshot> listenerTask = mock(Task.class);
        when(getTask.addOnCompleteListener(any())).thenReturn(listenerTask);
        manager.setDocRef(mockDoc);
        manager.getUserInfo(mock(CreateAdvertisement.class));
        verify(mockDoc,times(1)).get();
        verify(getTask,times(1)).addOnCompleteListener(any());
    }

    @Test
    public void testGetInfoOnCompleteProfileEdit() {
        ListingManager manager = dummyConstructor("testRef", "Venue", "profileEdit");
        CreateAdvertisement activity = mock(CreateAdvertisement.class);
        manager.setActivity(activity);
        Task mockTask = mock(Task.class);
        DocumentSnapshot mockDocSnap = mock(DocumentSnapshot.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDocSnap);
        when(mockDocSnap.exists()).thenReturn(true);
        when(mockDocSnap.getData()).thenReturn(userData);
        manager.getUserInfoOnComplete(mockTask);
        verify(activity,times(1)).onSuccessFromDatabase(userData);
    }

    @Test
    public void testGetInfoOnCompleteAdvertCreate() {
        ListingManager manager = dummyConstructor("testRef", "Venue", "");
        CreateAdvertisement activity = mock(CreateAdvertisement.class);
        manager.setActivity(activity);
        Task mockTask = mock(Task.class);
        DocumentSnapshot mockDocSnap = mock(DocumentSnapshot.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDocSnap);
        when(mockDocSnap.exists()).thenReturn(true);
        when(mockDocSnap.getData()).thenReturn(userData);
        manager.getUserInfoOnComplete(mockTask);
        verify(activity,times(1)).onSuccessFromDatabase(userData);
    }

    @Test
    public void testGetInfoOnCompleteAdvertCreateNoDoc() {
        ListingManager manager = dummyConstructor("testRef", "Venue", "");
        CreateAdvertisement activity = mock(CreateAdvertisement.class);
        manager.setActivity(activity);
        Task mockTask = mock(Task.class);
        DocumentSnapshot mockDocSnap = mock(DocumentSnapshot.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDocSnap);
        when(mockDocSnap.exists()).thenReturn(false);
        manager.getUserInfoOnComplete(mockTask);
        verify(activity,times(0)).onSuccessFromDatabase(userData);
    }

    @Test
    public void testGetInfoOnCompleteAdvertCreateTaskFail() {
        ListingManager manager = dummyConstructor("testRef", "Venue", "");
        CreateAdvertisement activity = mock(CreateAdvertisement.class);
        manager.setActivity(activity);
        Task mockTask = mock(Task.class);
        DocumentSnapshot mockDocSnap = mock(DocumentSnapshot.class);
        when(mockTask.isSuccessful()).thenReturn(false);
        manager.getUserInfoOnComplete(mockTask);
        verify(activity,times(0)).onSuccessFromDatabase(userData);
    }

    @Test
    public void testGetInfoOnCompleteAdvertEdit() {
        ListingManager manager = dummyConstructor("testRef", "Venue", "testRef");
        CreateAdvertisement activity = mock(CreateAdvertisement.class);
        manager.setActivity(activity);
        Task mockTask = mock(Task.class);
        DocumentSnapshot mockDocSnap = mock(DocumentSnapshot.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDocSnap);
        when(mockDocSnap.exists()).thenReturn(true);
        when(mockDocSnap.getData()).thenReturn(userData);
        DocumentReference mockRef = mock(DocumentReference.class);
        manager.setListRef(mockRef);
        when(mockRef.get()).thenReturn(mockTask);
        when(mockTask.addOnCompleteListener(any())).thenReturn(mock(Task.class));
        manager.getUserInfoOnComplete(mockTask);
        verify(mockRef,times(1)).get();
        verify(mockTask,times(1)).addOnCompleteListener(any());
    }

    @Test
    public void testGetListingOnCompleteDocExists() {
        ListingManager manager = dummyConstructor("testRef", "Venue", "");
        manager.setUserInfo(userData);
        CreateAdvertisement activity = mock(CreateAdvertisement.class);
        manager.setActivity(activity);
        Task mockTask = mock(Task.class);
        DocumentSnapshot mockDocSnap = mock(DocumentSnapshot.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDocSnap);
        when(mockDocSnap.exists()).thenReturn(true);
        when(mockDocSnap.getData()).thenReturn(listingData);
        manager.getListingOnComplete(mockTask);
        verify(activity,times(1)).onSuccessFromDatabase(userData,listingData);
    }

    @Test
    public void testGetListingOnCompleteDocDoesntExist() {
        ListingManager manager = dummyConstructor("testRef", "Venue", "");
        manager.setUserInfo(userData);
        CreateAdvertisement activity = mock(CreateAdvertisement.class);
        manager.setActivity(activity);
        Task mockTask = mock(Task.class);
        DocumentSnapshot mockDocSnap = mock(DocumentSnapshot.class);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mockTask.getResult()).thenReturn(mockDocSnap);
        when(mockDocSnap.exists()).thenReturn(false);
        manager.getListingOnComplete(mockTask);
        verify(activity,times(0)).onSuccessFromDatabase(userData,listingData);
    }

    @Test
    public void testGetListingOnCompleteUnsuccessfulTask() {
        ListingManager manager = dummyConstructor("testRef", "Venue", "");
        manager.setUserInfo(userData);
        CreateAdvertisement activity = mock(CreateAdvertisement.class);
        manager.setActivity(activity);
        Task mockTask = mock(Task.class);
        DocumentSnapshot mockDocSnap = mock(DocumentSnapshot.class);
        when(mockTask.isSuccessful()).thenReturn(false);
        manager.getListingOnComplete(mockTask);
        verify(activity,times(0)).onSuccessFromDatabase(userData,listingData);
    }

    @Test
    public void testGetImage()
    {
        ListingManager manager = dummyConstructor("testRef", "Venue", "");
        CreateAdvertisement activity = mock(CreateAdvertisement.class);
        manager.getImage(activity);
    }

    @Test
    public void testPostNewAdvertToDatabase()
    {
        ListingManager manager = dummyConstructor("testRef", "Venue", "");
        FirebaseFirestore mockDb = mock(FirebaseFirestore.class);
        CollectionReference mockColl = mock(CollectionReference.class);
        Task mockAdd = mock(Task.class);
        when(mockDb.collection(any())).thenReturn(mockColl);
        when(mockColl.add(any())).thenReturn(mockAdd);
        when(mockAdd.addOnSuccessListener(any())).thenReturn(mock(Task.class));
        when(mockAdd.addOnFailureListener(any())).thenReturn(mock(Task.class));
        manager.setDb(mockDb);
        Drawable mockDrawable = mock(Drawable.class);
        CreateAdvertisement mockActivity = mock(CreateAdvertisement.class);
        manager.postDataToDatabase(listingData,mockDrawable,mockActivity);
        verify(mockDb,times(1)).collection(any());
        verify(mockColl,times(1)).add(listingData);
    }

    @Test
    public void testcreateAdvertisementOnCompleteListingRef()
    {
        ListingManager manager = dummyConstructor("testRef", "Venue", "");
        DocumentReference mockDoc = mock(DocumentReference.class);
        when(mockDoc.getId()).thenReturn("testRef");
        StorageReference parentRef = mock(StorageReference.class);
        
        manager.setListingImage(parentRef);
        manager.setImage(null);
        manager.createAdvertisementOnComplete(mockDoc);
        assertThat(manager.getListingRef(),is(equalTo("testRef")));
        verify(mockDoc,times(1)).getId();
    }

    @Test
    public void testEditAdvertInDatabase()
    {
        ListingManager manager = dummyConstructor("testRef", "Venue", "testRef");
        FirebaseFirestore mockDb = mock(FirebaseFirestore.class);
        CollectionReference mockColl = mock(CollectionReference.class);
        Task mockTask = mock(Task.class);
        when(mockDb.runTransaction(any())).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(any())).thenReturn(mock(Task.class));
        when(mockTask.addOnFailureListener(any())).thenReturn(mock(Task.class));
        manager.setDb(mockDb);
        Drawable mockDrawable = mock(Drawable.class);
        CreateAdvertisement mockActivity = mock(CreateAdvertisement.class);
        manager.postDataToDatabase(listingData,mockDrawable,mockActivity);
        verify(mockDb,times(1)).runTransaction(any());
        assertThat(manager.getListingInfo(),is(equalTo(listingData)));
    }

    @Test
    public void testApplyEdit() throws FirebaseFirestoreException {
        ListingManager manager = dummyConstructor("testRef", "Venue", "testRef");
        manager.setListingInfo(listingData);
        Transaction mockTransaction = mock(Transaction.class);
        when(mockTransaction.update(any(),any())).thenReturn(mockTransaction);
        manager.applyEdit(mockTransaction);
        verify(mockTransaction,times(1)).get(any());
        verify(mockTransaction,times(listingData.size())).update(any(),anyString(),any());
    }

    @Test
    public void testEditAdvertisementOnCompleteNullImage()
    {
        ListingManager manager = dummyConstructor("testRef", "Venue", "testRef");
        CreateAdvertisement mockActivity = mock(CreateAdvertisement.class);
        manager.setActivity(mockActivity);
        manager.editAdvertisementOnComplete();
        verify(mockActivity,times(1)).handleDatabaseResponse(any());
    }

    @Test
    public void testEditAdvertisementOnCompleteValidImage()
    {
        ListingManager manager = dummyConstructor("testRef", "Venue", "testRef");
        Drawable mockImage = mock(Drawable.class);
        manager.setImage(mockImage);
        CreateAdvertisement mockActivity = mock(CreateAdvertisement.class);
        manager.setActivity(mockActivity);
        StorageReference mockListImage = mock(StorageReference.class);
        UploadTask mockUp = mock(UploadTask.class);
        when(mockListImage.putBytes(any())).thenReturn(mockUp);
        manager.setListingImage(mockListImage);
        manager.editAdvertisementOnComplete();
        verify(mockListImage,times(1)).putBytes(any());
        verify(mockUp,times(1)).addOnSuccessListener(any());
        verify(mockUp,times(1)).addOnFailureListener(any());
    }

    @Test
    public void testImageToByteArray()
    {
        ListingManager manager = dummyConstructor("testRef", "Venue", "testRef");
        Drawable mockImage = mock(Drawable.class);
        Bitmap mockBitmap = mock(Bitmap.class);
        when(mockBitmap.compress(any(),anyInt(),any())).thenReturn(true);
        manager.setBitmap(mockBitmap);
        manager.imageToByteArray(mockImage);
        verify(mockBitmap,times(1)).compress(any(),anyInt(),any());
    }

    @Test
    public void testImageToByteArrayNullImage()
    {
        ListingManager manager = dummyConstructor("testRef", "Venue", "testRef");
        Bitmap mockBitmap = mock(Bitmap.class);
        when(mockBitmap.compress(any(),anyInt(),any())).thenReturn(true);
        manager.setBitmap(mockBitmap);
        manager.imageToByteArray(null);
        verify(mockBitmap,times(0)).compress(any(),anyInt(),any());
    }

    @Test
    public void testGetExpiryDatePaymentNeeded()
    {
        ListingManager manager = dummyConstructor("testRef", "Venue", "testRef");
        Calendar testCalendar = Calendar.getInstance();
        manager.setCalendar(testCalendar);
        int expectedNumber = 2;
        manager.getCalendar().set(Calendar.DATE,expectedNumber);
        manager.getCalendar().set(Calendar.HOUR_OF_DAY,expectedNumber);
        manager.getCalendar().set(Calendar.MINUTE,expectedNumber);
        manager.getCalendar().set(Calendar.SECOND,expectedNumber);
        manager.getCalendar().set(Calendar.MILLISECOND,expectedNumber);
        manager.getExpiryDate();
        testCalendar = manager.getCalendar();
        assertThat(testCalendar.get(Calendar.HOUR_OF_DAY),is(equalTo(expectedNumber)));
        assertThat(testCalendar.get(Calendar.MINUTE),is(equalTo(expectedNumber)));
        assertThat(testCalendar.get(Calendar.SECOND),is(equalTo(expectedNumber)));
        assertThat(testCalendar.get(Calendar.MILLISECOND),is(equalTo(expectedNumber)));
        assertThat(testCalendar.get(Calendar.DATE),is(equalTo(expectedNumber - 1)));
    }

    @Test
    public void testGetExpiryNotMidnight()
    {
        ListingManager manager = dummyConstructor("testRef", "Band", "testRef");
        Calendar testCalendar = Calendar.getInstance();
        manager.setCalendar(testCalendar);
        int notMidnightNumber = 2;
        int expectedNumber = 0;
        manager.getCalendar().set(Calendar.HOUR_OF_DAY,notMidnightNumber);
        manager.getCalendar().set(Calendar.MINUTE,notMidnightNumber);
        manager.getCalendar().set(Calendar.SECOND,notMidnightNumber);
        manager.getCalendar().set(Calendar.MILLISECOND,notMidnightNumber);
        int expectedDate = manager.getCalendar().get(Calendar.DAY_OF_YEAR) + 32;
        manager.getExpiryDate();
        testCalendar = manager.getCalendar();
        assertThat(testCalendar.get(Calendar.HOUR_OF_DAY),is(equalTo(expectedNumber)));
        assertThat(testCalendar.get(Calendar.MINUTE),is(equalTo(expectedNumber)));
        assertThat(testCalendar.get(Calendar.SECOND),is(equalTo(expectedNumber)));
        assertThat(testCalendar.get(Calendar.MILLISECOND),is(equalTo(expectedNumber)));
        assertThat(testCalendar.get(Calendar.DAY_OF_YEAR),is(equalTo(expectedDate)));
    }

    @Test
    public void testGetExpiryIsMidnight()
    {
        ListingManager manager = dummyConstructor("testRef", "Band", "testRef");
        Calendar testCalendar = Calendar.getInstance();
        manager.setCalendar(testCalendar);
        int expectedNumber = 0;

        manager.getCalendar().set(Calendar.HOUR_OF_DAY,expectedNumber);
        manager.getCalendar().set(Calendar.MINUTE,expectedNumber);
        manager.getCalendar().set(Calendar.SECOND,expectedNumber);
        manager.getCalendar().set(Calendar.MILLISECOND,expectedNumber);
        int expectedDate = manager.getCalendar().get(Calendar.DAY_OF_YEAR) + 31;
        manager.getExpiryDate();
        testCalendar = manager.getCalendar();
        assertThat(testCalendar.get(Calendar.HOUR_OF_DAY),is(equalTo(expectedNumber)));
        assertThat(testCalendar.get(Calendar.MINUTE),is(equalTo(expectedNumber)));
        assertThat(testCalendar.get(Calendar.SECOND),is(equalTo(expectedNumber)));
        assertThat(testCalendar.get(Calendar.MILLISECOND),is(equalTo(expectedNumber)));
        assertThat(testCalendar.get(Calendar.DAY_OF_YEAR),is(equalTo(expectedDate)));
    }

    @Test
    public void testSetStorageRef()
    {
        ListingManager manager = dummyConstructor("testRef", "Band", "testRef");
        StorageReference mock = mock(StorageReference.class);
        manager.setStorageRef(mock);
        assertThat(manager.getStorageRef(),is(equalTo(mock)));
    }

    @Test
    public void testSetListRef()
    {
        ListingManager manager = dummyConstructor("testRef", "Band", "testRef");
        DocumentReference mock = mock(DocumentReference.class);
        manager.setListRef(mock);
        assertThat(manager.getListRef(),is(equalTo(mock)));
    }

    @Test
    public void testSetListingInfo()
    {
        ListingManager manager = dummyConstructor("testRef", "Band", "testRef");
        Map mock = mock(Map.class);
        manager.setListingInfo(mock);
        assertThat(manager.getListingInfo(),is(equalTo(mock)));
    }

    @Test
    public void testSetImageRef()
    {
        ListingManager manager = dummyConstructor("testRef", "Band", "testRef");
        StorageReference mock = mock(StorageReference.class);
        manager.setImageRef(mock);
        assertThat(manager.getImageRef(),is(equalTo(mock)));
    }

    @Test
    public void testIsNeedPayment()
    {
        ListingManager manager = dummyConstructor("testRef", "Band", "testRef");
        assertThat(manager.isNeedPayment(),is(equalTo(false)));
    }

    @Test
    public void testGetUSerInfoNullRef()
    {
        ListingManager manager = dummyConstructor("testRef", "Band", "testRef");
        CreateAdvertisement mockActivity = mock(CreateAdvertisement.class);
        manager.getUserInfo(mockActivity);
        verify(mockActivity,times(1)).onSuccessFromDatabase(any());
    }
}