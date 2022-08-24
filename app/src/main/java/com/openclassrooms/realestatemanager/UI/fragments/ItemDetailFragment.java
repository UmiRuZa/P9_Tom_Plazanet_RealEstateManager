package com.openclassrooms.realestatemanager.UI.fragments;

import static com.openclassrooms.realestatemanager.BuildConfig.MAP_API_KEY;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.DragEvent;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.UI.AddRealEstateActivity;
import com.openclassrooms.realestatemanager.UI.ItemDetailHostActivity;
import com.openclassrooms.realestatemanager.placeholder.PlaceholderContent;
import com.openclassrooms.realestatemanager.databinding.FragmentItemDetailBinding;
import com.openclassrooms.realestatemanager.roomdb.AppDatabase;
import com.openclassrooms.realestatemanager.roomdb.ResidenceDAO;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ItemDetailFragment extends Fragment {

    AppDatabase db = AppDatabase.getInstance(getActivity());
    ResidenceDAO residenceDAO = db.residenceDAO();

    public static final String ARG_ITEM_ID = "item_id";

    private static PlaceholderContent.PlaceholderItem mItem;
    private CarouselView mCarouselViewPictures;
    private TextView mTextViewType;
    private ArrayList<String> residPictures = new ArrayList<>();
    private TextView mTextViewDescription;
    private TextView mTextViewSurface;
    private TextView mTextViewRooms;
    private TextView mTextViewBathrooms;
    private TextView mTextViewBedrooms;
    private TextView mTextViewAddress;
    private ImageView mImageViewStaticMap;

    private final View.OnDragListener dragListener = (v, event) -> {
        if (event.getAction() == DragEvent.ACTION_DROP) {
            ClipData.Item clipDataItem = event.getClipData().getItemAt(0);
            mItem = PlaceholderContent.ITEM_MAP.get(clipDataItem.getText().toString());
            updateContent();
        }
        return true;
    };
    private FragmentItemDetailBinding binding;

    public ItemDetailFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the placeholder content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = PlaceholderContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }

        ((ItemDetailHostActivity)getActivity()).configureToolbar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentItemDetailBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        mCarouselViewPictures = (CarouselView) binding.fragmentResidImageView;
        mTextViewType = binding.fragmentResidType;
        mTextViewDescription = binding.fragmentResidDescription;
        mTextViewSurface = binding.fragmentResidSurface;
        mTextViewRooms = binding.fragmentResidRooms;
        mTextViewBathrooms = binding.fragmentResidBathrooms;
        mTextViewBedrooms = binding.fragmentResidBedrooms;
        mTextViewAddress = binding.fragmentResidAddress;
        mImageViewStaticMap = binding.fragmentResidStaticMap;

        // Show the placeholder content as text in a TextView & in the toolbar if available.
        updateContent();
        rootView.setOnDragListener(dragListener);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateContent() {
        if (mItem != null) {
            updateImageView();

            mTextViewType.setText(mItem.residType);
            mTextViewDescription.setText(mItem.residDescription);
            mTextViewSurface.setText(mItem.residSurface +" m²");
            mTextViewRooms.setText(mItem.residRooms);
            mTextViewBathrooms.setText(mItem.residBathrooms);
            mTextViewBedrooms.setText(mItem.residBedrooms);
            mTextViewAddress.setText(mItem.residAddress);

            LatLng residenceLatLng = getLocationFromAddress(getActivity(), mItem.getResidAddress());
            String url = "https://maps.google.com/maps/api/staticmap?center=" + residenceLatLng.latitude + "," + residenceLatLng.longitude + "&zoom=15&size=200x200&markers=small%7C" + residenceLatLng.latitude + "," + residenceLatLng.longitude + "&format=jpg&key=" + MAP_API_KEY;
            Picasso.get().load(url).into(mImageViewStaticMap);
        } else {
            mItem = residenceDAO.getAll().get(0);
            updateContent();
        }
    }

    private void updateImageView() {
        residPictures = getListOfPictureUri(mItem);
        if (residPictures.size() != 0) {
            mCarouselViewPictures.setPageCount(residPictures.size());
            mCarouselViewPictures.setImageListener(new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    imageView.setImageURI(Uri.parse(residPictures.get(position)));
                }
            });
        } else {
            mCarouselViewPictures.setPageCount(1);
            mCarouselViewPictures.setImageListener(new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    imageView.setImageResource(R.drawable.alona_gross_jpshfuads9i_unsplash_jpg);
                }
            });
        }
    }

    // MAP STATIC API

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

    // Convert jsonArray to list<String>

    private ArrayList<String> getListOfPictureUri(PlaceholderContent.PlaceholderItem mItem) {
        String retrieveResidPictureString = mItem.residPicture;
        Type type = new TypeToken<JSONArray>(){}.getType();
        Gson gson = new Gson();
        JSONArray jsonArray = gson.fromJson(retrieveResidPictureString, type);

        ArrayList<String> mResidPictures = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    mResidPictures.add(jsonArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return mResidPictures;
    }

    public static PlaceholderContent.PlaceholderItem getCurrentResidence(){
        return mItem;
    }
}