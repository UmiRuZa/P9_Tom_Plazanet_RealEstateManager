package com.openclassrooms.realestatemanager.UI.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.UI.ItemDetailHostActivity;


public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }


    SwitchCompat convert;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPref.edit();

        ((ItemDetailHostActivity)getActivity()).configureToolbar();

        convert = view.findViewById(R.id.settings_convert);

        this.setConvertButtonText();

        this.setConvertButton();

        return view;
    }

    private void setConvertButtonText() {
        if(sharedPref.getBoolean("Convert", false)) {
            convert.setText("Devise : EUR");
        } else {
            convert.setText("Devise : USD");
        }

    }

    private void setConvertButton() {
        if (sharedPref.getBoolean("Convert", false)) {
            convert.setChecked(true);
        } else {
            convert.setChecked(false);
        }


        convert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        editor.putBoolean("Convert", true).commit();
                    } else {
                        editor.putBoolean("Convert", false).commit();
                    }
                    setConvertButtonText();
            }
        });
    }
}