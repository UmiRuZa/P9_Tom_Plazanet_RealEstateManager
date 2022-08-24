package com.openclassrooms.realestatemanager.roomdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import androidx.room.Database;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.openclassrooms.realestatemanager.placeholder.PlaceholderContent;

@Database(entities = {PlaceholderContent.PlaceholderItem.class}, version = 5)
public abstract class AppDatabase extends RoomDatabase {
    public static AppDatabase database;

    private static String DATABASE_NAME = "REM_database";

    public synchronized static AppDatabase getInstance(Context context) {
        if (database == null) {
            database = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .addCallback(CALLBACK)
                    .build();
        }

        return database;
    }

    private static Callback CALLBACK = new Callback() {
        @Override
        public void onCreate(SupportSQLiteDatabase db) {
            super.onCreate(db);
            initialiseResidence(db);
        }

        @Override
        public void onOpen(SupportSQLiteDatabase db) {
            super.onOpen(db);
            Cursor csr = db.query("SELECT count(*) FROM " + PlaceholderContent.PlaceholderItem.TABLE_NAME);
            if (csr.moveToFirst()) {
                long row_count = csr.getLong(0);
                if (row_count != PlaceholderContent.PlaceholderItem.DEFINED_RESIDENCE.length) initialiseResidence(db);
            }
            csr.close();
        }
    };

    private static void initialiseResidence(SupportSQLiteDatabase db) {
        boolean alreadyInTransaction = db.inTransaction();
        ContentValues cv = new ContentValues();
        if (!alreadyInTransaction) db.beginTransaction();
        for(PlaceholderContent.PlaceholderItem p: PlaceholderContent.PlaceholderItem.DEFINED_RESIDENCE) {
            cv.clear();
            cv.put(PlaceholderContent.PlaceholderItem.RESID_ID,p.getId());
            cv.put(PlaceholderContent.PlaceholderItem.RESID_TYPE,p.getResidType());
            cv.put(PlaceholderContent.PlaceholderItem.RESID_LOCATION,p.getResidLocation());
            cv.put(PlaceholderContent.PlaceholderItem.RESID_ADDRESS,p.getResidAddress());
            cv.put(PlaceholderContent.PlaceholderItem.RESID_PRICE,p.getResidPrice());
            cv.put(PlaceholderContent.PlaceholderItem.RESID_DESCRIPTION,p.getResidDescription());
            cv.put(PlaceholderContent.PlaceholderItem.RESID_SURFACE,p.getResidSurface());
            cv.put(PlaceholderContent.PlaceholderItem.RESID_ROOMS,p.getResidRooms());
            cv.put(PlaceholderContent.PlaceholderItem.RESID_BATHROOMS,p.getResidBathrooms());
            cv.put(PlaceholderContent.PlaceholderItem.RESID_BEDROOMS,p.getResidBedrooms());
            db.insert(PlaceholderContent.PlaceholderItem.TABLE_NAME, OnConflictStrategy.IGNORE,cv);
        }
        if (!alreadyInTransaction) {
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    public abstract ResidenceDAO residenceDAO();
}
