package com.gangoffive.rig2gig;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LaunchActivity extends AppCompatActivity {

    Button logInBtn, registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        logInBtn = findViewById(R.id.launchLoginBtn);
        registerBtn = findViewById(R.id.launchRegisterButton);

        /**
         *  Redirecting from LaunchActivity to LoginActivity
         */
        logInBtn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        /**
         *  Redirecting from LaunchActivity to RegisterActivity
         */
        registerBtn.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }
}
