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
import com.gangoffive.rig2gig.advert.details.MusicianListingDetailsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;

public class SavedMusiciansFragment extends Fragment
{
    private static final String TAG = "SavedPerformersFragment";

    private String currentBandRef;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentSnapshot lastVisible;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;
    private MusicianAdapter adapter;

    private ArrayList<MusicianListing> musicianListings;
    private final ArrayList<String> bandMembers = new ArrayList();
    private boolean callingFirebase = false;

    /**
     * Creates an fragment for an index of favourited musician adverts.
     * @param inflater The inflater is used to read the passed xml file.
     * @param container The views base class.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        final View v = inflater.inflate(R.layout.fragment_saved_musicians, container, false);

        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;



        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.savedSwipeContainer);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "get successful with data123213213");
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }

                lastVisible = null;

                ft.detach(SavedMusiciansFragment.this).attach(SavedMusiciansFragment.this).commit();
                swipeLayout.setRefreshing(false);
            }
        });
        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));


        currentBandRef = this.getArguments().getString("CURRENT_BAND_ID");
        DocumentReference bandRef = db.collection("bands").document(currentBandRef);
        bandRef.get(source)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "Band Query Get successful with data");
                                ArrayList<String> members = (ArrayList<String>) document.get("members");
                                for(String member : members) {
                                    bandMembers.add(member);
                                }
                            } else {
                                Log.d(TAG, "Band Query Get successful without data");
                            }
                        } else {
                            Log.d(TAG, "Band Query Get unsuccessful");
                        }
                    }
                });
        /*while(bandMembers.isEmpty()) {

        }
*/
        recyclerView = (RecyclerView) v.findViewById(R.id.savedRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        musicianListings = new ArrayList<>();
        adapter = new MusicianAdapter(musicianListings, getContext());

        adapter.setOnItemClickListener(new MusicianAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent openListingIntent = new Intent(v.getContext(), MusicianListingDetailsActivity.class);
                String listingRef = musicianListings.get(position).getListingRef();
                openListingIntent.putExtra("EXTRA_MUSICIAN_LISTING_ID", listingRef);
                openListingIntent.putExtra("CURRENT_BAND_ID", currentBandRef);
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

    /**
     * Calls the Firebase database for more adverts, has built in pagenation.
     * @param source
     */
    private void firebaseCall(Source source) {

        callingFirebase = true;

        Query next;
        String uID = FirebaseAuth.getInstance().getUid();
        Timestamp currentDate = Timestamp.now();

        if(lastVisible == null) {
            next = db.collection("favourite-ads").document(uID).collection("musician-listings")
                    .whereGreaterThanOrEqualTo("expiry-date",  currentDate)
                    .limit(10);
        } else {
            next = db.collection("favourite-ads").document(uID).collection("musician-listings")
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
                                    ArrayList<String> positions = (ArrayList<String>) documentSnapshot.get("position");
                                    MusicianListing musicianListing = new MusicianListing(
                                            documentSnapshot.getId(),
                                            documentSnapshot.get("musician-ref").toString(),
                                            positions);

                                    musicianListings.add(musicianListing);

                                    lastVisible = documentSnapshot;
                                }

                                adapter.notifyItemInserted(musicianListings.size() - 1);

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

    /**
     * After this fragment opens the associated advert activity, when it returns it forces the activity
     * to restart to allow the favourited adverts to updated.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "get successful with data123213213");
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }

        lastVisible = null;

        ft.detach(SavedMusiciansFragment.this).attach(SavedMusiciansFragment.this).commit();
    }
}
