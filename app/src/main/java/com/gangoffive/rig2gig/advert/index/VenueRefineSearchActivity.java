package com.gangoffive.rig2gig.advert.index;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gangoffive.rig2gig.R;

import java.util.ArrayList;

public class VenueRefineSearchActivity extends AppCompatActivity {

    private int height, width;
    AppCompatRadioButton rbLeft, rbRight;

    private TextView ratingValue, distanceValue;
    private SeekBar ratingSlider, distanceSlider;

    private Button applyButton, cancelButton;

    private String sortBy, minRating, maxDistance;
    private ArrayList<String> venueTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_refine_search);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = (metrics.heightPixels) /100 * 80;
        width = (metrics.widthPixels) /100 * 80;
        getWindow().setLayout(width,height);

        rbLeft = findViewById(R.id.rbLeft);
        rbRight = findViewById(R.id.rbRight);

        Intent receivedIntent = getIntent();
        sortBy = receivedIntent.getStringExtra("EXTRA_SORT_BY");
        minRating = receivedIntent.getStringExtra("EXTRA_MIN_RATING");
        maxDistance = receivedIntent.getStringExtra("EXTRA_MAX_DISTANCE");
        venueTypes = receivedIntent.getStringArrayListExtra("EXTRA_VENUE_TYPES");


        if(sortBy != null) {
            switch (sortBy) {
                case "Rating": rbLeft.setChecked(true);
                onRadioButtonClicked(rbLeft);
                break;
                case "Recent": rbRight.setChecked(true);
                onRadioButtonClicked(rbRight);
                break;
            }
        }

        ratingValue = (TextView) findViewById(R.id.ratingValue);
        ratingSlider = (SeekBar) findViewById(R.id.ratingSlider);

        if(minRating != null) {
            double progressDouble = Double.valueOf(minRating);
            ratingValue.setText((progressDouble / 10) + "/5");
            int progress = (int) progressDouble;
            ratingSlider.setProgress(progress);
        }

        ratingSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                minRating = "" + progress;

                double progressDouble = progress;
                ratingValue.setText((progressDouble / 10) + "/5");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        distanceValue = (TextView) findViewById(R.id.distanceValue);
        distanceSlider = (SeekBar) findViewById(R.id.distanceSlider);

        if(maxDistance != null) {
            int progress = Integer.parseInt(maxDistance);
            distanceValue.setText((progress * 5) + " Miles");
            distanceSlider.setProgress(progress);
        }

        distanceSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maxDistance = "" + progress;

                distanceValue.setText((progress * 5) + " Miles");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        applyButton = findViewById(R.id.applyButton);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                if(sortBy != null) {
                    returnIntent.putExtra("EXTRA_SORT_BY", sortBy);
                }
                if(minRating != null) {
                    returnIntent.putExtra("EXTRA_MIN_RATING", minRating);
                }
                if(maxDistance != null) {
                    returnIntent.putExtra("EXTRA_MAX_DISTANCE", maxDistance);
                }
                if(venueTypes != null) {
                    returnIntent.putStringArrayListExtra("EXTRA_VENUE_TYPES", venueTypes);
                }
                setResult(VenueRefineSearchActivity.RESULT_OK, returnIntent);
                finish();
            }
        });

        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean isSelected = ((AppCompatRadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rbLeft:
                if(isSelected) {
                    rbLeft.setTextColor(Color.WHITE);
                    rbRight.setTextColor(Color.rgb(	18, 194, 233));

                    sortBy = "Rating";
                }
                break;
            case R.id.rbRight:
                if(isSelected) {
                    rbRight.setTextColor(Color.WHITE);
                    rbLeft.setTextColor(Color.rgb(	18, 194, 233));

                    sortBy = "Recent";
                }
                break;
        }
    }
}
