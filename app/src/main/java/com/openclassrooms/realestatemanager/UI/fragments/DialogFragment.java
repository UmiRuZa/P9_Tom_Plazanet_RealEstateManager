package com.openclassrooms.realestatemanager.UI.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.placeholder.PlaceholderContent;
import com.openclassrooms.realestatemanager.roomdb.AppDatabase;
import com.openclassrooms.realestatemanager.roomdb.ResidenceDAO;

import java.util.ArrayList;
import java.util.List;

public class DialogFragment extends androidx.fragment.app.DialogFragment {

    Context mContext;

    public DialogFragment() {
    }

    public static DialogFragment newInstance(String title) {
        DialogFragment frag = new DialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    AppDatabase db;
    ResidenceDAO residenceDAO;

    // widget
    TextView residTypeTV, residLocationTV;
    Button confirmButton, cancelButton, clearButton;

    EditText residPriceMin, residPriceMax;
    EditText residSurfaceMin, residSurfaceMax;

    TextView residRoomsTV, residBedroomsTV;
    SeekBar residRoomsSB, residBedroomsSB;

    // variables
    Bundle filteredArgs = new Bundle();
    List<PlaceholderContent.PlaceholderItem> residList = new ArrayList<>();

    String queryString = new String();
    List<Object> args = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_main, container, false);

        mContext = getActivity();

        db = AppDatabase.getInstance(mContext);
        residenceDAO = db.residenceDAO();

        filteredArgs.clear();
        queryString += "SELECT * FROM residence";

        residTypeTV = view.findViewById(R.id.dialog_resid_type);
        residLocationTV = view.findViewById(R.id.dialog_resid_location);

        residPriceMin = view.findViewById(R.id.dialog_resid_price_min);
        residPriceMax = view.findViewById(R.id.dialog_resid_price_max);

        residSurfaceMin = view.findViewById(R.id.dialog_resid_surface_min);
        residSurfaceMax = view.findViewById(R.id.dialog_resid_surface_max);

        residRoomsTV = view.findViewById(R.id.dialog_tv_rooms);
        residRoomsSB = view.findViewById(R.id.dialog_resid_rooms_seekbar);
        residRoomsSB.setOnSeekBarChangeListener(seekBarRoomsChangeListener);

        residBedroomsTV = view.findViewById(R.id.dialog_tv_bedrooms);
        residBedroomsSB = view.findViewById(R.id.dialog_resid_bedrooms_seekbar);
        residBedroomsSB.setOnSeekBarChangeListener(seekBarBedroomsChangeListener);

        confirmButton = view.findViewById(R.id.dialog_confirm_button);
        cancelButton = view.findViewById(R.id.dialog_cancel_button);
        clearButton = view.findViewById(R.id.dialog_clear_button);

        residList = residenceDAO.getAll();

        this.configureLayout();

        return view;
    }

    private void configureLayout() {
        residTypeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateFilterTypeDialog();
            }
        });

        residLocationTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateFilterLocationDialog();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPriceRange();
                getSurfaceRange();

                ItemListFragment.getFilteredResidList(queryString, args, true);
                getDialog().dismiss();
                Toast.makeText(mContext, "Research complete!", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
                Toast.makeText(mContext, "You cancel your research!", Toast.LENGTH_LONG).show();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filteredArgs.clear();
                ItemListFragment.getFilteredResidList(queryString, args, false);
                getDialog().dismiss();
                Toast.makeText(mContext, "Research cleared!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ------------------
    //  RESID TYPE DIALOG
    // ------------------

    private void onCreateFilterTypeDialog() {
        List<String> residTypeStringList = new ArrayList<>();
        for (int i = 0; i < residList.size(); i++) {
            if (!residTypeStringList.contains(residList.get(i).getResidType())) {
                residTypeStringList.add(residList.get(i).getResidType());
            }
        }
        CharSequence[] residTypeList = residTypeStringList.toArray(new CharSequence[residTypeStringList.size()]);

        new MaterialAlertDialogBuilder(mContext)
                .setTitle("Select Type")
                .setSingleChoiceItems(residTypeList, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        residTypeTV.setText(residTypeStringList.get(i));
                        residTypeTV.setTextColor(getResources().getColor(R.color.purple_500));
                        if (filteredArgs.size() == 0) {
                            filteredArgs.putString("ResidType", residTypeStringList.get(i));
                            queryString += " WHERE";
                        } else {
                            filteredArgs.putString("ResidType", residTypeStringList.get(i));
                            queryString += " AND";
                        }
                        queryString += " resid_type = ?";
                        args.add(residTypeStringList.get(i));
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
    }

    // ------------------
    //  LOCATION DIALOG
    // ------------------

    private void onCreateFilterLocationDialog() {
        List<String> residLocationStringList = new ArrayList<>();
        for (int i = 0; i < residList.size(); i++) {
            if (!residLocationStringList.contains(residList.get(i).getResidLocation())) {
                residLocationStringList.add(residList.get(i).getResidLocation());
            }
        }
        CharSequence[] residLocationList = residLocationStringList.toArray(new CharSequence[residLocationStringList.size()]);

        new MaterialAlertDialogBuilder(mContext)
                .setTitle("Title")
                .setSingleChoiceItems(residLocationList, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        residLocationTV.setText(residLocationStringList.get(i));
                        residLocationTV.setTextColor(getResources().getColor(R.color.purple_500));
                        if (filteredArgs.size() == 0) {
                            filteredArgs.putString("ResidLocation", residLocationStringList.get(i));
                            queryString += " WHERE";
                        } else {
                            filteredArgs.putString("ResidLocation", residLocationStringList.get(i));
                            queryString += " AND";
                        }
                        queryString += " resid_location = ?";
                        args.add(residLocationStringList.get(i));
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
    }

    // ------------------
    //  PRICE RANGE
    // ------------------

    private void getPriceRange() {
        String residPriceMinString = "", residPriceMaxString = "";

        residPriceMinString = residPriceMin.getText().toString();
        residPriceMaxString = residPriceMax.getText().toString();

        if (!residPriceMinString.equals("") || !residPriceMaxString.equals("")) {

            if (filteredArgs.size() == 0) {

                filteredArgs.putString("ResidPriceMin", residPriceMin.getText().toString());
                filteredArgs.putString("ResidPriceMax", residPriceMax.getText().toString());

                queryString += " WHERE";

                if (!residPriceMinString.equals("") && !residPriceMaxString.equals("")) {
                    queryString += " resid_price BETWEEN ? AND ?";
                    args.add(residPriceMinString);
                    args.add(residPriceMaxString);
                } else if (!residPriceMinString.equals("") && residPriceMaxString.equals("")) {
                    queryString += " resid_price >= ?";
                    args.add(residPriceMinString);
                } else if (residPriceMinString.equals("") && !residPriceMaxString.equals("")) {
                    queryString += " resid_price <= ?";
                    args.add(residPriceMaxString);
                }
            } else {

                filteredArgs.putString("ResidPriceMin", residPriceMin.getText().toString());
                filteredArgs.putString("ResidPriceMax", residPriceMax.getText().toString());

                queryString += " AND";

                if (!residPriceMinString.equals("") && !residPriceMaxString.equals("")) {
                    queryString += " resid_price BETWEEN ? AND ?";
                    args.add(residPriceMinString);
                    args.add(residPriceMaxString);
                } else if (!residPriceMinString.equals("") && residPriceMaxString.equals("")) {
                    queryString += " resid_price >= ?";
                    args.add(residPriceMinString);
                } else if (residPriceMinString.equals("") && !residPriceMaxString.equals("")) {
                    queryString += " resid_price <= ?";
                    args.add(residPriceMaxString);
                }
            }
        }
    }

    // ------------------
    //  SURFACE RANGE
    // ------------------

    private void getSurfaceRange() {
        String residSurfaceMinString = "", residSurfaceMaxString = "";

        residSurfaceMinString = residSurfaceMin.getText().toString();
        residSurfaceMaxString = residSurfaceMax.getText().toString();

        if (!residSurfaceMinString.equals("") || !residSurfaceMaxString.equals("")) {
            if (filteredArgs.size() == 0) {

                filteredArgs.putString("ResidSurfaceMin", residSurfaceMin.getText().toString());
                filteredArgs.putString("ResidSurfaceMax", residSurfaceMax.getText().toString());

                queryString += " WHERE";

                if (!residSurfaceMinString.equals("") && !residSurfaceMaxString.equals("")) {
                    queryString += " resid_surface BETWEEN ? AND ?";
                    args.add(residSurfaceMinString);
                    args.add(residSurfaceMaxString);
                } else if (!residSurfaceMinString.equals("") && residSurfaceMaxString.equals("")) {
                    queryString += " resid_surface >= ?";
                    args.add(residSurfaceMinString);
                } else if (residSurfaceMinString.equals("") && !residSurfaceMaxString.equals("")) {
                    queryString += " resid_surface <= ?";
                    args.add(residSurfaceMaxString);
                }
            } else {

                filteredArgs.putString("ResidSurfaceMin", residSurfaceMin.getText().toString());
                filteredArgs.putString("ResidSurfaceMax", residSurfaceMax.getText().toString());

                queryString += " AND";

                if (!residSurfaceMinString.equals("") && !residSurfaceMaxString.equals("")) {
                    queryString += " resid_surface BETWEEN ? AND ?";
                    args.add(residSurfaceMinString);
                    args.add(residSurfaceMaxString);
                } else if (!residSurfaceMinString.equals("") && residSurfaceMaxString.equals("")) {
                    queryString += " resid_surface >= ?";
                    args.add(residSurfaceMinString);
                } else if (residSurfaceMinString.equals("") && !residSurfaceMaxString.equals("")) {
                    queryString += " resid_surface <= ?";
                    args.add(residSurfaceMaxString);
                }
            }
        }
    }

    // ------------------
    //  ROOMS SEEKBAR
    // ------------------

    SeekBar.OnSeekBarChangeListener seekBarRoomsChangeListener = new SeekBar.OnSeekBarChangeListener() {
        String residRooms = "";

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            residRooms = String.valueOf(i);
            if (i == 1) {
                residRoomsTV.setText("Number of Rooms : Studio");
            } else if (i == 5) {
                residRoomsTV.setText("Number of Rooms : 5+");
            } else {
                residRoomsTV.setText("Number of Rooms : " + i);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (residRooms.equals("5")) {
                if (filteredArgs.size() == 0) {
                    filteredArgs.putString("ResidRooms", "5");
                    queryString += " WHERE";
                } else {
                    filteredArgs.putString("ResidRooms", "5");
                    queryString += " AND";
                }
                queryString += " resid_rooms >= ?";
                args.add("5");

                residRoomsTV.setText("Number of Rooms : 5+");

            } else {
                if (filteredArgs.size() == 0) {
                    filteredArgs.putString("ResidRooms", residRooms);
                    queryString += " WHERE";
                } else {
                    filteredArgs.putString("ResidRooms", residRooms);
                    queryString += " AND";
                }
                queryString += " resid_rooms = ?";
                args.add(residRooms);

                if (residRooms == "1") {
                    residRoomsTV.setText("Number of Rooms : Studio");
                } else {
                    residRoomsTV.setText("Number of Rooms : " + residRooms);
                }
            }
        }
    };

    // ------------------
    //  BEDROOMS SEEKBAR
    // ------------------

    SeekBar.OnSeekBarChangeListener seekBarBedroomsChangeListener = new SeekBar.OnSeekBarChangeListener() {
        String residBedrooms = "";

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            residBedrooms = String.valueOf(i);
            if (i == 5) {
                residBedroomsTV.setText("Number of Bedrooms : 5+");
            } else {
                residBedroomsTV.setText("Number of Bedrooms : " + i);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (residBedrooms.equals("5")) {
                if (filteredArgs.size() == 0) {
                    filteredArgs.putString("ResidBedrooms", "5");
                    queryString += " WHERE";
                } else {
                    filteredArgs.putString("ResidBedrooms", "5");
                    queryString += " AND";
                }
                queryString += " resid_bedrooms >= ?";
                args.add("5");

                residBedroomsTV.setText("Number of Bedrooms : 5+");

            } else {
                if (filteredArgs.size() == 0) {
                    filteredArgs.putString("ResidBedrooms", residBedrooms);
                    queryString += " WHERE";
                } else {
                    filteredArgs.putString("ResidBedrooms", residBedrooms);
                    queryString += " AND";
                }
                queryString += " resid_bedrooms = ?";
                args.add(residBedrooms);

                residBedroomsTV.setText("Number of Bedrooms : " + residBedrooms);
            }
        }
    };
}
