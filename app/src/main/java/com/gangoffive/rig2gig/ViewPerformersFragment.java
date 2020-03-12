package com.gangoffive.rig2gig;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;



public class ViewPerformersFragment extends Fragment
{
    private String TAG = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@";

    private FirebaseFirestore db;
    private CollectionReference colRef;
    private List<DocumentSnapshot> documentSnapshots;

    private RecyclerView recyclerView;
    private PerformerAdapter adapter;

    private ArrayList<PerformerListing> performerListings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View v = inflater.inflate(R.layout.fragment_view_performers, container, false);

        setHasOptionsMenu(true);

        db = FirebaseFirestore.getInstance();
        colRef = db.collection("performer-listings");

        performerListings = new ArrayList<>();

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.test, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch(menuItem.getItemId())
        {
            case R.id.favourite_icon:
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SavedPerformersFragment()).commit();
                break;
            default:
                break;
        }

        return true;
    }
}
