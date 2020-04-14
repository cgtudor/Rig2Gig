package com.gangoffive.rig2gig.band.management;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gangoffive.rig2gig.R;

public class DeleteMemberConfirmation extends Activity {

    private int height, width;
    private Button yes, no;
    private TextView confirmationText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation_layout);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = (metrics.heightPixels) /100 * 30;
        width = (metrics.widthPixels) /100 * 80;
        getWindow().setLayout(width,height);
        Intent intent = getIntent();
        String name = intent.getStringExtra("EXTRA_NAME");
        int position = intent.getIntExtra("EXTRA_POSITION", -1);
        confirmationText = findViewById(R.id.confirmationText);
        confirmationText.setText("Are you sure you want to remove this person from your band?");
        yes = findViewById(R.id.yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra("EXTRA_POSITION", position);
                setResult(RESULT_OK, result);
                finish();
            }
        });
        no = findViewById(R.id.no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnNotDeleted();
            }
        });
    }

    /**
     * Handle on back pressed
     */
    @Override
    public void onBackPressed()
    {
        returnNotDeleted();
    }

    /**
     * Handle if top activity changes
     * @param isTopResumedActivity false if no longer the top activty
     */
    @Override
    public void onTopResumedActivityChanged (boolean isTopResumedActivity)
    {
        if(!isTopResumedActivity)
        {
            returnNotDeleted();
        }
    }

    /**
     * Finish activity if band member is not deleted
     */
    public void returnNotDeleted()
    {
        Intent result = new Intent();
        result.putExtra("EXTRA_POSITION", -1);
        setResult(RESULT_OK, result);
        finish();
    }

}
