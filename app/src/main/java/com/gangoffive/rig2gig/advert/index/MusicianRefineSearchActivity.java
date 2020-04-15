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
import androidx.appcompat.widget.AppCompatRadioButton;

import com.gangoffive.rig2gig.R;
import com.gangoffive.rig2gig.utils.GenreSelectorActivity;
import com.gangoffive.rig2gig.utils.PositionSelectorActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class MusicianRefineSearchActivity extends AppCompatActivity {

    private final int LAUNCH_MUSICIAN_POSITION_SELECT = 2709;
    private final int LAUNCH_MUSICIAN_GENRE_SELECT = 1414;

    private int height, width;
    AppCompatRadioButton rbLeft, rbRight;

    private TextView ratingValue, distanceValue, positionValue, genreValue;
    private SeekBar ratingSlider, distanceSlider;

    private Button positionButton, genreButton, applyButton, cancelButton;

    private String sortBy, minRating, maxDistance;
    private ArrayList<String> positions, genres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musician_refine_search);

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
        positions = receivedIntent.getStringArrayListExtra("EXTRA_MUSICIAN_POSITIONS");
        genres = receivedIntent.getStringArrayListExtra("EXTRA_MUSICIAN_GENRES");


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

        positionButton = findViewById(R.id.positionButton);

        positionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPositions();
            }
        });

        positionValue = findViewById(R.id.positionValue);

        if(positions != null) {
            String positionsString = positions.toString();
            positionsString = positionsString.substring(1, (positionsString.length() - 1));
            if(!positions.isEmpty()) {
                positionButton.setText("Edit Positions");
            }
            positionValue.setText(positionsString);
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
            String genresString = genres.toString();
            genresString = genresString.substring(1, (genresString.length() - 1));
            if(!positions.isEmpty()) {
                genreButton.setText("Edit Genres");
            }
            genreValue.setText(genresString);
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
                if(positions != null) {
                    returnIntent.putStringArrayListExtra("EXTRA_MUSICIAN_POSITIONS", positions);
                }
                if(genres != null) {
                    returnIntent.putStringArrayListExtra("EXTRA_MUSICIAN_GENRES", genres);
                }
                setResult(MusicianRefineSearchActivity.RESULT_OK, returnIntent);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_MUSICIAN_POSITION_SELECT)
        {
            if (resultCode == RESULT_OK)
            {
                positions = new ArrayList<String>(Arrays.asList(data.getStringExtra("EXTRA_SELECTED_POSITIONS").split(",")));
                String positionsString = positions.toString();
                positionsString = positionsString.substring(1, (positionsString.length() - 1));
                positionValue.setText(positionsString);
                setPositions();
            }
        }
        if (requestCode == LAUNCH_MUSICIAN_GENRE_SELECT)
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

    public void selectPositions()
    {
        Intent intent =  new Intent(this, PositionSelectorActivity.class);
        intent.putExtra("EXTRA_LAYOUT_TYPE", "Not Login");
        if(positions != null) {
            String positionsString = positions.toString();
            positionsString = positionsString.substring(1, (positionsString.length() - 1));
            intent.putExtra("EXTRA_POSITIONS", positionsString);
        }

        startActivityForResult(intent, LAUNCH_MUSICIAN_POSITION_SELECT);
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

        startActivityForResult(intent, LAUNCH_MUSICIAN_GENRE_SELECT);
    }

    public void setPositions()
    {
        if (positionValue.getText().toString().equals(""))
        {
            positionButton.setText("Select Positions");
        }
        else
        {
            positionButton.setText("Edit Positions");
        }
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
