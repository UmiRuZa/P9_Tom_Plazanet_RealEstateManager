package com.openclassrooms.realestatemanager.roomdb.itemProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.openclassrooms.realestatemanager.placeholder.PlaceholderContent;
import com.openclassrooms.realestatemanager.roomdb.AppDatabase;
import com.openclassrooms.realestatemanager.roomdb.ResidenceDAO;

public class ItemContentProvider extends ContentProvider {

    // FOR DATA
    public static final String AUTHORITY = "com.openclassrooms.realestatemanager";
    public static final String TABLE_NAME = PlaceholderContent.PlaceholderItem.class.getSimpleName();
    public static final Uri URI_ITEM = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

    @Override
    public boolean onCreate() { return true; }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        if (getContext() != null) {
            long residID = ContentUris.parseId(uri);
            final Cursor cursor = AppDatabase.getInstance(getContext()).residenceDAO().getItemsWithCursor(residID);
            cursor.setNotificationUri(getContext().getContentResolver(), uri);

            return cursor;
        }
        throw new IllegalArgumentException("Failed to query row from uri" + uri);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "vnd.android.cursor.residence/" + AUTHORITY + "." + TABLE_NAME;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        if (getContext() != null) {
            final long id = AppDatabase.getInstance(getContext()).residenceDAO().insertAll(PlaceholderContent.PlaceholderItem.fromContentValues(contentValues));

            if (id != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            }
        }

        throw new IllegalArgumentException("Failed to deleteResidence rom into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        if (getContext() != null) {

            final int count = AppDatabase.getInstance(getContext()).residenceDAO().deleteResidence(ContentUris.parseId(uri));

            getContext().getContentResolver().notifyChange(uri, null);

            return count;

        }

        throw new IllegalArgumentException("Failed to deleteResidence row into " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        if (getContext() != null && contentValues != null) {

            final int count = AppDatabase.getInstance(getContext()).residenceDAO().update(PlaceholderContent.PlaceholderItem.fromContentValues(contentValues));

            getContext().getContentResolver().notifyChange(uri, null);

            return count;

        }

        throw new IllegalArgumentException("Failed to update row into " + uri);
    }
}
