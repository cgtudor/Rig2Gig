package com.gangoffive.rig2gig;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class SavedPerformersFragment extends Fragment implements DefaultGoBack
{
    private String TAG = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@";

    SwipeRefreshLayout swipeLayout;

    private FirebaseFirestore db;
    private CollectionReference colRef;
    private DocumentReference docRef;
    private List<DocumentSnapshot> documentSnapshots;

    private RecyclerView recyclerView;
    private PerformerAdapter adapter;

    private ArrayList<PerformerListing> performerListings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View v = inflater.inflate(R.layout.fragment_saved_performers, container, false);

        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "get successful with data123213213");
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(SavedPerformersFragment.this).attach(SavedPerformersFragment.this).commit();
                swipeLayout.setRefreshing(false);
            }
        });
        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));

        String uID = FirebaseAuth.getInstance().getUid();

        db = FirebaseFirestore.getInstance();
        colRef = db.collection("favourite-ads").document(uID).collection("performer-listings");

        performerListings = new ArrayList<>();

        Timestamp currentDate = Timestamp.now();

        Query first = colRef
                .whereGreaterThanOrEqualTo("expiry-date",  currentDate)
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

                                    PerformerListing performerListing = new PerformerListing(
                                            documentSnapshot.getId(),
                                            documentSnapshot.get("performer-ref").toString(),
                                            documentSnapshot.get("performer-type").toString());

                                    performerListings.add(performerListing);
                                }

                                adapter = new PerformerAdapter(performerListings, getContext());

                                adapter.setOnItemClickListener(new PerformerAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        Intent openListingIntent = new Intent(v.getContext(), PerformanceListingDetailsActivity.class);
                                        String listingRef = performerListings.get(position).getListingRef();
                                        openListingIntent.putExtra("EXTRA_PERFORMANCE_LISTING_ID", listingRef);
                                        startActivityForResult(openListingIntent, 1);
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

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "get successful with data123213213");
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(SavedPerformersFragment.this).attach(SavedPerformersFragment.this).commit();
    }
}
