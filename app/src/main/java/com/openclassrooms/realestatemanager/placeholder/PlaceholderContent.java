package com.openclassrooms.realestatemanager.placeholder;


import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.openclassrooms.realestatemanager.utils.MyApplication;
import com.openclassrooms.realestatemanager.roomdb.AppDatabase;
import com.openclassrooms.realestatemanager.roomdb.ResidenceDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlaceholderContent {

    static AppDatabase db = AppDatabase.getInstance(MyApplication.getAppContext());
    static ResidenceDAO residenceDAO = db.residenceDAO();

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<PlaceholderItem> ITEMS = new ArrayList<PlaceholderItem>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<String, PlaceholderItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = residenceDAO.getAll().size();

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createPlaceholderItem(i));
        }
    }

    // ------------------
    //  LIST METHODS
    // ------------------

    public static void addItem(PlaceholderItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void deleteItem(PlaceholderItem item) {
        ITEMS.remove(item);
    }

    public static void updateList() {
        ITEMS.clear();
        ITEMS.addAll(residenceDAO.getAll());
    }

    public static void filterItem(List<PlaceholderItem> listItem) {
        ITEMS.clear();
        ITEMS.addAll(listItem);
    }

    private static PlaceholderItem createPlaceholderItem(int position) {
        return new PlaceholderItem(
                residenceDAO.getAll().get(position - 1).getId(),
                residenceDAO.getAll().get(position - 1).getResidPicture(),
                residenceDAO.getAll().get(position - 1).getResidType(),
                residenceDAO.getAll().get(position - 1).getResidLocation(),
                residenceDAO.getAll().get(position - 1).getResidAddress(),
                residenceDAO.getAll().get(position - 1).getResidPrice(),
                residenceDAO.getAll().get(position - 1).getResidDescription(),
                residenceDAO.getAll().get(position - 1).getResidSurface(),
                residenceDAO.getAll().get(position - 1).getResidRooms(),
                residenceDAO.getAll().get(position - 1).getResidBathrooms(),
                residenceDAO.getAll().get(position - 1).getResidBedrooms()
        );
    }


    // ------------------
    //  PLACEHOLDER ITEM
    // ------------------

    /**
     * A placeholder item representing a piece of content.
     */
    @Entity(tableName = PlaceholderItem.TABLE_NAME)
    public static class PlaceholderItem {

        public static final String TABLE_NAME = "residence";
        public static final String RESID_ID = "resid_id";
        public static final String RESID_PICTURE = "resid_picture";
        public static final String RESID_TYPE = "resid_type";
        public static final String RESID_LOCATION = "resid_location";
        public static final String RESID_ADDRESS = "resid_address";
        public static final String RESID_PRICE = "resid_price";
        public static final String RESID_DESCRIPTION = "resid_description";
        public static final String RESID_SURFACE = "resid_surface";
        public static final String RESID_ROOMS = "resid_rooms";
        public static final String RESID_BATHROOMS = "resid_bathrooms";
        public static final String RESID_BEDROOMS = "resid_bedrooms";

        public static final PlaceholderItem[] DEFINED_RESIDENCE = {
                new PlaceholderItem("123", "", "Penthouse", "Upper East Side", "1540 2nd Ave, New York, NY 10028, USA", "29872000",
                        "It's a good home !", "750", "8", "2", "4"),
                new PlaceholderItem("125", "", "House", "Montauk", "60 Bryan Rd, Montauk, NY 11954, USA", "21130000",
                        "The perfect House !", "800", "16", "4", "8"),
                new PlaceholderItem("153","" , "Duplex", "Brooklyn", "289 Division Ave, Brooklyn, NY 11211, USA", "13990000",
                        "A perfect placed one !", "640", "6", "2", "3"),
                new PlaceholderItem("176","", "Flat", "Manhattan", "80 Grove St, New York, NY 10014, USA", "17870000",
                        "A lovely apartment !", "350", "8", "2", "3")
        };

        @PrimaryKey
        @ColumnInfo(name = RESID_ID)
        @NonNull
        public String id;

        @ColumnInfo(name = RESID_PICTURE)
        public String residPicture;

        @ColumnInfo(name = RESID_TYPE)
        public String residType;

        @ColumnInfo(name = RESID_LOCATION)
        public String residLocation;

        @ColumnInfo(name = RESID_ADDRESS)
        public String residAddress;

        @ColumnInfo(name = RESID_PRICE)
        public String residPrice;

        @ColumnInfo(name = RESID_DESCRIPTION)
        public String residDescription;

        @ColumnInfo(name = RESID_SURFACE)
        public String residSurface;

        @ColumnInfo(name = RESID_ROOMS)
        public String residRooms;

        @ColumnInfo(name = RESID_BATHROOMS)
        public String residBathrooms;

        @ColumnInfo(name = RESID_BEDROOMS)
        public String residBedrooms;

        public PlaceholderItem(String id, String residPicture, String residType, String residLocation, String residAddress, String residPrice, String residDescription,
                               String residArea, String residRooms, String residBathrooms, String residBedrooms) {
            this.id = id;
            this.residPicture = residPicture;
            this.residType = residType;
            this.residLocation = residLocation;
            this.residAddress = residAddress;
            this.residPrice = residPrice;
            this.residDescription = residDescription;
            this.residSurface = residArea;
            this.residRooms = residRooms;
            this.residBathrooms = residBathrooms;
            this.residBedrooms = residBedrooms;
        }

        public PlaceholderItem() {
        }

        @Nullable
        public static PlaceholderItem getProjectById(String id) {
            for (PlaceholderItem residence : DEFINED_RESIDENCE) {
                if (residence.id.equals(id))
                    return residence;
            }
            return null;
        }


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getResidPicture() {
            return residPicture;
        }

        public void setResidPicture(String residPicture) {
            this.residPicture = residPicture;
        }

        public String getResidType() {
            return residType;
        }

        public void setResidType(String residType) {
            this.residType = residType;
        }

        public String getResidLocation() {
            return residLocation;
        }

        public void setResidLocation(String residLocation) {
            this.residLocation = residLocation;
        }

        public String getResidAddress() {
            return residAddress;
        }

        public void setResidAddress(String residAddress) {
            this.residAddress = residAddress;
        }

        public String getResidPrice() {
            return residPrice;
        }

        public void setResidPrice(String residPrice) {
            this.residPrice = residPrice;
        }

        public String getResidDescription() {
            return residDescription;
        }

        public void setResidDescription(String residDescription) {
            this.residDescription = residDescription;
        }

        public String getResidSurface() {
            return residSurface;
        }

        public void setResidSurface(String residSurface) {
            this.residSurface = residSurface;
        }

        public String getResidRooms() {
            return residRooms;
        }

        public void setResidRooms(String residRooms) {
            this.residRooms = residRooms;
        }

        public String getResidBathrooms() {
            return residBathrooms;
        }

        public void setResidBathrooms(String residBathrooms) {
            this.residBathrooms = residBathrooms;
        }

        public String getResidBedrooms() {
            return residBedrooms;
        }

        public void setResidBedrooms(String residBedrooms) {
            this.residBedrooms = residBedrooms;
        }

        @Override
        public String toString() {
            return residType;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PlaceholderItem that = (PlaceholderItem) o;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        //--- CONTENT PROVIDER UTILS ---
        public static PlaceholderItem fromContentValues(ContentValues values) {
            final PlaceholderItem item = new PlaceholderItem();

            if(values.containsKey("resid_id")) item.setId(values.getAsString("resid_id"));
            if(values.containsKey("resid_picture")) item.setResidPicture(values.getAsString("resid_picture"));
            if(values.containsKey("resid_type")) item.setResidType(values.getAsString("resid_type"));
            if(values.containsKey("resid_location")) item.setResidLocation(values.getAsString("resid_location"));
            if(values.containsKey("resid_address")) item.setResidAddress(values.getAsString("resid_address"));
            if(values.containsKey("resid_price")) item.setResidPrice(values.getAsString("resid_price"));
            if(values.containsKey("resid_description")) item.setResidDescription(values.getAsString("resid_description"));
            if(values.containsKey("resid_surface")) item.setResidSurface(values.getAsString("resid_surface"));
            if(values.containsKey("resid_rooms")) item.setResidRooms(values.getAsString("resid_rooms"));
            if(values.containsKey("resid_bathrooms")) item.setResidBathrooms(values.getAsString("resid_bathrooms"));
            if(values.containsKey("resid_bedrooms")) item.setResidBedrooms(values.getAsString("resid_bedrooms"));

            return item;
        }
    }
}