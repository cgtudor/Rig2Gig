package com.gangoffive.rig2gig.advert.index;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatRadioButton;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.utils.GenreSelectorActivity;
import com.gangoffive.rig2gig.utils.VenueTypeSelectorActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class PerformerRefineSearchActivity extends AppCompatActivity {

    private final int LAUNCH_PERFORMER_GENRE_SELECT = 2749;

    private int height, width;
    AppCompatRadioButton rbRating, rbRecent;

    private TextView ratingValue, distanceValue, genreValue;
    private SeekBar ratingSlider, distanceSlider;

    AppCompatCheckBox showMusicianCheckBox, showBandCheckBox;

    private Button genreButton, applyButton, cancelButton;

    private String sortBy, minRating, maxDistance;
    private ArrayList<String> performerTypes, genres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performer_refine_search);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = (metrics.heightPixels) /100 * 80;
        width = (metrics.widthPixels) /100 * 80;
        getWindow().setLayout(width,height);

        rbRating = findViewById(R.id.rbRating);
        rbRecent = findViewById(R.id.rbRecent);

        Intent receivedIntent = getIntent();
        sortBy = receivedIntent.getStringExtra("EXTRA_SORT_BY");
        minRating = receivedIntent.getStringExtra("EXTRA_MIN_RATING");
        maxDistance = receivedIntent.getStringExtra("EXTRA_MAX_DISTANCE");
        performerTypes = receivedIntent.getStringArrayListExtra("EXTRA_PERFORMER_TYPES");
        genres = receivedIntent.getStringArrayListExtra("EXTRA_PERFORMER_GENRES");


        if(sortBy != null) {
            switch (sortBy) {
                case "Rating": rbRating.setChecked(true);
                onRadioButtonClicked(rbRating);
                break;
                case "Recent": rbRecent.setChecked(true);
                onRadioButtonClicked(rbRecent);
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

        showMusicianCheckBox = findViewById(R.id.showMusicianCheckBox);
        showBandCheckBox = findViewById(R.id.showBandCheckBox);

        if(performerTypes != null) {
            if(!performerTypes.contains("Musicians")) {
                showMusicianCheckBox.setChecked(false);
            }

            if(!performerTypes.contains("Bands")) {
                showBandCheckBox.setChecked(false);
            }
        }

        genreButton = findViewById(R.id.genreButton);

        genreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGenres();
            }
        });

        genreValue = findViewById(R.id.genreValue);

        if(genres != null) {
            String venueTypesString = genres.toString();
            venueTypesString = venueTypesString.substring(1, (venueTypesString.length() - 1));
            if(!genres.isEmpty()) {
                genreButton.setText("Edit Genres");
            }
            genreValue.setText(venueTypesString);
        }

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
                if(performerTypes != null) {
                    returnIntent.putStringArrayListExtra("EXTRA_PERFORMER_TYPES", performerTypes);
                }
                if(genres != null) {
                    returnIntent.putStringArrayListExtra("EXTRA_PERFORMER_GENRES", genres);
                }
                setResult(PerformerRefineSearchActivity.RESULT_OK, returnIntent);
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
            case R.id.rbRating:
                if(isSelected) {
                    rbRating.setTextColor(Color.WHITE);
                    rbRecent.setTextColor(Color.rgb(	18, 194, 233));

                    sortBy = "Rating";
                }
                break;
            case R.id.rbRecent:
                if(isSelected) {
                    rbRecent.setTextColor(Color.WHITE);
                    rbRating.setTextColor(Color.rgb(	18, 194, 233));

                    sortBy = "Recent";
                }
                break;
        }
    }

    public void onCheckBoxClicked(View view) {
        boolean isSelected = ((AppCompatCheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.showMusicianCheckBox:
                if(isSelected) {
                    if(performerTypes == null) {
                        performerTypes = new ArrayList<String>();
                    }
                    performerTypes.add("Musicians");
                } else {
                    if(performerTypes == null) {
                        performerTypes = new ArrayList<String>();
                        performerTypes.add("Bands");
                    } else {
                        performerTypes.remove("Musicians");
                    }
                }
                break;
            case R.id.showBandCheckBox:
                if(isSelected) {
                    if(performerTypes == null) {
                        performerTypes = new ArrayList<String>();
                    }
                    performerTypes.add("Bands");
                } else {
                    if(performerTypes == null) {
                        performerTypes = new ArrayList<String>();
                        performerTypes.add("Musicians");
                    } else {
                        performerTypes.remove("Bands");
                    }
                }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_PERFORMER_GENRE_SELECT)
        {
            if (resultCode == RESULT_OK)
            {
                genres = new ArrayList<String>(Arrays.asList(data.getStringExtra("EXTRA_SELECTED_GENRES").split(",")));
                String genresString = genres.toString();
                genresString = genresString.substring(1, (genresString.length() - 1));
                genreValue.setText(genresString);
                setGenres();
            }

        }
    }

    public void selectGenres()
    {
        Intent intent =  new Intent(this, GenreSelectorActivity.class);
        intent.putExtra("EXTRA_LAYOUT_TYPE", "Not Login");
        if(genres != null) {
            String genresString = genres.toString();
            genresString = genresString.substring(1, (genresString.length() - 1));
            intent.putExtra("EXTRA_GENRES", genresString);
        }

        startActivityForResult(intent, LAUNCH_PERFORMER_GENRE_SELECT);
    }

    public void setGenres()
    {
        if (genreValue.getText().toString().equals(""))
        {
            genreButton.setText("Select Genres");
        }
        else
        {
            genreButton.setText("Edit Genres");
        }
    }
}
