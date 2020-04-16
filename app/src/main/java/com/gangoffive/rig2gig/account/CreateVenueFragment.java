package com.gangoffive.rig2gig.account;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gangoffive.rig2gig.advert.management.GooglePlacesAutoSuggestAdapter;
import com.gangoffive.rig2gig.navbar.NavBarActivity;
import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.advert.index.VenueListing;
import com.gangoffive.rig2gig.advert.index.ViewVenuesFragment;
import com.gangoffive.rig2gig.utils.VenueTypes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateVenueFragment extends Fragment implements View.OnClickListener {
    private String TAG = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    private static final int REQUEST_GALLERY__PHOTO = 1;

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseStorage fStorage = FirebaseStorage.getInstance();
    private EditText description, name, venueType;
    private AutoCompleteTextView location;
    public static Button submit, takePhotoBtn, uploadPhotoBtn;
    private Geocoder geocoder;

    String email, userRef, phoneNumber, type;

    private ImageView image;
    private Drawable chosenPic;

    private ArrayList<VenueListing> venueListings;

    public static Button venueBtn;

    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.SYSTEM_ALERT_WINDOW","android.permission.CAMERA"};

    /**
     * Upon creation of the ViewVenuesFragment, create the fragment_view_venues layout.
     *
     * @param inflater           The inflater is used to read the passed xml file.
     * @param container          The views base class.
     * @param savedInstanceState This is the saved previous state passed from the previous fragment/activity.
     * @return Returns a View of the fragment_upgrade_to_musicians layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewVenuesFragment()).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        final View v = inflater.inflate(R.layout.fragment_create_venue, container, false);
        venueBtn = v.findViewById(R.id.submitBtn);
        takePhotoBtn = v.findViewById(R.id.takeBtn);
        uploadPhotoBtn = v.findViewById(R.id.uploadBtn);
        takePhotoBtn.setOnClickListener(this);
        uploadPhotoBtn.setOnClickListener(this);
        venueBtn.setOnClickListener(this);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        description = v.findViewById(R.id.venue_description);
        location = v.findViewById(R.id.venue_location);
        name = v.findViewById(R.id.venue_name);
        venueType = v.findViewById(R.id.type);
        submit = v.findViewById(R.id.submitBtn);

        location = v.findViewById(R.id.venue_location);
        location.setAdapter(new GooglePlacesAutoSuggestAdapter(getActivity(), android.R.layout.simple_list_item_1));

//        image = v.findViewById(R.id.imageViewVenue);
//        image.setBackgroundResource(R.drawable.com_facebook_profile_picture_blank_portrait);

        image = v.findViewById(R.id.imageViewVenue);

        submit.setVisibility(View.INVISIBLE);

        Spinner spinner = v.findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.spinner, VenueTypes.getTypes());
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                switch (position){
                    case 0:
                        type = "Function Room";
                        break;
                    case 1:
                        type = "Bar";
                        break;
                    case 2:
                        type = "Club";
                        break;
                    case 3:
                        type = "Pub";
                        break;
                    case 4:
                        type = "Hotel";
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + type);
        switch (v.getId()) {
            case R.id.submitBtn:
                userRef = fAuth.getUid();
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ + " + userRef);
                email = fAuth.getCurrentUser().getEmail();
                String desc = description.getText().toString();
                String venueAddressTextView = location.getText().toString();
                Address venueAddress = getAddress();
                String venueName = name.getText().toString();
                String venueRating = "-1";

//                if (image.getDrawable() == null)
//                {
//                    location.setError("Please select an image");
//                    return;
//                }

//                if (TextUtils.isEmpty(venueAddressTextView)) {
//                    location.setError("Please Set A Locaton!");
//                    return;
//                }
//                if(venueAddress == null)
//                {
//                    location.setError("Please enter a valid location!");
//                    return;
//                }
//                if (TextUtils.isEmpty(desc)) {
//                    description.setError("Please Enter A Venue Description!");
//                    return;
//                }
//                if (TextUtils.isEmpty(venueName)) {
//                    name.setError("Please Enter A Venue Name!");
//                    return;
//                }


                fStore.collection("users").document(fAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "Document exists!");
                                phoneNumber = document.get("phone-number").toString();

                                Map<String, Object> venues = new HashMap<>();
                                venues.put("name", venueName);
                                venues.put("location", checkLocality(venueAddress));
                                venues.put("description", desc);
                                venues.put("user-ref", userRef);
                                venues.put("venue-type", type);
                                venues.put("email-address", email);
                                venues.put("phone-number", phoneNumber);
                                venues.put("rating", venueRating);
                                venues.put("latitude", venueAddress.getLatitude());
                                venues.put("longitude", venueAddress.getLongitude());
                                fStore.collection("venues")
                                        .add(venues)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                                StorageReference sRef = fStorage.getReference()
                                                        .child("/images/venues/" + documentReference.getId() + ".jpg");
                                                UploadTask uploadTask = sRef.putBytes(imageToByteArray(image.getDrawable()));
                                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        Log.d("STORAGE SUCCEEDED", taskSnapshot.getMetadata().toString());
                                                        Intent intent = new Intent(getActivity(), NavBarActivity.class);
                                                        startActivity(intent);
                                                        Toast.makeText(getActivity(), "Venue Account Created!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("STORAGE FAILED", e.toString());
                                                    }
                                                });

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error adding document", e);
                                            }
                                        });
                            } else {
                                Log.d(TAG, "Document doesn't exists!");
                            }
                        }
                    }

                });
                break;
            case R.id.takeBtn:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,
                        CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                break;
            case R.id.uploadBtn:
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, REQUEST_GALLERY__PHOTO);
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@2 " + REQUEST_GALLERY__PHOTO);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    private String checkLocality(Address venueAddress)
    {
        if(venueAddress.getLocality() != null)
        {
            return venueAddress.getLocality();
        }
        else if(venueAddress.getSubLocality() != null)
        {
            return venueAddress.getSubLocality();
        }
        else
        {
            return venueAddress.getPostalCode();
        }
    }

    public boolean onBackPressed() {
        return true;
    }

    public byte[] imageToByteArray(Drawable image) {
        Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap

                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);

                image.setImageBitmap(bitmap);
            }
        }
        else
        {
            if (requestCode == REQUEST_GALLERY__PHOTO)
            {
                    Uri returnUri = data.getData();
                    Bitmap bitmapImage = null;
                    try {
                        bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image.setImageBitmap(bitmapImage);
            }
        }
    }

    private Address getAddress()
    {
        String venueName = location.getText().toString();
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try
        {
            List<Address> addressList = geocoder.getFromLocationName(venueName, 1);

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
            Log.d(TAG, io.toString());
            return null;
        }
    }
}




