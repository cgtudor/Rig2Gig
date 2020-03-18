package com.gangoffive.rig2gig;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ViewCommsFragment extends Fragment
{
    private String TAG = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@";

    SwipeRefreshLayout swipeLayout;

    private FirebaseFirestore db;
    private CollectionReference colRef;
    private List<DocumentSnapshot> documentSnapshots;

    private RecyclerView recyclerView;
    private CommsAdapter adapter;

    private ArrayList<Communication> communications;

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
        final View v = inflater.inflate(R.layout.fragment_view_comms, container, false);

        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "get successful with data123213213");
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(ViewCommsFragment.this).attach(ViewCommsFragment.this).commit();
                swipeLayout.setRefreshing(false);
            }
        });
        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));

        String uID = FirebaseAuth.getInstance().getUid();

        db = FirebaseFirestore.getInstance();
        colRef = db.collection("communications").document(uID).collection("received");

        communications = new ArrayList<>();

        Query first = colRef
                .limit(10)
                .orderBy("posting-date", Query.Direction.ASCENDING);

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

                                    Communication communication;
                                    if(documentSnapshot.get("type").toString().equals("join-request"))
                                    {
                                        communication = new Communication(
                                                documentSnapshot.getId(),
                                                documentSnapshot.get("sent-from").toString(),
                                                documentSnapshot.get("type").toString(),
                                                documentSnapshot.get("band-ref").toString(),
                                                documentSnapshot.get("musician-ref").toString());
                                    }
                                    else
                                    {
                                        communication = new Communication(
                                                documentSnapshot.getId(),
                                                documentSnapshot.get("sent-from").toString(),
                                                documentSnapshot.get("type").toString());
                                    }
                                    if (!communication.getCommType().equals("accepted-invite")
                                            && !communication.getCommType().equals("rejected-invite")
                                            && !communication.getCommType().equals("left-band"))
                                    {
                                        communications.add(communication);
                                    }
                                }

                                adapter = new CommsAdapter(communications, getContext());

                                adapter.setOnItemClickListener(new CommsAdapter.OnItemClickListener() {
                                    /*@Override
                                    public void onItemClick(int position) {
                                        Intent openListingIntent = new Intent(v.getContext(), VenueListingDetailsActivity.class);
                                        String listingRef = communications.get(position).getListingRef();
                                        openListingIntent.putExtra("EXTRA_VENUE_LISTING_ID", listingRef);
                                        v.getContext().startActivity(openListingIntent);
                                    }*/
                                    /*@Override
                                    public void onPhotoClick(int position) {
                                        //
                                    }
                                    @Override
                                    public void onNameClick(int position) {
                                        //
                                    }*/
                                    @Override
                                    public void onTopButtonClick(int position) {

                                        switch (communications.get(position).getCommType())
                                        {
                                            case "contact-request":
                                                DocumentReference updateRef = db.collection("communications")
                                                        .document(uID)
                                                        .collection("received")
                                                        .document(communications.get(position).getCommRef());

                                                updateRef.update("type" , "contact-send")
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    Log.d("FIRESTORE", "Contact request update successful");
                                                                }
                                                                else
                                                                {
                                                                    Log.d("FIRESTORE", "Contact request update failed: " + task.getException());
                                                                }
                                                            }
                                                        });

                                                HashMap<String, Object> request = new HashMap<>();
                                                request.put("type", "contact-accept");
                                                request.put("posting-date", Timestamp.now());
                                                request.put("sent-from", FirebaseAuth.getInstance().getUid());

                                                CollectionReference received = db.collection("communications")
                                                        .document(communications.get(position).getUserRef())
                                                        .collection("received");

                                                received.add(request)
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    Log.d("FIRESTORE", "Contact request added with info " + task.getResult().toString());
                                                                    Toast.makeText(getActivity(), "Contact request accepted!", Toast.LENGTH_SHORT).show();

                                                                    communications.get(position).setCommType("contact-send");
                                                                    adapter.notifyItemChanged(position);
                                                                }
                                                                else
                                                                {
                                                                    Log.d("FIRESTORE", "Contact request failed with ", task.getException());
                                                                }
                                                            }
                                                        });

                                                HashMap<String, Object> requestSent = new HashMap<>();
                                                requestSent.put("type", "contact-send");
                                                requestSent.put("posting-date", Timestamp.now());
                                                requestSent.put("sent-to", communications.get(position).getUserRef());
                                                CollectionReference sent = db.collection("communications")
                                                        .document(FirebaseAuth.getInstance().getUid())
                                                        .collection("sent");

                                                sent.add(requestSent)
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    Log.d("FIRESTORE", "Contact request sent with info " + task.getResult().toString());
                                                                }
                                                                else
                                                                {
                                                                    Log.d("FIRESTORE", "Contact request sending failed with ", task.getException());
                                                                }
                                                            }
                                                        });
                                                break;
                                            case "contact-accept":
                                            case "contact-send":

                                                DocumentReference userDetails = db.collection("users")
                                                        .document(communications.get(position).getUserRef());

                                                userDetails.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    DocumentSnapshot userDoc = task.getResult();
                                                                    if(userDoc.exists())
                                                                    {
                                                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                                                        intent.setData(Uri.parse("tel:" + userDoc.get("phone-number")));
                                                                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                                                                            startActivity(intent);
                                                                    }
                                                                    else
                                                                    {
                                                                        Log.d(TAG, "User not found");
                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    Log.e(TAG, "Get user failed");
                                                                }
                                                            }
                                                        }});
                                                break;
                                            case "join-request":
                                                handleJoinBand(position, uID);
                                                break;
                                            default:
                                                //
                                        }

                                    }

                                    @Override
                                    public void onBotButtonClick(int position) {

                                        switch (communications.get(position).getCommType())
                                        {
                                            case "contact-request":
                                                DocumentReference updateRef = db.collection("communications")
                                                        .document(uID)
                                                        .collection("received")
                                                        .document(communications.get(position).getCommRef());

                                                updateRef.update("type" , "contact-retain")
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    Log.d("FIRESTORE", "Contact request update successful");
                                                                }
                                                                else
                                                                {
                                                                    Log.d("FIRESTORE", "Contact request update failed with" + task.getException().toString());
                                                                }
                                                            }
                                                        });

                                                HashMap<String, Object> request = new HashMap<>();
                                                request.put("type", "contact-decline");
                                                request.put("posting-date", Timestamp.now());
                                                request.put("sent-from", FirebaseAuth.getInstance().getUid());

                                                CollectionReference received = db.collection("communications")
                                                        .document(communications.get(position).getUserRef())
                                                        .collection("received");

                                                received.add(request)
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    Log.d("FIRESTORE", "Contact request added with info " + task.getResult().toString());
                                                                    Toast.makeText(getActivity(), "Contact request denied!", Toast.LENGTH_SHORT).show();

                                                                    communications.get(position).setCommType("contact-retain");
                                                                    adapter.notifyItemChanged(position);
                                                                }
                                                                else
                                                                {
                                                                    Log.d("FIRESTORE", "Contact request failed with ", task.getException());
                                                                }
                                                            }
                                                        });

                                                HashMap<String, Object> requestSent = new HashMap<>();
                                                requestSent.put("type", "contact-retain");
                                                requestSent.put("posting-date", Timestamp.now());
                                                requestSent.put("sent-to", communications.get(position).getUserRef());
                                                CollectionReference sent = db.collection("communications")
                                                        .document(FirebaseAuth.getInstance().getUid())
                                                        .collection("sent");

                                                sent.add(requestSent)
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    Log.d("FIRESTORE", "Contact request sent with info " + task.getResult().toString());
                                                                }
                                                                else
                                                                {
                                                                    Log.d("FIRESTORE", "Contact request sending failed with ", task.getException());
                                                                }
                                                            }
                                                        });
                                                break;
                                            case "contact-accept":
                                            case "contact-send":

                                                DocumentReference userDetails = db.collection("users")
                                                        .document(communications.get(position).getUserRef());

                                                userDetails.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    DocumentSnapshot userDoc = task.getResult();
                                                                    if(userDoc.exists())
                                                                    {
                                                                        Intent email = new Intent(Intent.ACTION_SEND);
                                                                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ userDoc.get("email-address").toString()});
                                                                        email.putExtra(Intent.EXTRA_SUBJECT, "We are interested in your advert!");
                                                                        email.putExtra(Intent.EXTRA_TEXT, "We found you on Rig2Gig and you look like the best fit for us!");

                                                                        email.setType("message/rfc822");

                                                                        startActivity(Intent.createChooser(email, "Choose an Email client :"));
                                                                    }
                                                                    else
                                                                    {
                                                                        Log.d(TAG, "User not found");
                                                                    }
                                                                }
                                                                else
                                                                {
                                                                    Log.e(TAG, "Get user failed");
                                                                }
                                                            }
                                                        });
                                                break;
                                            case "join-request":
                                                handleNotJoinBand(position, uID);
                                                break;
                                            default:
                                                //
                                        }
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

    public void handleJoinBand(int position, String uID)
    {
        String bandRef = communications.get(position).getBandRef();
        String musicianRef = communications.get(position).getMusicianRef();
        DocumentReference receiverCommDoc = db.collection("communications")
                .document(uID)
                .collection("received")
                .document(communications.get(position).getCommRef());
        receiverCommDoc.update("type" , "accepted-invite")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    //on successfully updating invite comm document with accepted-invite
                    //attempt to get band document
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d("FIRESTORE", "Band invite update successful");
                              updateBandMembers(bandRef, musicianRef);
                        }
                        else {
                            Log.d("FIRESTORE", "Join band update failed: " + task.getException());
                        }
                }});
    }

    public void updateBandMembers(String bandRef, String musicianRef)
    {
        DocumentReference bandDoc = db.collection("bands")
                .document(bandRef);
        bandDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> bandInfo = document.getData();
                        ((List)bandInfo.get("members")).add(musicianRef);
                        db.runTransaction(new Transaction.Function<Void>() {
                            @Override
                            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                DocumentSnapshot snapshot = transaction.get(bandDoc);
                                for (String key : bandInfo.keySet())
                                {
                                    transaction.update(bandDoc, key, bandInfo.get(key));
                                }
                                return null;
                            }})
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("FIRESTORE", "Musician added to band members list successfully!");
                                        updateMusiciansBands(bandRef, musicianRef);
                                    }});
                }}
                else {
                    Log.w("FIRESTORE", "Error adding musician to band!");
                }}});
    }

    public void updateMusiciansBands(String bandRef, String musicianRef)
    {
        DocumentReference musicianDoc = db.collection("musicians")
                .document(musicianRef);
        musicianDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            //on successfully obtaining musician document
            //attempt to add band to bands list
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> musicianInfo = document.getData();
                        ((List) musicianInfo.get("bands")).add(bandRef);
                        db.runTransaction(new Transaction.Function<Void>() {
                            @Override
                            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                DocumentSnapshot snapshot = transaction.get(musicianDoc);
                                for (String key : musicianInfo.keySet()) {
                                    transaction.update(musicianDoc, key, musicianInfo.get(key));
                                }
                                return null;
                            }})
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("FIRESTORE", "Band added to musician's band list successfully!");
                                        Toast.makeText(getActivity(), "Band joined!", Toast.LENGTH_SHORT).show();
                                        refreshScreen();
                                    }});
                    }}
                else {
                    Log.w("FIRESTORE", "Error adding band to musician's band list!");
                }}});
    }

    public void handleNotJoinBand(int position, String uID)
    {
        String bandRef = communications.get(position).getBandRef();
        String musicianRef = communications.get(position).getMusicianRef();
        DocumentReference receiverCommDoc = db.collection("communications")
                .document(uID)
                .collection("received")
                .document(communications.get(position).getCommRef());
        receiverCommDoc.update("type" , "rejected-invite")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    //on successfully updating invite comm document with accepted-invite
                    //attempt to get band document
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d("FIRESTORE", "Band invite rejection successful");
                            Toast.makeText(getActivity(), "Band not joined!", Toast.LENGTH_SHORT).show();
                            refreshScreen();
                        }
                        else {
                            Log.d("FIRESTORE", "Band rejection update failed: " + task.getException());
                        }
                    }});
    }

    public void refreshScreen()
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(ViewCommsFragment.this).attach(ViewCommsFragment.this).commit();
        swipeLayout.setRefreshing(false);
    }
}
