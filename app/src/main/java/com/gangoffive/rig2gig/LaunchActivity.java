package com.gangoffive.rig2gig;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * LaunchActivity loads on the initial launch of the application.
 */
public class LaunchActivity extends AppCompatActivity {

    /**
     * When the onCreate is called previous states from the activity can be restored.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_acitivy);
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
