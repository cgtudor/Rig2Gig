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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewVenuesFragment extends Fragment
{
    private String TAG = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@";

    private FirebaseFirestore db;
    private CollectionReference colRef;
    private List<DocumentSnapshot> documentSnapshots;

    private RecyclerView recyclerView;
    private VenueAdapter adapter;

    private ArrayList<VenueListing> venueListings;

    /**
     * Upon creation of the ViewVenuesFragment, create the fragment_view_venues layout.
     * @param inflater The inflater is used to read the passed xml file.
     * @param container The views base class.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     * @return Returns a View of the fragment_upgrade_to_musicians layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View v = inflater.inflate(R.layout.fragment_view_venues, container, false);

        db = FirebaseFirestore.getInstance();
        colRef = db.collection("venue-listings");

        venueListings = new ArrayList<>();

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

                                    VenueListing venueListing = new VenueListing(
                                            documentSnapshot.getId(),
                                            documentSnapshot.get("venue-ref").toString());

                                    venueListings.add(venueListing);
                                }

                                adapter = new VenueAdapter(venueListings, getContext());

                                adapter.setOnItemClickListener(new VenueAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        Intent openListingIntent = new Intent(v.getContext(), VenueListingDetailsActivity.class);
                                        String listingRef = venueListings.get(position).getListingRef();
                                        openListingIntent.putExtra("EXTRA_VENUE_LISTING_ID", listingRef);
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
}
