package com.openclassrooms.realestatemanager.UI;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.placeholder.PlaceholderContent;
import com.openclassrooms.realestatemanager.roomdb.AppDatabase;
import com.openclassrooms.realestatemanager.roomdb.ResidenceDAO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SuppressWarnings("deprecation")
public class AddRealEstateActivity extends AppCompatActivity {

    AppDatabase db;
    ResidenceDAO residenceDAO;

    private PlaceholderContent.PlaceholderItem residenceIsSaved;

    String residPicture;
    TextInputLayout residType;
    TextInputLayout residSurface;
    TextInputLayout residRooms;
    TextInputLayout residBathrooms;
    TextInputLayout residBedrooms;
    TextInputLayout residLocation;
    TextInputLayout residAddress;
    TextInputLayout residDescription;
    TextInputLayout residPrice;

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
        setContentView(R.layout.activity_add_residence);

        db = AppDatabase.getInstance(this);
        residenceDAO = db.residenceDAO();

        mArrayUri = new ArrayList<>();

        residPicturesIV = findViewById(R.id.residPicturesIV);

        openGalleryButton = findViewById(R.id.residPicturesButtonGallery);
        openGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        openCameraButton = findViewById(R.id.residPicturesButtonCamera);
        openCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

        this.createNotificationChannel();
        this.configureLayout();
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
        final int[] isAllField = {0};

        saveButton = findViewById(R.id.residSaveButton);

        residType = findViewById(R.id.residTypeLyt);
        residType.getEditText().addTextChangedListener(new TextWatcher() {
            final boolean[] hasRun = {false};

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!hasRun[0]) {
                    isAllField[0]++;
                    setSaveButton(isAllField);
                    hasRun[0] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        residSurface = findViewById(R.id.residSurfaceLyt);
        residSurface.getEditText().addTextChangedListener(new TextWatcher() {
            final boolean[] hasRun = {false};

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!hasRun[0]) {
                    isAllField[0]++;
                    setSaveButton(isAllField);
                    hasRun[0] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        residRooms = findViewById(R.id.residRoomsLyt);
        residRooms.getEditText().addTextChangedListener(new TextWatcher() {
            final boolean[] hasRun = {false};

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!hasRun[0]) {
                    isAllField[0]++;
                    setSaveButton(isAllField);
                    hasRun[0] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        residBathrooms = findViewById(R.id.residBathroomsLyt);
        residBathrooms.getEditText().addTextChangedListener(new TextWatcher() {
            final boolean[] hasRun = {false};

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!hasRun[0]) {
                    isAllField[0]++;
                    setSaveButton(isAllField);
                    hasRun[0] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        residBedrooms = findViewById(R.id.residBedroomsLyt);
        residBedrooms.getEditText().addTextChangedListener(new TextWatcher() {
            final boolean[] hasRun = {false};

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!hasRun[0]) {
                    isAllField[0]++;
                    setSaveButton(isAllField);
                    hasRun[0] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        residLocation = findViewById(R.id.residLocationLyt);
        residLocation.getEditText().addTextChangedListener(new TextWatcher() {
            final boolean[] hasRun = {false};

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!hasRun[0]) {
                    isAllField[0]++;
                    setSaveButton(isAllField);
                    hasRun[0] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        residAddress = findViewById(R.id.residAddressLyt);
        residAddress.getEditText().addTextChangedListener(new TextWatcher() {
            final boolean[] hasRun = {false};

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!hasRun[0]) {
                    isAllField[0]++;
                    setSaveButton(isAllField);
                    hasRun[0] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        residDescription = findViewById(R.id.residDescriptionLyt);
        residDescription.getEditText().addTextChangedListener(new TextWatcher() {
            final boolean[] hasRun = {false};

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!hasRun[0]) {
                    isAllField[0]++;
                    setSaveButton(isAllField);
                    hasRun[0] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        residPrice = findViewById(R.id.residPriceLyt);
        residPrice.getEditText().addTextChangedListener(new TextWatcher() {
            final boolean[] hasRun = {false};

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!hasRun[0]) {
                    isAllField[0]++;
                    setSaveButton(isAllField);
                    hasRun[0] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void setSaveButton(int[] isAllField) {
        String s = Arrays.toString(isAllField);
        if (s.equals("[9]")) {
            saveButton.setEnabled(true);
            saveButton.setBackgroundColor(getResources().getColor(R.color.purple_500));

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createResidence();
                    onSavedNotify();
                    Intent i = new Intent(AddRealEstateActivity.this, ItemDetailHostActivity.class);
                    startActivity(i);
                }
            });
        }
    }

    // ------------------
    //  CREATE RESIDENCE
    // ------------------

    private void createResidence() {
        PlaceholderContent.PlaceholderItem placeholderItem = new PlaceholderContent.PlaceholderItem(
                String.valueOf(System.currentTimeMillis()),
                residPicture,
                residType.getEditText().getText().toString(),
                residLocation.getEditText().getText().toString(),
                residAddress.getEditText().getText().toString(),
                residPrice.getEditText().getText().toString(),
                residDescription.getEditText().getText().toString(),
                residSurface.getEditText().getText().toString(),
                residRooms.getEditText().getText().toString(),
                residBathrooms.getEditText().getText().toString(),
                residBedrooms.getEditText().getText().toString()
        );

        residenceIsSaved = placeholderItem;

        PlaceholderContent.addItem(placeholderItem);
        residenceDAO.insertAll(placeholderItem);
    }

    // ------------------
    //  NOTIFY ADDED
    // ------------------

    private String CHANNEL_ID = "REM_NOTIFY";

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SavedNotify";
            String description = "Channel For SAve Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void onSavedNotify() {
        CharSequence textTitleSaved = "Residence Saved !";
        CharSequence textContentSaved = "Your " + residType.getEditText().getText().toString().toUpperCase() + " has been SAVED !";

        CharSequence textTitleFailed = "Save Failed !";
        CharSequence textContentFailed = "Your " + residType.getEditText().getText().toString().toUpperCase() + " save has FAILED ! TRY AGAIN";

        int notificationId = (int) System.currentTimeMillis();

        Intent intent = new Intent(this, ItemDetailHostActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        if (residenceDAO.getAll().contains(residenceIsSaved)) {
            builder.setSmallIcon(R.drawable.ic_baseline_home_24)
                    .setContentTitle(textTitleSaved)
                    .setContentText(textContentSaved)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
        } else {
            builder.setSmallIcon(R.drawable.ic_baseline_home_24)
                    .setContentTitle(textTitleFailed)
                    .setContentText(textContentFailed)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }
}
