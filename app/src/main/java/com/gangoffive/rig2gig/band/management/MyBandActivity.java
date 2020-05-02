package com.gangoffive.rig2gig.band.management;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gangoffive.rig2gig.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.List;

public class MyBandActivity extends AppCompatActivity {

    private static final String TAG = "==================";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    CollectionReference colRef;
    private HashMap<String, Object> band;
    String bandRef;
    TextView bandLocationTxt, bandDistanceTxt, bandGenresTxt, bandEmailTxt, bandPhoneNoTxt,
            bandName, bandLocation, distance, genres, email, phoneNo;
    String test;
    String getUUID;
    private List<DocumentSnapshot> documentSnapshots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_band);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Source source = isConnected ? Source.SERVER : Source.CACHE;

        String UUID = fAuth.getUid();
        //String UUID = "11111";

        bandLocationTxt = findViewById(R.id.bandLocation);
        bandDistanceTxt = findViewById(R.id.venue_description_final);
        bandGenresTxt = findViewById(R.id.Genres);
        bandEmailTxt = findViewById(R.id.Email);
        bandPhoneNoTxt = findViewById(R.id.phoneNo);


        bandName = findViewById(R.id.myBandName);
        bandLocation = findViewById(R.id.myBandLocation);
        distance = findViewById(R.id.myDistance);
        genres = findViewById(R.id.myBandGenres);
        email = findViewById(R.id.myBandEmail);
        phoneNo = findViewById(R.id.myPhoneNo);

        DocumentReference user = fStore.collection("users").document(UUID);
        user.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());
                        bandRef = document.get("band-ref").toString();
                        DocumentReference bandInfo = fStore.collection("band").document(bandRef);
                        bandInfo.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot snap = task.getResult();
                                    if (snap.exists()){
                                        Log.d("FIRESTORE", "DocumentSnapshot data: " + document.getData());

                                        bandName.setText(snap.get("Band Name").toString());
                                        bandLocation.setText(snap.get("Band Location").toString());
                                        distance.setText(snap.get("Distance").toString());
                                        genres.setText(snap.get("Genres").toString());
                                        email.setText(snap.get("Band Email Address").toString());
                                        phoneNo.setText(snap.get("Band Phone Number").toString());
                                    }else{
                                        System.out.println("========================== User not part of a band!");
                                    }
                                }
                            }
                        });
                    } else{
                        System.out.println("========================== UUID: " + UUID + " not part of a band!");
                        bandLocationTxt.setText("User Not In A Band");
                        bandDistanceTxt.setVisibility(View.INVISIBLE);
                        bandGenresTxt.setVisibility(View.INVISIBLE);
                        bandEmailTxt.setVisibility(View.INVISIBLE);
                        bandPhoneNoTxt.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }
}
