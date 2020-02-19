  package com.gangoffive.rig2gig;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;


  public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Following intent creates the navbar activity. Ensure last in executions.
        Intent intent = new Intent(this, NavBarActivity.class);
        startActivity(intent);
    }
  }
