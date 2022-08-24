package com.openclassrooms.realestatemanager;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.openclassrooms.realestatemanager.roomdb.AppDatabase;
import com.openclassrooms.realestatemanager.roomdb.itemProvider.ItemContentProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

public class ItemContentProviderTest {

    // FOR DATA

    private ContentResolver mContentResolver;

    // DATA SET FOR TEST

    private static long USER_ID = 465;

    @Before

    public void setUp() {

        Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getContext()
                        ,

                        AppDatabase.class)


                .allowMainThreadQueries()


                .build();


        mContentResolver = InstrumentationRegistry.getInstrumentation().getContext()

                .getContentResolver();

    }

    @Test

    public void getItemsWhenNoItemInserted() {

        final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(ItemContentProvider.URI_ITEM, USER_ID), null, null, null, null);

        assertThat(cursor, notNullValue());

        assertThat(cursor.getCount(), is(0));

        cursor.close();

    }

    @Test

    public void insertAndGetItem() {

        // BEFORE : Adding demo item

        final Uri userUri = mContentResolver.insert(ItemContentProvider.URI_ITEM, generateItem());

        // TEST

        final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(ItemContentProvider.URI_ITEM, USER_ID), null, null, null, null);

        assertThat(cursor, notNullValue());

        assertThat(cursor.getCount(), is(1));

        assertThat(cursor.moveToFirst(), is(true));

        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("resid_type")), is("VILLA"));

    }

    // ---

    private ContentValues generateItem(){

        final ContentValues values = new ContentValues();

        values.put("resid_id", "465");

        values.put("resid_type", "VILLA");

        values.put("resid_location", "FRENCH RIVIERA");

        values.put("resid_address", "18 Bd Princesse Grâce de Monaco, 06300 Nice");

        values.put("resid_price", "20790900");

        values.put("resid_description", "This outstanding “Belle Epoque” style property right at the waters’ edge, with an infinity pool (11m x 5m) offers a stunning panoramic view.");

        values.put("resid_surface", "3445");

        values.put("resid_rooms", "7");

        values.put("resid_bathrooms", "1");

        values.put("resid_bedrooms", "5");

        return values;

    }

}