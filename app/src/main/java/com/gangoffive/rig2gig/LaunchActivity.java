package com.gangoffive.rig2gig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

/**
 * LaunchActivity loads on the initial launch of the application.
 */
public class LaunchActivity extends AppCompatActivity {

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private static final String TAG = "TAG";

    /**
     * When the onCreate is called previous states from the activity can be restored.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch_activity);

        //Uncomment this for login testing
        if (fAuth.getCurrentUser() != null)
        {
            final String getUserId = fAuth.getUid();
            DocumentReference docIdRef = fStore.collection("users").document(getUserId);
            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                {
                    if (task.isSuccessful())
                    {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists())
                        {
                            Log.d(TAG, "Document exists!");
                            CredentialActivity.userType = document.get("User Type").toString();
                            startActivity(new Intent(getApplicationContext(), NavBarActivity.class));
                        }
                        else
                        {
                            Log.d(TAG, "Document doesn't exists!");
                        }
                    }
                }
            });
       }
    }

    /**
     * onClick the registerBtn will launch the LoginActivity
     * @param view
     */
    public void loginBtnOnClick(View view) {
        startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
    }

    /**
     * onClick the registerBtn will launch the RegisterActivity
     * @param view
     */
    public void registerBtnOnClick(View view) {
        startActivity(new Intent(LaunchActivity.this, RegisterActivity.class));
    }
}
