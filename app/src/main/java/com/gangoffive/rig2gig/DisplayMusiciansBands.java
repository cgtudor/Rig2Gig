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
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class DisplayMusiciansBands extends Fragment
{
    private RecyclerView recyclerView;
    private MusiciansBandsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private String TAG = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@";

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final String USERID = fAuth.getUid();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference musiciansRef = db.collection("musicians");
    private List<DocumentSnapshot> musicians;

    private CollectionReference bandsRef = db.collection("bands");
    private List<DocumentSnapshot> bandsList;

    private ArrayList<MusiciansBands> musiciansBands;
    private ArrayList<String> bands;

    private String musicianId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_my_bands, container, false);

        musiciansBands = new ArrayList<>();

        Query first = musiciansRef;
        Query second = bandsRef;

        first.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            musicians = task.getResult().getDocuments();

                            if(!musicians.isEmpty())
                            {
                                Log.d(TAG, "get successful with data");

                                for(DocumentSnapshot musician : musicians)
                                {
                                    System.out.println(TAG + " databse user-ref " + musician.get("user-ref"));
                                    System.out.println(TAG + " local USERID " + USERID);
                                    if(musician.get("user-ref").equals(USERID))
                                    {
                                        musicianId = musician.getId();

                                        bands = (ArrayList<String>) musician.get("bands");

                                        System.out.println(TAG + " band size = " + bands.size());

                                        if(bands.size() > 0)
                                        {
                                            second.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                                            {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        bandsList = task.getResult().getDocuments();

                                                        System.out.println(TAG + " band List = " + bandsList.size());

                                                        if(!bandsList.isEmpty())
                                                        {
                                                            for(DocumentSnapshot bandsSnapshot : bandsList)
                                                            {
                                                                for(String b : bands)
                                                                {
                                                                    System.out.println(TAG + " database document: " + bandsSnapshot.getId() + " local document " + b);

                                                                    if(bandsSnapshot.getId().equals(b))
                                                                    {
                                                                        MusiciansBands band = new MusiciansBands(bandsSnapshot.getId());

                                                                        adapter = new MusiciansBandsAdapter(musiciansBands, getContext());

                                                                        adapter.setOnItemClickListener(new MusiciansBandsAdapter.OnItemClickListener()
                                                                        {
                                                                            @Override
                                                                            public void onItemClick(int position)
                                                                            {
                                                                                /*Intent openListingIntent = new Intent(v.getContext(), PerformanceListingDetailsActivity.class);
                                                                                String listingRef = musiciansBands.get(position).getreference();
                                                                                openListingIntent.putExtra("EXTRA_PERFORMANCE_LISTING_ID", listingRef);
                                                                                v.getContext().startActivity(openListingIntent);*/
                                                                            }
                                                                        });

                                                                        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
                                                                        recyclerView.setHasFixedSize(true);
                                                                        recyclerView.setAdapter(adapter);
                                                                        LinearLayoutManager llManager = new LinearLayoutManager(getContext());
                                                                        recyclerView.setLayoutManager(llManager);

                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        else
                                                        {
                                                            System.out.println(TAG + " Band list is empty");
                                                            //What do we do if bands are empty???
                                                        }
                                                    }
                                                    else
                                                    {
                                                        Log.d(TAG, "Task is successful with data.");
                                                    }
                                                }
                                            });
                                        }
                                        else
                                        {
                                            //What do we do if we have no bands to show???
                                        }

                                        break;
                                    }
                                    else
                                    {
                                        Log.d(TAG, "musician.get(\"user-ref\") does not match USERID");
                                    }
                                }
                            }
                            else
                            {
                                Log.d(TAG, "get successful without data");
                            }
                        }
                        else
                        {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

        return v;
    }
}
