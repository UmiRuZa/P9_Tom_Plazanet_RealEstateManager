package com.openclassrooms.realestatemanager.UI.fragments;


import static com.openclassrooms.realestatemanager.BuildConfig.MAP_API_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.openclassrooms.realestatemanager.BuildConfig;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.UI.ItemDetailHostActivity;
import com.openclassrooms.realestatemanager.placeholder.PlaceholderContent;
import com.openclassrooms.realestatemanager.roomdb.AppDatabase;
import com.openclassrooms.realestatemanager.roomdb.ResidenceDAO;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    double lat, lng;

    Activity activity;
    GoogleMap mMap;

    private static final String TAG = "MainActivity";
    FusedLocationProviderClient fusedLocationProviderClient;

    AppDatabase db;
    ResidenceDAO residenceDAO;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        activity = getActivity();

        db = AppDatabase.getInstance(activity);
        residenceDAO = db.residenceDAO();

        ((ItemDetailHostActivity)getActivity()).configureToolbar();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        updateUIWithMarker();
    }

    // ------------------
    //  UPDATE UI
    // ------------------

    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p1;

    }

    private void updateUIWithMarker() {
        int height = 175;
        int width = 175;
        @SuppressWarnings("deprecation") @SuppressLint("UseCompatLoadingForDrawables")
        BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_residence_marker);
        Bitmap b = bitmapDraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        List<PlaceholderContent.PlaceholderItem> mResidences = new ArrayList<>();
        mResidences = residenceDAO.getAll();

        for (PlaceholderContent.PlaceholderItem residences : mResidences) {
            LatLng restPos = getLocationFromAddress(activity, residences.getResidAddress());
            mMap.addMarker(new MarkerOptions()
                    .position(restPos)
                    .title(residences.getResidType())
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getLocationFromAddress(
                activity, residenceDAO.getAll().get(0).getResidAddress()), 16));
    }
}