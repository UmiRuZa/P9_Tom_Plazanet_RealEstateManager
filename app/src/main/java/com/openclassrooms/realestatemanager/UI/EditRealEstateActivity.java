package com.openclassrooms.realestatemanager.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.UI.fragments.ItemListFragment;
import com.openclassrooms.realestatemanager.placeholder.PlaceholderContent;
import com.openclassrooms.realestatemanager.roomdb.AppDatabase;
import com.openclassrooms.realestatemanager.roomdb.ResidenceDAO;

import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditRealEstateActivity extends AppCompatActivity {

    AppDatabase db;
    ResidenceDAO residenceDAO;

    static PlaceholderContent.PlaceholderItem residence;

    String residPicture;
    TextInputEditText editResidType;
    TextInputEditText editResidSurface;
    TextInputEditText editResidRooms;
    TextInputEditText editResidBathrooms;
    TextInputEditText editResidBedrooms;
    TextInputEditText editResidLocation;
    TextInputEditText editResidAddress;
    TextInputEditText editResidDescription;
    TextInputEditText editResidPrice;

    Button saveButton;

    private static final int GALLERY_REQUEST = 100;
    private static final int CAMERA_REQUEST = 200;
    Uri photoURI;
    List<Uri> mArrayUri;
    int position = 0;

    String currentPhotoPath;

    ImageView residPicturesIV;
    Button openGalleryButton;
    Button openCameraButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_residence);

        db = AppDatabase.getInstance(this);
        residenceDAO = db.residenceDAO();

        mArrayUri = new ArrayList<>();

        residPicturesIV = findViewById(R.id.editResidPicturesIV);

        openGalleryButton = findViewById(R.id.editResidPicturesButtonGallery);
        openGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        openCameraButton = findViewById(R.id.editResidPicturesButtonCamera);
        openCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

        this.configureLayout();
    }

    public static void setResidence(PlaceholderContent.PlaceholderItem getResidence) {
        residence = getResidence;
    }

    // ------------------
    //  CONFIGURE PICTURES
    // ------------------

    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(i, "Select picture"), GALLERY_REQUEST);
    }

    private void openCamera() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (i.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                i.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(i, CAMERA_REQUEST);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            // Get the Image from data
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    // adding imageUri in array
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    mArrayUri.add(imageUri);
                }
            } else {
                Uri imageUri = data.getData();
                mArrayUri.add(imageUri);
            }

            // setting 1st selected image into imageView
            residPicturesIV.setImageURI(mArrayUri.get(0));
            position = 0;
        } else {
            // show this if no image is selected
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            residPicturesIV.setImageURI(photoURI);
            mArrayUri.add(photoURI);
        }

        ArrayList<String> mArrayString = new ArrayList<>();
        for (int i = 0; i < mArrayUri.size(); i++) {
            mArrayString.add(String.valueOf(mArrayUri.get(i)));
        }
        JSONArray jsonArray = new JSONArray(mArrayString);
        residPicture = new Gson().toJson(jsonArray);
    }

    // Create internal file for camera taken pictures

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // ------------------
    //  CONFIGURE LAYOUT
    // ------------------

    private void configureLayout() {
        editResidType = findViewById(R.id.editResidType);
        editResidType.setText(residence.residType);

        editResidSurface = findViewById(R.id.editResidSurface);
        editResidSurface.setText(residence.residSurface);

        editResidRooms = findViewById(R.id.editResidRooms);
        editResidRooms.setText(residence.residRooms);

        editResidBathrooms = findViewById(R.id.editResidBathrooms);
        editResidBathrooms.setText(residence.residBathrooms);

        editResidBedrooms = findViewById(R.id.editResidBedrooms);
        editResidBedrooms.setText(residence.residBedrooms);

        editResidLocation = findViewById(R.id.editResidLocation);
        editResidLocation.setText(residence.residLocation);

        editResidAddress = findViewById(R.id.editResidAddress);
        editResidAddress.setText(residence.residAddress);

        editResidDescription = findViewById(R.id.editResidDescription);
        editResidDescription.setText(residence.residDescription);

        editResidPrice = findViewById(R.id.editResidPrice);
        editResidPrice.setText(residence.residPrice);

        saveButton = findViewById(R.id.editResidSaveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editResidence();
                Intent i = new Intent(EditRealEstateActivity.this, ItemDetailHostActivity.class);
                startActivity(i);
            }
        });
    }

    // ------------------
    //  EDIT RESIDENCE
    // ------------------

    private void editResidence() {
        PlaceholderContent.PlaceholderItem editedResidence = new PlaceholderContent.PlaceholderItem(
                residence.id,
                residPicture,
                editResidType.getText().toString(),
                editResidLocation.getText().toString(),
                editResidAddress.getText().toString(),
                editResidPrice.getText().toString(),
                editResidDescription.getText().toString(),
                editResidSurface.getText().toString(),
                editResidRooms.getText().toString(),
                editResidBathrooms.getText().toString(),
                editResidBedrooms.getText().toString()
        );

        residenceDAO.update(editedResidence);
        PlaceholderContent.updateList();
        PlaceholderContent.ITEM_MAP.put(editedResidence.getId(), editedResidence);

        ItemListFragment.getResidList();
        onListUpdated();
    }

    public void onListUpdated() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(new Intent(EditRealEstateActivity.this, ItemDetailHostActivity.class));
        overridePendingTransition(0, 0);
    }
}
