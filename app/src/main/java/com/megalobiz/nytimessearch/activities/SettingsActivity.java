package com.megalobiz.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.megalobiz.nytimessearch.R;
import com.megalobiz.nytimessearch.models.SearchSettings;

import java.text.SimpleDateFormat;

public class SettingsActivity extends AppCompatActivity {

    SimpleDateFormat beginDate;

    RadioGroup radioGroupSort;
    RadioButton rbNewest, rbOldest, radioButtonSort;

    CheckBox cbArts, cbFashion, cbSports;

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
        // begin date


        // radio sort order objects
        radioGroupSort = (RadioGroup) findViewById(R.id.rgSort);
        rbNewest = (RadioButton) findViewById(R.id.rbNewest);
        rbOldest = (RadioButton) findViewById(R.id.rbOldest);

        if(settings.getSortOrder() == SearchSettings.Sort.newest) {
            rbNewest.setChecked(true);
        } else if(settings.getSortOrder() == SearchSettings.Sort.oldest) {
            rbOldest.setChecked(true);
        }


        // checkbox filter objects
        // fill checkboxes according to settings value
        cbArts = (CheckBox) findViewById(R.id.cbArts);
        cbFashion = (CheckBox) findViewById(R.id.cbFashion);
        cbSports = (CheckBox) findViewById(R.id.cbSports);

        for(String filter : settings.getFilters()) {
            if(filter.equals("Arts"))
                cbArts.setChecked(true);

            if(filter.equals("Fashion & Style"))
                cbFashion.setChecked(true);

            if(filter.equals("Sports"))
                cbSports.setChecked(true);
        }
    }

    public void onClick(View view) {

        // set radio sort settings
        setRadioSort();

        // set checkbox filters settings
        setCheckboxFilters();

        //Toast.makeText(this, "Radio Button: "+ this.sort, Toast.LENGTH_SHORT).show();
        Intent i = new Intent();
        i.putExtra("settings", settings);
        setResult(RESULT_OK, i);
        finish();
    }

    public void setRadioSort() {
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

    public void setCheckboxFilters() {
        settings.getFilters().clear();

        if(cbArts.isChecked())
            settings.addFilter("Arts");

        if(cbFashion.isChecked())
            settings.addFilter("Fashion & Style");

        if(cbSports.isChecked())
            settings.addFilter("Sports");
    }


    public void onOpenDatePicker(View view) {

    }
}
