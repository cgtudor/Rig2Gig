package com.gangoffive.rig2gig;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.identityconnectors.common.security.GuardedString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


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
    EditText distance, location, name, genre;
    Button takePhotoBtn, uploadPhotoBtn;

    String email, userRef, phoneNumber, type;

    private ImageView image;
    private Drawable chosenPic;

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

        btn.setOnClickListener(this);
        takePhotoBtn.setOnClickListener(this);
        uploadPhotoBtn.setOnClickListener(this);

        btn.setVisibility(View.INVISIBLE);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();

        distance = v.findViewById(R.id.distance);
        location = v.findViewById(R.id.location);
        name = v.findViewById(R.id.name);
        genre = v.findViewById(R.id.genre);

        image = v.findViewById(R.id.imageView);

        return v;
    }

    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.submitBtn:
                userRef = fAuth.getUid();
                email = fAuth.getCurrentUser().getEmail();

                String loc = location.getText().toString();
                String musicianName = name.getText().toString();
                String musicianDistance = distance.getText().toString();
                String genres = genre.getText().toString();
                ImageView defImg = new ImageView(getActivity());
                defImg.setImageResource(R.drawable.com_facebook_profile_picture_blank_portrait);

                if (TextUtils.isEmpty(loc)) {
                    location.setError("Please Set A Locaton!");
                    return;
                }
                if (TextUtils.isEmpty(musicianName)) {
                    name.setError("Please Enter A Musician Name!");
                    return;
                }
                if (TextUtils.isEmpty(musicianDistance)) {
                    distance.setError("Please Set A Distance!");
                    return;
                }

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

                                Map<String, Object> musicians = new HashMap<>();
                                musicians.put("name", musicianName);
                                musicians.put("location", loc);
                                musicians.put("user-ref", userRef);
                                musicians.put("email-address", email);
                                musicians.put("phone-number", phoneNumber);
                                musicians.put("genres", genres);
                                musicians.put("distance", musicianDistance);
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
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
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
}
