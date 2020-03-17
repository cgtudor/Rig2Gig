package com.gangoffive.rig2gig;

import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SavedMusiciansFragment extends Fragment
{
    private String TAG = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@";

    private FirebaseFirestore db;
    private CollectionReference colRef;
    private List<DocumentSnapshot> documentSnapshots;

    private RecyclerView recyclerView;
    private MusicianAdapter adapter;

    private ArrayList<MusicianListing> musicianListings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View v = inflater.inflate(R.layout.fragment_saved_performers, container, false);

        String uID = FirebaseAuth.getInstance().getUid();

        db = FirebaseFirestore.getInstance();
        colRef = db.collection("favourite-ads").document("uID").collection("musician-listings");

        musicianListings = new ArrayList<>();

        Query first = colRef
                .limit(10);

        first.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            documentSnapshots = task.getResult().getDocuments();
                            if(!documentSnapshots.isEmpty())
                            {
                                Log.d(TAG, "get successful with data");

                                for(DocumentSnapshot documentSnapshot : documentSnapshots){
                                    ArrayList<String> positions = (ArrayList<String>) documentSnapshot.get("position");
                                    MusicianListing musicianListing = new MusicianListing(
                                            documentSnapshot.getId(),
                                            documentSnapshot.get("musician-ref").toString(),
                                            positions);

                                    musicianListings.add(musicianListing);
                                }

                                adapter = new MusicianAdapter(musicianListings, getContext());

                                adapter.setOnItemClickListener(new MusicianAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        Intent openListingIntent = new Intent(v.getContext(), MusicianListingDetailsActivity.class);
                                        String listingRef = musicianListings.get(position).getListingRef();
                                        openListingIntent.putExtra("EXTRA_MUSICIAN_LISTING_ID", listingRef);
                                        v.getContext().startActivity(openListingIntent);
                                    }
                                });

                                recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setAdapter(adapter);
                                LinearLayoutManager llManager = new LinearLayoutManager(getContext());
                                recyclerView.setLayoutManager(llManager);

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
