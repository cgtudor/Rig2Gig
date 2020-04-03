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
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gangoffive.rig2gig.advert.management.PerformerAdvertisementEditor;
import com.gangoffive.rig2gig.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

public class MyBandFragment extends Fragment
{
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    String UUID = fAuth.getUid();

    //String UUID = "TvuDGJwqX13vJ6LWZYB2";
    //String UUID = "11111";
    String bandRef;

    TextView bandLocationTxt, bandDistanceTxt, bandGenresTxt, bandEmailTxt, bandPhoneNoTxt,
            bandName, bandLocation, distance, genres, email, phoneNo;
    Button createBtn, createBandBtn;

    public void setInputReferences(View view){
        bandLocationTxt = view.findViewById(R.id.bandLocation);
        bandDistanceTxt = view.findViewById(R.id.venue_description_final);
        bandGenresTxt = view.findViewById(R.id.Genres);
        bandEmailTxt = view.findViewById(R.id.Email);
        bandPhoneNoTxt = view.findViewById(R.id.phoneNo);

        bandName = view.findViewById(R.id.myBandName);
        bandLocation = view.findViewById(R.id.myBandLocation);
        distance = view.findViewById(R.id.myDistance);
        genres = view.findViewById(R.id.myBandGenres);
        email = view.findViewById(R.id.myBandEmail);
        phoneNo = view.findViewById(R.id.myPhoneNo);

        createBtn = view.findViewById(R.id.createBandAdBtn);
        createBandBtn = view.findViewById(R.id.createBandBtn);

    }

    /**
     * Upon creation of the MyBandFragment, create the fragment_my_band layout.
     * @param inflater The inflater is used to read the passed xml file.
     * @param container The views base class.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     * @return Returns a View of the fragment_about layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        System.out.println("=================== " + UUID);
        View view = inflater.inflate(R.layout.fragment_my_band, container, false);

        bandLocationTxt = view.findViewById(R.id.bandLocation);

        setInputReferences(view);

        createBandBtn.setVisibility(view.INVISIBLE);

        DocumentReference user = fStore.collection("users").document(UUID);
        user.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    System.out.println("============================= 1");
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        System.out.println("============================= 2");
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());
                        bandRef = document.get("band-ref").toString().trim();
                        DocumentReference bandInfo = fStore.collection("bands").document(bandRef);
                        bandInfo.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    System.out.println("============================= 3");
                                    DocumentSnapshot snap = task.getResult();
                                    if (snap.exists()){
                                        System.out.println("============================= 4");
                                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());
                                        System.out.println("============================= " + UUID);
                                        bandName.setText(snap.get("name").toString());
                                        bandLocation.setText(snap.get("location").toString());
                                        distance.setText(snap.get("distance").toString());
                                        genres.setText(snap.get("genres").toString());
                                        email.setText(snap.get("email").toString());
                                        phoneNo.setText(snap.get("phone-number").toString());

                                    }else{
                                        System.out.println("========================== User not part of a band!");
                                    }
                                }
                            }
                        });
                    } else{
                        System.out.println("========================== UUID: " + UUID + " not part of a band!");
                        bandName.setText("User Not In A Band!");
                        bandLocationTxt.setVisibility(view.INVISIBLE);
                        bandDistanceTxt.setVisibility(view.INVISIBLE);
                        bandGenresTxt.setVisibility(view.INVISIBLE);
                        bandEmailTxt.setVisibility(view.INVISIBLE);
                        bandPhoneNoTxt.setVisibility(view.INVISIBLE);
                        createBtn.setVisibility(view.INVISIBLE);
                        createBandBtn.setVisibility(view.VISIBLE);


                    }
                }
            }
        });

        createBandBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), CreateBandActivity.class);
                startActivity(intent);
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), PerformerAdvertisementEditor.class);
                intent.putExtra("EXTRA_BAND_ID", bandRef);
                intent.putExtra("EXTRA_LISTING_ID", "");
                intent.putExtra("EXTRA_PERFORMER_TYPE", "Band");
                startActivity(intent);
            }
        });

        return view;
    }
}
