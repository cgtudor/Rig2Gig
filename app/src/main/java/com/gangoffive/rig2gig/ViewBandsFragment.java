package com.gangoffive.rig2gig;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;



public class ViewBandsFragment extends Fragment
{
    private String TAG = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@";

    private FirebaseFirestore db;
    private CollectionReference colRef;
    private List<DocumentSnapshot> documentSnapshots;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private List<PerformerListing> performerListings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_view_bands, container, false);

        db = FirebaseFirestore.getInstance();
        colRef = db.collection("performer-listings");

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        performerListings = new ArrayList<>();

        Query first = colRef
                .orderBy("name")
                .limit(10);

        /*first.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        documentSnapshots= querySnapshot.getDocuments();

                        for(DocumentSnapshot documentSnapshot: documentSnapshots){
                            PerformerListing performerListing = new PerformerListing(
                                             documentSnapshot.getId(),
                                    (String) documentSnapshot.get("name"),
                                    (String) documentSnapshot.get("genres"),
                                    (String) documentSnapshot.get("location")
                            );

                            performerListings.add(performerListing);
                        }
                    }
                });*/

        first.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            documentSnapshots = task.getResult().getDocuments();
                            if(!documentSnapshots.isEmpty())
                            {
                                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                                    PerformerListing performerListing = new PerformerListing(
                                            documentSnapshot.getId(),
                                            (String) documentSnapshot.get("name"),
                                            (String) documentSnapshot.get("genres"),
                                            (String) documentSnapshot.get("location")
                                            //rating
                                    );

                                    performerListings.add(performerListing);
                            }
                                adapter = new MyAdapter(performerListings, getContext());

                                recyclerView.setAdapter(adapter);

                                Log.d(TAG, "get successful with data");
                            } else {
                                Log.d(TAG, "get successful without data");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });



        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
