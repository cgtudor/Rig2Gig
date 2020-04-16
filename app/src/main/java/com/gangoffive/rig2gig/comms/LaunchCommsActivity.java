package com.gangoffive.rig2gig.comms;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gangoffive.rig2gig.navbar.NavBarActivity;

public class LaunchCommsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, NavBarActivity.class).putExtra("TARGET_FRAGMENT", "COMMS"));
        finish();
    }
}
