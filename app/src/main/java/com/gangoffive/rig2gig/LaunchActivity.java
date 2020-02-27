package com.gangoffive.rig2gig;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_activity);
    }

    public void loginBtnOnClick(View view) {
        startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
    }

    public void registerBtnOnClick(View view) {
        startActivity(new Intent(LaunchActivity.this, RegisterActivity.class));
    }
}
