package com.openclassrooms.realestatemanager.UI.fragments;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.sqlite.db.SimpleSQLiteQuery;

import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.UI.ItemDetailHostActivity;
import com.openclassrooms.realestatemanager.databinding.FragmentItemListBinding;
import com.openclassrooms.realestatemanager.databinding.ItemListContentBinding;

import com.openclassrooms.realestatemanager.placeholder.PlaceholderContent;
import com.openclassrooms.realestatemanager.roomdb.AppDatabase;
import com.openclassrooms.realestatemanager.roomdb.ResidenceDAO;
import com.openclassrooms.realestatemanager.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ItemListFragment extends Fragment {

    static SharedPreferences sharedPref;
    static AppDatabase db;
    static ResidenceDAO residenceDAO;
    static SimpleItemRecyclerViewAdapter adapter;

    ViewCompat.OnUnhandledKeyEventListenerCompat unhandledKeyEventListenerCompat = (v, event) -> {
        if (event.getKeyCode() == KeyEvent.KEYCODE_Z && event.isCtrlPressed()) {
            Toast.makeText(
                    v.getContext(),
                    "Undo (Ctrl + Z) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_F && event.isCtrlPressed()) {
            Toast.makeText(
                    v.getContext(),
                    "Find (Ctrl + F) shortcut triggered",
                    Toast.LENGTH_LONG
            ).show();
            return true;
        }
        return false;
    };

    private FragmentItemListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentItemListBinding.inflate(inflater, container, false);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        db = AppDatabase.getInstance(getActivity());
        residenceDAO = db.residenceDAO();

        ((ItemDetailHostActivity)getActivity()).configureToolbar();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewCompat.addOnUnhandledKeyEventListener(view, unhandledKeyEventListenerCompat);

        RecyclerView recyclerView = binding.itemList;

        // Leaving this not using view binding as it relies on if the view is visible the current
        // layout configuration (layout, layout-sw600dp)
        View itemDetailFragmentContainer = view.findViewById(R.id.item_detail_nav_container);

        /* Click Listener to trigger navigation based on if you have
         * a single pane layout or two pane layout
         */
        View.OnClickListener onClickListener = itemView -> {
            PlaceholderContent.PlaceholderItem item =
                    (PlaceholderContent.PlaceholderItem) itemView.getTag();
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.id);
            if (itemDetailFragmentContainer != null) {
                Navigation.findNavController(itemDetailFragmentContainer)
                        .navigate(R.id.fragment_item_detail, arguments);
            } else {
                Navigation.findNavController(itemView).navigate(R.id.show_item_detail, arguments);
            }
        };

        /*
         * Context click listener to handle Right click events
         * from mice and trackpad input to provide a more native
         * experience on larger screen devices
         */
        View.OnContextClickListener onContextClickListener = itemView -> {
            PlaceholderContent.PlaceholderItem item =
                    (PlaceholderContent.PlaceholderItem) itemView.getTag();
            Toast.makeText(
                    itemView.getContext(),
                    "Context click of item " + item.id,
                    Toast.LENGTH_LONG
            ).show();
            return true;
        };

        setupRecyclerView(recyclerView, onClickListener, onContextClickListener);
    }

    private void setupRecyclerView(
            RecyclerView recyclerView,
            View.OnClickListener onClickListener,
            View.OnContextClickListener onContextClickListener
    ) {
        adapter= new SimpleItemRecyclerViewAdapter(
                PlaceholderContent.ITEMS,
                onClickListener,
                onContextClickListener
        );
        recyclerView.setAdapter(adapter);
    }

    // -- PRICE CONVERTER UTILS --

    public static String setPrice(String residPrice) {
        String residPriceConverted = "";

        if (sharedPref.getBoolean("Convert", false) && !Objects.equals(residPrice, "")) {
            residPriceConverted = String.format("%,d", Utils.convertDollarToEuro(Integer.parseInt(residPrice))) + " €";
        } else if (Objects.equals(residPrice, "")) {
            residPriceConverted = "no price";
        } else {
            residPriceConverted = "$ " + String.format("%,d", Long.parseLong(residPrice));
        }

        return residPriceConverted;
    }

    // Update rv when list has been filtered

    public static void getFilteredResidList(String queryString, List<Object> args, boolean isSearched) {
        List<PlaceholderContent.PlaceholderItem> residenceList;

        if (isSearched) {
            SimpleSQLiteQuery query = new SimpleSQLiteQuery(queryString, args.toArray());
            residenceList = residenceDAO.getFilterResidence(query);

            PlaceholderContent.filterItem(residenceList);
        } else {
            PlaceholderContent.updateList();
        }

        adapter.notifyDataSetChanged();
    }

    // Update rv when list has been filtered

    public static void getResidList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ------------------
    //  SIMPLE RV ADAPTER
    // ------------------

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<PlaceholderContent.PlaceholderItem> mValues;
        private final View.OnClickListener mOnClickListener;
        private final View.OnContextClickListener mOnContextClickListener;

        private ArrayList<String> residPictures = new ArrayList<>();

        SimpleItemRecyclerViewAdapter(List<PlaceholderContent.PlaceholderItem> items,
                                      View.OnClickListener onClickListener,
                                      View.OnContextClickListener onContextClickListener) {
            mValues = items;
            mOnClickListener = onClickListener;
            mOnContextClickListener = onContextClickListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            ItemListContentBinding binding =
                    ItemListContentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

            return new ViewHolder(binding);

        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            residPictures = getListOfPictureUri(position);

            if (residPictures.size() != 0) {
                holder.mPictureView.setImageURI(Uri.parse(residPictures.get(0)));
            } else {
                holder.mPictureView.setImageResource(R.drawable.alona_gross_jpshfuads9i_unsplash_jpg);
            }


            holder.mIdView.setText(mValues.get(position).residType);
            holder.mContentView.setText(ItemListFragment.setPrice(mValues.get(position).residPrice));
            holder.mLocationView.setText(mValues.get(position).residLocation);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.itemView.setOnContextClickListener(mOnContextClickListener);
            }
            holder.itemView.setOnLongClickListener(v -> {
                // Setting the item id as the clip data so that the drop target is able to
                // identify the id of the content
                ClipData.Item clipItem = new ClipData.Item(mValues.get(position).id);
                ClipData dragData = new ClipData(
                        ((PlaceholderContent.PlaceholderItem) v.getTag()).residType,
                        new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                        clipItem
                );

                if (Build.VERSION.SDK_INT >= 24) {
                    v.startDragAndDrop(
                            dragData,
                            new View.DragShadowBuilder(v),
                            null,
                            0
                    );
                } else {
                    v.startDrag(
                            dragData,
                            new View.DragShadowBuilder(v),
                            null,
                            0
                    );
                }
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView mPictureView;
            final TextView mIdView;
            final TextView mContentView;
            final TextView mLocationView;

            ViewHolder(ItemListContentBinding binding) {
                super(binding.getRoot());
                mPictureView = binding.listImageView;
                mIdView = binding.idText;
                mContentView = binding.content;
                mLocationView = binding.location;
            }

        }

        // Convert jsonArray to List<String>

        private ArrayList<String> getListOfPictureUri(int position) {
            String retrieveResidPictureString = mValues.get(position).residPicture;
            Type type = new TypeToken<JSONArray>() {
            }.getType();
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
    }
}