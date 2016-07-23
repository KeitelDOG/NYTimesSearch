package com.megalobiz.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.megalobiz.nytimessearch.R;
import com.megalobiz.nytimessearch.models.SearchSettings;

public class SettingsActivity extends AppCompatActivity {

    RadioGroup radioGroupSort;
    RadioButton radioButtonSort;

    RadioButton rbNewest;
    RadioButton rbOldest;

    SearchSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        setSearchParametersValue();

        // initialize radio objects
        initializeSettingsObjects();
    }

    public void setSearchParametersValue() {
        this.settings = (SearchSettings) getIntent().getSerializableExtra("settings");
    }

    public void initializeSettingsObjects() {
        rbNewest = (RadioButton) findViewById(R.id.rbNewest);
        rbOldest = (RadioButton) findViewById(R.id.rbOldest);

        if(settings.getSortOrder().equals(SearchSettings.Sort.newest.name())) {
            rbNewest.setChecked(true);
        } else if(settings.getSortOrder().equals(SearchSettings.Sort.oldest.name())) {
            rbOldest.setChecked(true);
        }

    }

    public void onClick(View view) {

        // set radio sort settings
        setRadioSort();

        //Toast.makeText(this, "Radio Button: "+ this.sort, Toast.LENGTH_SHORT).show();
        Intent i = new Intent();
        i.putExtra("settings", settings);
        setResult(RESULT_OK, i);
        finish();
    }

    public void setRadioSort() {
        radioGroupSort = (RadioGroup) findViewById(R.id.rgSort);

        // get selected radio button
        int selectedId = radioGroupSort.getCheckedRadioButtonId();

        // find the radio button
        radioButtonSort = (RadioButton) findViewById(selectedId);

        switch (selectedId){
            case R.id.rbNewest:
                this.settings.setSortOrder(SearchSettings.Sort.newest);
                break;

            case R.id.rbOldest:
                this.settings.setSortOrder(SearchSettings.Sort.oldest);
                break;
        }
    }


}
