package com.gangoffive.rig2gig;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Observable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
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
        listingData.put("data","testData");
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
        Drawable mockImage = mock(Drawable.class);
        ListingManager manager = dummyConstructor("testRef", "Venue", "");
        DocumentReference mockDoc = mock(DocumentReference.class);
        when(mockDoc.getId()).thenReturn("testRef");
        StorageReference parentRef = mock(StorageReference.class);
        
        manager.setListingImage(parentRef);
        manager.setImage(mockImage);
        manager.createAdvertisementOnComplete(mockDoc);
        assertThat(manager.getListingRef(),is(equalTo("testRef")));
        verify(mockDoc,times(1)).getId();
        verify(parentRef,times(1)).putBytes(any());
    }


}