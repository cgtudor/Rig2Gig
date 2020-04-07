package com.gangoffive.rig2gig.advert.index;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gangoffive.rig2gig.R;

import java.util.ArrayList;

public class VenueRefineSearchActivity extends AppCompatActivity {

    private int height, width;
    AppCompatRadioButton rbLeft, rbCentre, rbRight;

    private TextView ratingValue, distanceValue;
    private SeekBar ratingSlider, distanceSlider;

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
        rbCentre = findViewById(R.id.rbCentre);
        rbRight = findViewById(R.id.rbRight);

        if(sortBy != null) {
            switch (sortBy) {
                case "Rating": onRadioButtonClicked(rbLeft);;
                break;
                case "Distance": onRadioButtonClicked(rbCentre);;
                break;
                case "Recent": onRadioButtonClicked(rbRight);;
                break;
            }
        }

        ratingValue = (TextView) findViewById(R.id.ratingValue);
        ratingSlider = (SeekBar) findViewById(R.id.ratingSlider);

        if(minRating != null) {
            ratingValue.setText(minRating + "/5");
            double progressDouble = Double.valueOf(minRating);
            int progress = (int) progressDouble / 10;
            ratingSlider.setProgress(progress);
        }

        ratingSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
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
            distanceValue.setText(maxDistance + " Miles");
            int progress = Integer.parseInt(maxDistance);
            ratingSlider.setProgress(progress);
        }

        distanceSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceValue.setText((progress * 5) + " Miles");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean isSelected = ((AppCompatRadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rbLeft:
                if(isSelected) {
                    rbLeft.setTextColor(Color.WHITE);
                    rbCentre.setTextColor(Color.rgb(	18, 194, 233));
                    rbRight.setTextColor(Color.rgb(	18, 194, 233));
                }
                break;
            case R.id.rbCentre:
                if(isSelected) {
                    rbLeft.setTextColor(Color.rgb(	18, 194, 233));
                    rbCentre.setTextColor(Color.WHITE);
                    rbRight.setTextColor(Color.rgb(	18, 194, 233));
                }
                break;
            case R.id.rbRight:
                if(isSelected) {
                    rbRight.setTextColor(Color.WHITE);
                    rbCentre.setTextColor(Color.rgb(	18, 194, 233));
                    rbLeft.setTextColor(Color.rgb(	18, 194, 233));
                }
                break;
        }
    }
}
