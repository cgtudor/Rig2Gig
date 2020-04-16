package com.gangoffive.rig2gig.advert.index;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.advert.details.VenueListingDetailsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;

public class SavedVenuesFragment extends Fragment
{
    private static final String TAG = "SavedVenuesFragment";

    private String currentUserType;
    private String currentBandId;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentSnapshot lastVisible;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;
    private VenueAdapter adapter;

    private ArrayList<VenueListing> venueListings;
    private boolean callingFirebase = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View v = inflater.inflate(R.layout.fragment_saved_venues, container, false);

        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "get successful with data123213213");
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }

                lastVisible = null;

                ft.detach(SavedVenuesFragment.this).attach(SavedVenuesFragment.this).commit();
                swipeLayout.setRefreshing(false);
            }
        });
        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        venueListings = new ArrayList<>();
        adapter = new VenueAdapter(venueListings, getContext());

        currentUserType = this.getArguments().getString("CURRENT_USER_TYPE");

        Bundle extras = this.getArguments();
        if(extras != null) {
            if(extras.containsKey("CURRENT_BAND_ID")) {
                currentBandId = extras.getString("CURRENT_BAND_ID");
            }
        }

        adapter.setOnItemClickListener(new VenueAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent openListingIntent = new Intent(v.getContext(), VenueListingDetailsActivity.class);
                String listingRef = venueListings.get(position).getListingRef();
                openListingIntent.putExtra("EXTRA_VENUE_LISTING_ID", listingRef);
                startActivityForResult(openListingIntent, 1);
            }
        });
        recyclerView.setAdapter(adapter);
        firebaseCall(source);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(callingFirebase == false) {
                    super.onScrollStateChanged(recyclerView, newState);

                    int offset = recyclerView.computeVerticalScrollOffset();
                    int extent = recyclerView.computeVerticalScrollExtent();
                    int range = recyclerView.computeVerticalScrollRange();

                    float percentage = (100.0f * offset / (float)(range - extent));

                    if(percentage > 75) {
                        firebaseCall(source);
                    }
                }
            }
        });

        return v;
    }

    private void firebaseCall(Source source) {

        callingFirebase = true;

        Query next;
        String uID = FirebaseAuth.getInstance().getUid();
        Timestamp currentDate = Timestamp.now();

        if(lastVisible == null) {
            next = db.collection("favourite-ads").document(uID).collection("venue-listings")
                    .whereGreaterThanOrEqualTo("expiry-date",  currentDate)
                    .limit(10);
        } else {
            next = db.collection("favourite-ads").document(uID).collection("venue-listings")
                    .whereGreaterThanOrEqualTo("expiry-date",  currentDate)
                    .startAfter(lastVisible)
                    .limit(10);
        }

        next.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            if(!documentSnapshots.isEmpty())
                            {
                                Log.d(TAG, "get successful with data");

                                for(DocumentSnapshot documentSnapshot : documentSnapshots){

                                    VenueListing venueListing = new VenueListing(
                                            documentSnapshot.getId(),
                                            documentSnapshot.get("venue-ref").toString());

                                    venueListings.add(venueListing);
                                    lastVisible = documentSnapshot;
                                }

                                adapter.notifyItemInserted(venueListings.size() - 1);

                                callingFirebase = false;
                            } else {
                                Log.d(TAG, "get successful without data");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "get successful with data123213213");
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }

        lastVisible = null;

        ft.detach(SavedVenuesFragment.this).attach(SavedVenuesFragment.this).commit();
    }
}
