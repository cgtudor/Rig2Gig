  package com.gangoffive.rig2gig;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

  public class MainActivity extends AppCompatActivity {

      FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Db test
        fStore = FirebaseFirestore.getInstance();

        DocumentReference documentReference = fStore.collection("Testing").document("testId");
        Map<String,Object> testInput = new HashMap<>();
        testInput.put("firstName", "Jacob");
        testInput.put("lastName", "Jardine");
        documentReference.set(testInput).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                System.out.println("------------------------Worked");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("------------------------Didn't Work");
            }
        });

        //Following intent creates the navbar activity. Ensure last in executions.
        Intent intent = new Intent(this, NavBarActivity.class);
        startActivity(intent);
    }
}
