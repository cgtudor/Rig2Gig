package com.gangoffive.rig2gig;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.google.firebase.firestore.FieldValue.arrayUnion;

public class CreateBandFragment extends Fragment implements View.OnClickListener {

    private Geocoder geocoder;
    private AutoCompleteTextView location;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseStorage fStorage = FirebaseStorage.getInstance();
    private final List<String> newBand = new ArrayList<>();

    public static Button btn;

    EditText cBandName, cBandLocation, cBandDistance, cBandGenres, cBandEmail, cBandPhoneNumber;

    public static String bandRef;
    /**
     * Upon creation of the CreateBandFragment, create the fragment_create_band layout.
     * @param inflater The inflater is used to read the passed xml file.
     * @param container The views base class.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     * @return Returns a View of the fragment_about layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        final View v = inflater.inflate(R.layout.fragment_create_band, container, false);

        btn = v.findViewById(R.id.createBandBtn);
        btn.setOnClickListener(this);
        btn.setVisibility(View.INVISIBLE);

        cBandName = v.findViewById(R.id.BandName);
        location = v.findViewById(R.id.location3);
        location.setAdapter(new GooglePlacesAutoSuggestAdapter(getActivity(), android.R.layout.simple_list_item_1));
        cBandDistance = v.findViewById(R.id.bandDistance);
        cBandGenres = v.findViewById(R.id.bandGenres);
        cBandEmail = v.findViewById(R.id.bandEmail);
        cBandPhoneNumber = v.findViewById(R.id.bandPhoneNumber);

        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        return v;
    }

    private Address getAddress()
    {
        String bandName = location.getText().toString();
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try
        {
            List<Address> addressList = geocoder.getFromLocationName(bandName, 1);

            if(addressList.size() > 0)
            {
                Address address = addressList.get(0);
                return address;
            }
            else
            {
                return null;
            }
        }
        catch(IOException io)
        {
            System.out.println(io.getMessage());
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.createBandBtn:
                final String bandName = cBandName.getText().toString();
                final String bandLocation = location.getText().toString().trim();
                final Address bandAddress = getAddress();
                final String bandDistance = cBandDistance.getText().toString().trim();
                final String bandGenres = cBandGenres.getText().toString().trim();
                final String bandEmail = cBandEmail.getText().toString().trim();
                final String bandPhoneNumber = cBandPhoneNumber.getText().toString().trim();
                newBand.add(TabbedBandActivity.musicianID);

//                if (TextUtils.isEmpty(bandName)){
//                    cBandName.setError("Band Name Is Required!");
//                    return;
//                }
//                if(bandAddress == null)
//                {
//                    location.setError("Please Enter A Valid Address");
//                    return;
//                }
//                if(TextUtils.isEmpty(bandLocation))
//                {
//                    location.setError("Please Enter An Address");
//                    return;
//                }
//                if (TextUtils.isEmpty(bandDistance)){
//                    cBandDistance.setError("Distance Is Required!");
//                    return;
//                }
//                if (TextUtils.isEmpty(bandGenres)){
//                    cBandGenres.setError("Genres Is Required!");
//                    return;
//                }
//                if (TextUtils.isEmpty(bandEmail)){
//                    cBandEmail.setError("Band Email Is Required!");
//                    return;
//                }
//                if (TextUtils.isEmpty(bandPhoneNumber)){
//                    cBandPhoneNumber.setError("Band Phone Number Is Required!");
//                    return;
//                }

                DocumentReference docRef = fStore.collection("users").document(fAuth.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                Map<String, Object> band = new HashMap<>();
                                band.put("name", bandName);
                                band.put("location", checkLocality(bandAddress));
                                band.put("distance", bandDistance);
                                band.put("genres", bandGenres);
                                band.put("email", bandEmail);
                                band.put("phone-number", bandPhoneNumber);
                                band.put("latitude", bandAddress.getLatitude());
                                band.put("longitude", bandAddress.getLongitude());
                                band.put("rating", "-1");
                                band.put("members", Arrays.asList(TabbedBandActivity.musicianID));

                                fStore.collection("bands")
                                        .add(band)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ going to band image");
                                                bandRef = documentReference.getId();


                                                DocumentReference doc = fStore.collection("musicians").document(TabbedBandActivity.musicianID);
                                                doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful())
                                                        {
                                                            DocumentSnapshot document = task.getResult();
                                                            Map<String, Object> musicians = new HashMap<>();
                                                            musicians = document.getData();
                                                            ArrayList bands = (ArrayList) musicians.get("bands");
                                                            if (bands == null)
                                                            {
                                                                musicians.put("bands", Arrays.asList(bandRef));
                                                            }else
                                                                {
                                                                    bands.add(bandRef);
                                                                }

                                                            //
                                                            doc.update(musicians);
                                                            BandImageFragment.submitBtn.performClick();
                                                            Toast.makeText(getActivity(), "Band Created!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("STORAGE FAILED", e.toString());
                                    }
                                });

                            } else {
                                Log.d("LOGGER", "No such document");
                            }
                        } else {
                            Log.d("LOGGER", "get failed with ", task.getException());
                        }
                    }
                });
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    private String checkLocality(Address bandAddress)
    {
        if(bandAddress.getLocality() != null)
        {
            return bandAddress.getLocality();
        }
        else if(bandAddress.getSubLocality() != null)
        {
            return bandAddress.getSubLocality();
        }
        else
        {
            return bandAddress.getPostalCode();
        }
    }
}
