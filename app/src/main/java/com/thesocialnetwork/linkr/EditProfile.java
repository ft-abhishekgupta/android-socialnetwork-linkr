package com.thesocialnetwork.linkr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import java.util.ArrayList;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class EditProfile extends AppCompatActivity {

    StorageReference storageReference;
    CircularImageView profile_img;
    ImageView add_phone, add_address, add_email, add_link;
    TextView default_phone, default_email, profile_name;
    RecyclerView rv_phone, rv_address, rv_email, rv_link;
    FirebaseAuth firebaseAuth;
    LinearLayoutManager layoutManagerPhone, layoutManagerEmail, layoutManagerAddress, layoutManagerLink;

    ArrayList<ModelDetailedInfo> dataListModelPhone = new ArrayList<>();
    ArrayList<ModelDetailedInfo> dataListModelEmail = new ArrayList<>();
    ArrayList<ModelDetailedInfo> dataListModelAddress = new ArrayList<>();
    ArrayList<ModelDetailedInfo> dataListModelLink = new ArrayList<>();
    AdapterProfileData adapterInfoPhone, adapterInfoLink, adapterInfoEmail, adapterInfoAddress;

    ValueEventListener valueEventListener;
    DatabaseReference db_usersProfile, db_detailedInfo;
    String UserId;
    String type[] = { "Type", "Home", "Work", "Main", "Other" };
    String secure[] = { "Privacy", "Only Me", "Friends", "Everyone" };
    String source_link[] = { "Source", "LinkedIn", "Facebook", "Twitter", "Google+", "Instagram", "SnapChat" };
    String user_name, user_phone, user_email, user_img, user_status;
    private static final int PICK_IMAGE_REQUEST = 234;
    public static final String STORAGE_PATH_UPLOADS = "profileImages/";
    String defaultPicURL = "https://firebasestorage.googleapis.com/v0/b/linkr-app.appspot.com/o/profileImages%2Fdefault.png?alt=media&token=962bbcb0-d9ff-4ae6-a848-2d718b173c02";
    Uri filePath, downloadedUri;
    private Uri mCropImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().hide();

        profile_name = (TextView) findViewById(R.id.profile_displayName);
        profile_img = (CircularImageView) findViewById(R.id.circleImageView);
        add_phone = findViewById(R.id.add_phone);
        add_email = findViewById(R.id.add_email);
        add_address = findViewById(R.id.add_addr);
        add_link = findViewById(R.id.add_handle);
        rv_phone = (RecyclerView) findViewById(R.id.recycler_view_phone);
        rv_email = (RecyclerView) findViewById(R.id.recycler_view_email);
        rv_address = (RecyclerView) findViewById(R.id.recycler_view_address);
        rv_link = (RecyclerView) findViewById(R.id.recycler_view_link);
        rv_address.setHasFixedSize(true);
        rv_address.setNestedScrollingEnabled(false);
        rv_email.setHasFixedSize(true);
        rv_email.setNestedScrollingEnabled(false);
        rv_phone.setHasFixedSize(true);
        rv_phone.setNestedScrollingEnabled(false);
        rv_link.setHasFixedSize(true);
        rv_link.setNestedScrollingEnabled(false);
        layoutManagerPhone = new LinearLayoutManager(this);
        layoutManagerEmail = new LinearLayoutManager(this);
        layoutManagerAddress = new LinearLayoutManager(this);
        layoutManagerLink = new LinearLayoutManager(this);
        rv_link.setLayoutManager(layoutManagerLink);
        rv_phone.setLayoutManager(layoutManagerPhone);
        rv_email.setLayoutManager(layoutManagerEmail);
        rv_address.setLayoutManager(layoutManagerAddress);

        firebaseAuth = FirebaseAuth.getInstance();
        UserId = firebaseAuth.getCurrentUser().getUid();
        db_usersProfile = FirebaseDatabase.getInstance().getReference().child("usersProfile").child(UserId);
        db_usersProfile.keepSynced(true);
        db_detailedInfo = FirebaseDatabase.getInstance().getReference().child("detailedInfo").child(UserId);
        db_detailedInfo.keepSynced(true);
        storageReference = FirebaseStorage.getInstance().getReference();

        add_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialogBox("Phone");
            }
        });

        add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialogBox("Address");
            }
        });

        add_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialogBox("Email");
            }
        });

        add_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialogBox("Social Link");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
        Runtime.getRuntime().gc();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                user_name = dataSnapshot.child("profile_name").getValue().toString();
                user_img = dataSnapshot.child("profile_img").getValue().toString();
                user_phone = dataSnapshot.child("profile_phone").getValue().toString();
                user_email = dataSnapshot.child("profile_email").getValue().toString();
                user_status = dataSnapshot.child("profile_online_status").getValue().toString();
                profile_name.setText(user_name);
                Glide.with(getApplicationContext()).load(user_img).thumbnail(0.5f).crossFade().centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(profile_img);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        db_usersProfile.addValueEventListener(valueEventListener);

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                CharSequence options[] = new CharSequence[] { "Change Image", "Remove Image" };

                final AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);

                builder.setTitle("Select Options");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Click Event for each item.
                        if (i == 0) {
                            onSelectImageClick(view);
                        }

                        if (i == 1) {
                            removeImage();
                        }
                    }
                });

                builder.show();
            }
        });

        adapterInfoPhone = new AdapterProfileData(EditProfile.this, fetch("Phone"), "Phone");
        adapterInfoEmail = new AdapterProfileData(EditProfile.this, fetch("Email"), "Email");
        adapterInfoAddress = new AdapterProfileData(EditProfile.this, fetch("Address"), "Address");
        adapterInfoLink = new AdapterProfileData(EditProfile.this, fetch("Link"), "Link");
        rv_phone.setAdapter(adapterInfoPhone);
        rv_email.setAdapter(adapterInfoEmail);
        rv_address.setAdapter(adapterInfoAddress);
        rv_link.setAdapter(adapterInfoLink);

        super.onResume();
    }

    private void onSelectImageClick(View view) {

        CropImage.startPickImageActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read
            // external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[] { android.Manifest.permission.READ_EXTERNAL_STORAGE }, 0);
                }

            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri filePath = result.getUri();
                Glide.with(getApplicationContext()).load(filePath.toString()).thumbnail(0.5f).crossFade().centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(profile_img);
                uploadFile(filePath);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL).setMultiTouchEnabled(true).setAspectRatio(2, 2).start(this);
    }

    private ArrayList<ModelDetailedInfo> fetch(String string) {

        if (string.equals("Phone")) {
            db_detailedInfo.child("other_phone").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataListModelPhone.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ModelDetailedInfo model = ds.getValue(ModelDetailedInfo.class);
                        dataListModelPhone.add(model);
                    }
                    adapterInfoPhone.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return dataListModelPhone;
        } else if (string.equals("Email")) {
            db_detailedInfo.child("other_email").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataListModelEmail.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ModelDetailedInfo model = ds.getValue(ModelDetailedInfo.class);
                        dataListModelEmail.add(model);
                    }
                    adapterInfoEmail.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return dataListModelEmail;
        } else if (string.equals("Address")) {
            db_detailedInfo.child("other_address").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataListModelAddress.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ModelDetailedInfo model = ds.getValue(ModelDetailedInfo.class);
                        dataListModelAddress.add(model);
                    }
                    adapterInfoAddress.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return dataListModelAddress;
        } else {
            db_detailedInfo.child("other_link").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dataListModelLink.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ModelDetailedInfo model = ds.getValue(ModelDetailedInfo.class);
                        dataListModelLink.add(model);
                    }
                    adapterInfoLink.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return dataListModelLink;
        }
    }

    private void removeImage() {

        Glide.with(getApplicationContext()).load(defaultPicURL).thumbnail(0.5f).crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(profile_img);

        ModelUserProfile modelUserProfile = new ModelUserProfile(UserId, user_name, user_phone, user_email,
                defaultPicURL, user_status);
        db_usersProfile.setValue(modelUserProfile);
        Toast.makeText(EditProfile.this, "Image Removed", Toast.LENGTH_SHORT).show();
    }

    public String getFileExtension(Uri uri) {
        // ContentResolver cR = getContentResolver();
        // MimeTypeMap mime = MimeTypeMap.getSingleton();
        String str = uri.toString();
        String extension = str.substring(str.lastIndexOf(".") + 1);
        return extension;
    }

    private void uploadFile(Uri file) {

        if (file != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Please Wait..");
            progressDialog.show();

            StorageReference sRef = storageReference.child(STORAGE_PATH_UPLOADS
                    + FirebaseAuth.getInstance().getCurrentUser().getUid() + "." + getFileExtension(file));
            sRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // dismissing the progress dialog
                    progressDialog.dismiss();

                    // displaying success toast
                    Toast.makeText(getApplicationContext(), "Image Changed", Toast.LENGTH_LONG).show();

                    downloadedUri = taskSnapshot.getDownloadUrl();
                    ModelUserProfile modelUserProfile = new ModelUserProfile(UserId, user_name, user_phone, user_email,
                            downloadedUri.toString(), user_status);
                    db_usersProfile.setValue(modelUserProfile);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    // displaying the upload progress
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploading file " + ((int) progress) + "%...");
                }
            });
        }
    }

    private void showDialogBox(final String string) {

        LayoutInflater li = LayoutInflater.from(EditProfile.this);
        View promptsView = li.inflate(R.layout.add_item_dialog, null);

        final EditText editText = (EditText) promptsView.findViewById(R.id.enterDetail);
        final Spinner spinner1 = (Spinner) promptsView.findViewById(R.id.spinner_type);
        final Spinner spinner2 = (Spinner) promptsView.findViewById(R.id.spinner_secure);
        editText.setHint("Enter " + string);

        if (string.equals("Social Link")) {
            final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                    source_link);
            adapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner1.setAdapter(adapter1);
        } else {
            final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                    type);
            adapter1.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            spinner1.setAdapter(adapter1);
        }

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, secure);
        adapter2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EditProfile.this);

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder.setTitle(string).setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (string.equals("Phone")) {
                            String str = editText.getText().toString().trim();
                            String type = spinner1.getSelectedItem().toString().trim();
                            String privacy = spinner2.getSelectedItem().toString().trim();

                            if (str.equals(null) || type.equals("Choose Type") || privacy.equals("Who Can See")) {
                                Toast.makeText(EditProfile.this, "Try Again", Toast.LENGTH_SHORT).show();
                            } else {
                                String key = db_detailedInfo.child("other_phone").push().getKey();
                                ModelDetailedInfo modelDetailedInfo = new ModelDetailedInfo(key, str, privacy, type);
                                db_detailedInfo.child("other_phone").child(key).setValue(modelDetailedInfo);
                                adapterInfoPhone.notifyDataSetChanged();
                            }
                        } else if (string.equals("Address")) {
                            String str = editText.getText().toString().trim();
                            String type = spinner1.getSelectedItem().toString().trim();
                            String privacy = spinner2.getSelectedItem().toString().trim();

                            if (str.equals(null) || type.equals("Choose Type") || privacy.equals("Who Can See")) {
                                Toast.makeText(EditProfile.this, "Try Again", Toast.LENGTH_SHORT).show();
                            } else {
                                String key = db_detailedInfo.child("other_address").push().getKey();
                                ModelDetailedInfo modelDetailedInfo = new ModelDetailedInfo(key, str, privacy, type);
                                db_detailedInfo.child("other_address").child(key).setValue(modelDetailedInfo);
                                adapterInfoAddress.notifyDataSetChanged();
                            }
                        } else if (string.equals("Email")) {
                            String str = editText.getText().toString().trim();
                            String type = spinner1.getSelectedItem().toString().trim();
                            String privacy = spinner2.getSelectedItem().toString().trim();

                            if (str.equals(null) || type.equals("Choose Type") || privacy.equals("Who Can See")) {
                                Toast.makeText(EditProfile.this, "Try Again", Toast.LENGTH_SHORT).show();
                            } else {
                                String key = db_detailedInfo.child("other_email").push().getKey();
                                ModelDetailedInfo modelDetailedInfo = new ModelDetailedInfo(key, str, privacy, type);
                                db_detailedInfo.child("other_email").child(key).setValue(modelDetailedInfo);
                                adapterInfoEmail.notifyDataSetChanged();
                            }
                        } else {
                            String str = editText.getText().toString().trim();
                            String type = spinner1.getSelectedItem().toString().trim();
                            String privacy = spinner2.getSelectedItem().toString().trim();

                            if (str.equals(null) || type.equals("Choose Type") || privacy.equals("Who Can See")) {
                                Toast.makeText(EditProfile.this, "Try Again", Toast.LENGTH_SHORT).show();
                            } else {
                                String key = db_detailedInfo.child("other_link").push().getKey();
                                ModelDetailedInfo modelDetailedInfo = new ModelDetailedInfo(key, str, privacy, type);
                                db_detailedInfo.child("other_link").child(key).setValue(modelDetailedInfo);
                                adapterInfoLink.notifyDataSetChanged();
                            }
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }

                );

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

}
