package com.gangoffive.rig2gig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.identityconnectors.framework.impl.api.local.operations.SpiOperationLoggingUtil;

public class SplashScreenActivity extends AppCompatActivity {

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    private static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        System.out.println("@@@@2@@@@@@@@@@@@@@@@@@@@@@2 " + fAuth.getUid());

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
                            AccountPurposeActivity.userType = document.get("user-type").toString();
                            startActivity(new Intent(getApplicationContext(), NavBarActivity.class));
                        }
                        else
                        {
                            Log.d(TAG, "Document doesn't exists!");
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                    }
                }
            });
        }
        else
            {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
    }
}
