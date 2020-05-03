package com.gangoffive.rig2gig.band.management;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.navbar.BandConsoleActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to display the musician's bands on a Card View.
 */
public class DisplayMusiciansBands extends Fragment
{
    private RecyclerView recyclerView;
    private MusiciansBandsAdapter adapter;

    private String TAG = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@";

    private final FirebaseAuth FAUTH = FirebaseAuth.getInstance();
    private final String USERID = FAUTH.getUid();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference MUSICIANSREF = db.collection("musicians");
    private List<DocumentSnapshot> musicians;

    private final CollectionReference BANDREF = db.collection("bands");
    private List<DocumentSnapshot> bandsList;

    private ArrayList<MusiciansBands> musiciansBands;
    private ArrayList<String> bands;

    private String musicianId;

    /**
     * This method is used to create the view of the page upon loading the class.
     * @param inflater The inflater is used to read the passed xml file.
     * @param container The views base class.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     * @return Returns a View of the fragment_my_bands layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_my_bands, container, false);

        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        musiciansBands = new ArrayList<>();

        Query first = MUSICIANSREF;

        first.whereEqualTo("user-ref", USERID).get(source).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            /**
             * This method is used to determine the completion of a get request of Firebase.
             * @param task References the result of the get request.
             */
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if(task.isSuccessful())
                {
                    musicians = task.getResult().getDocuments();

                    if(!musicians.isEmpty())
                    {
                        Log.d(TAG, "get successful with data");

                        DocumentSnapshot musician = musicians.get(0);

                        musicianId = musician.getId();

                        bands = (ArrayList<String>) musician.get("bands");

                        //System.out.println(TAG + " band size = " + bands.size());

                        if(bands != null && bands.size() > 0)
                        {
                            for(String b : bands)
                            {
                                BANDREF.document(b).get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                                {
                                    /**
                                     * This method is used to determine the completion of a get request of Firebase.
                                     * @param task References the result of the get request.
                                     */
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            DocumentSnapshot bandSnapshot = task.getResult();

                                            System.out.println(TAG + " database document: " + bandSnapshot.getId() + " local document " + b);

                                            MusiciansBands band = new MusiciansBands(bandSnapshot.getId(), bandSnapshot.get("name").toString());
                                            musiciansBands.add(band);

                                            adapter = new MusiciansBandsAdapter(musiciansBands, getContext());

                                            adapter.setOnItemClickListener(new MusiciansBandsAdapter.OnItemClickListener()
                                            {
                                                @Override
                                                public void onItemClick(int position)
                                                {
                                                    //Uncomment following when the fragment/activity to view a band's details has been created.
                                                    Intent selectedBand = new Intent(view.getContext(), BandConsoleActivity.class);
                                                    String bandReference = musiciansBands.get(position).getReference();
                                                    String bandName = musiciansBands.get(position).getBandName();
                                                    selectedBand.putExtra("EXTRA_SELECTED_BAND_ID", bandReference);
                                                    selectedBand.putExtra("EXTRA_SELECTED_BAND_NAME", bandName);
                                                    view.getContext().startActivity(selectedBand);
                                                }
                                            });

                                            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
                                            recyclerView.setHasFixedSize(true);
                                            recyclerView.setAdapter(adapter);
                                            LinearLayoutManager llManager = new LinearLayoutManager(getContext());
                                            recyclerView.setLayoutManager(llManager);

                                        }
                                        else
                                        {

                                        }
                                    }
                                });
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

        return view;
    }
}
