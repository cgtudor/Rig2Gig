package com.gangoffive.rig2gig.account;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gangoffive.rig2gig.advert.management.GooglePlacesAutoSuggestAdapter;
import com.gangoffive.rig2gig.band.management.TabbedBandActivity;
import com.gangoffive.rig2gig.musician.management.TabbedMusicianActivity;
import com.gangoffive.rig2gig.navbar.NavBarActivity;
import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.advert.index.ViewVenuesFragment;
import com.gangoffive.rig2gig.utils.GenreSelectorActivity;
import com.gangoffive.rig2gig.utils.ImageRequestHandler;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateMusicianFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateMusicianFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateMusicianFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public static Button btn;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    private static final int REQUEST_GALLERY__PHOTO = 1;

    private final String TAG = "@@@@@@@@@@@@@@@@";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;
    EditText distance, name;
    TextView genre;
    Button takePhotoBtn, uploadPhotoBtn, selectGenres;

    String email, userRef, phoneNumber, type, rating;

    private ImageView image;
    private Drawable chosenPic;

    //Google Places autocomplete textview
    private AutoCompleteTextView autoCompleteTextView;
    private Geocoder geocoder;

    private String [] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.READ_PHONE_STATE", "android.permission.SYSTEM_ALERT_WINDOW","android.permission.CAMERA"};

    public CreateMusicianFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateMusicianFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateMusicianFragment newInstance(String param1, String param2) {
        CreateMusicianFragment fragment = new CreateMusicianFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new ViewVenuesFragment()).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        final View v = inflater.inflate(R.layout.fragment_create_musician, container, false);

        btn = v.findViewById(R.id.submitBtn);
        takePhotoBtn = v.findViewById(R.id.takeBtn);
        uploadPhotoBtn = v.findViewById(R.id.uploadBtn);
        selectGenres = v.findViewById(R.id.selectGenres);


        btn.setOnClickListener(this);
        takePhotoBtn.setOnClickListener(this);
        uploadPhotoBtn.setOnClickListener(this);
        selectGenres.setOnClickListener(this);

        btn.setVisibility(View.INVISIBLE);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        distance = v.findViewById(R.id.firstName3);
        name = v.findViewById(R.id.firstName);
        genre = v.findViewById(R.id.firstName5);

        image = v.findViewById(R.id.imageView);

        autoCompleteTextView = v.findViewById(R.id.location2);
        autoCompleteTextView.setAdapter(new GooglePlacesAutoSuggestAdapter(getActivity(), android.R.layout.simple_list_item_1));

        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }

        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M) {
            boolean canWriteSettings = Settings.System.canWrite(getActivity());
            if (!canWriteSettings) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                startActivity(intent);
            }
        }

        return v;
    }

    private Address getAddress()
    {
        String musicianName = autoCompleteTextView.getText().toString();
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try
        {
            List<Address> addressList = geocoder.getFromLocationName(musicianName, 1);

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

    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.submitBtn:
                userRef = fAuth.getUid();
                email = fAuth.getCurrentUser().getEmail();
                String musicianName = name.getText().toString();
                String musicianDistance = distance.getText().toString();
                String musicianAddressTextView = autoCompleteTextView.getText().toString();
                Address musicianAddress = getAddress();
                String genres = genre.getText().toString();
                ImageView defImg = new ImageView(getActivity());
                defImg.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);
                rating = "-1";

//                if(TextUtils.isEmpty(musicianAddressTextView))
//                {
//                    autoCompleteTextView.setError("Please Enter Your Venue Address");
//                    return;
//                }
//                if(musicianAddress == null)
//                {
//                    autoCompleteTextView.setError("Please Enter A Valid Address");
//                    return;
//                }
//                if (TextUtils.isEmpty(musicianName)) {
//                    name.setError("Please Enter A Musician Name!");
//                    return;
//                }
//                if (TextUtils.isEmpty(musicianDistance)) {
//                    distance.setError("Please Set A Distance!");
//                    return;
//                }

                fStore.collection("users").document(userRef).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists())
                            {
                                Log.d(TAG, "Document exists!");
                                phoneNumber = document.get("phone-number").toString();

                                String genresText = genre.getText().toString();
                                ArrayList<String> selectedGenres = new ArrayList<String>(Arrays.asList(genresText.split(",")));
                                for (int i = 0; i < selectedGenres.size(); i++)
                                {
                                    selectedGenres.set(i,selectedGenres.get(i).trim());
                                }


                                Map<String, Object> musicians = new HashMap<>();
                                musicians.put("name", musicianName);
                                musicians.put("index-name",musicianName.toLowerCase());
                                musicians.put("location", checkLocality(musicianAddress));
                                musicians.put("user-ref", userRef);
                                musicians.put("email-address", email);
                                musicians.put("phone-number", phoneNumber);
                                musicians.put("genres",selectedGenres);
                                musicians.put("distance", musicianDistance);
                                musicians.put("latitude", musicianAddress.getLatitude());
                                musicians.put("longitude", musicianAddress.getLongitude());
                                musicians.put("rating", rating);

                                fStore.collection("musicians")
                                        .add(musicians)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                                StorageReference sRef = fStorage.getReference()
                                                        .child("/images/musicians/" + documentReference.getId() + ".jpg");
                                                UploadTask uploadTask = sRef.putBytes(imageToByteArray(image.getDrawable()));
                                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        Log.d("STORAGE SUCCEEDED", taskSnapshot.getMetadata().toString());
                                                        Intent intent = new Intent(getActivity(), NavBarActivity.class);
                                                        startActivity(intent);
                                                        Toast.makeText(getActivity(), "Musician Account Created!", Toast.LENGTH_SHORT).show();
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
                            }
                            else
                            {
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
            case R.id.selectGenres:
                TabbedMusicianActivity.faderBtn.performClick();
                selectGenres();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    private String checkLocality(Address musicianAddress)
    {
        if(musicianAddress.getLocality() != null)
        {
            return musicianAddress.getLocality();
        }
        else if(musicianAddress.getSubLocality() != null)
        {
            return musicianAddress.getSubLocality();
        }
        else
        {
            return musicianAddress.getPostalCode();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public byte[] imageToByteArray(Drawable image)
    {
        Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public void selectGenres()
    {
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@ HERE");
        Intent intent =  new Intent(getActivity(), GenreSelectorActivity.class);
        intent.putExtra("EXTRA_LAYOUT_TYPE", "Login");
        intent.putExtra("EXTRA_GENRES", genre.getText().toString());
        startActivityForResult(intent, 99);
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

        if (requestCode == 99 && resultCode == Activity.RESULT_OK)
        {
            String genresExtra = data.getStringExtra("EXTRA_SELECTED_GENRES");
            genre.setText(genresExtra);
        }
    }
}
