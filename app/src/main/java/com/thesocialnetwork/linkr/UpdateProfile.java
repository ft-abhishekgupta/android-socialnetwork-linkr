package com.thesocialnetwork.linkr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class UpdateProfile extends AppCompatActivity {

    FirebaseUser firebaseUser;
    StorageReference storageReference;
    DatabaseReference mDatabase, db_detailedUserProfile;
    TextInputEditText profile_name, profile_phone, profile_email;
    CircularImageView profile_image;
    String userName, userEmai, userPhone;
    Button changeimg, update, logout;
    Uri userImg, filePath, downloadedUri;
    boolean isImgChanged = false;
    String uploadId, UserId;
    private GoogleApiClient GoogleApiClient;
    String defaultPic = "https://firebasestorage.googleapis.com/v0/b/linkr-app.appspot.com/o/profileImages%2Fdefault.png?alt=media&token=962bbcb0-d9ff-4ae6-a848-2d718b173c02";
    public static final String STORAGE_PATH_UPLOADS = "profileImages/";
    public static final String DATABASE_PATH_USERS = "usersProfile";
    private static final int PICK_IMAGE_REQUEST = 234;
    private Uri mCropImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_update_profile);
        setTitle("Update Profile");
        getSupportActionBar().hide();

        profile_name = (TextInputEditText) findViewById(R.id.profile_name_text);
        profile_phone = (TextInputEditText) findViewById(R.id.phone_text);
        profile_email = (TextInputEditText) findViewById(R.id.email_text);
        profile_image = (CircularImageView) findViewById(R.id.circleImageView);
        changeimg = (Button) findViewById(R.id.changeimg);
        update = (Button) findViewById(R.id.updateProfile);
        logout = findViewById(R.id.logout);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        UserId = firebaseUser.getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(DATABASE_PATH_USERS);
        db_detailedUserProfile = FirebaseDatabase.getInstance().getReference().child("detailedInfo").child(UserId);
        uploadId = firebaseUser.getUid();

        if (firebaseUser != null) {
            userName = firebaseUser.getDisplayName();
            userEmai = firebaseUser.getEmail();
            userPhone = firebaseUser.getPhoneNumber();
            userImg = firebaseUser.getPhotoUrl();

            profile_name.setText(userName);
            profile_email.setText(userEmai);
            profile_phone.setText(userPhone);
            if (!userImg.equals("")) {
                Glide.with(getApplicationContext()).load(userImg.toString()).thumbnail(0.5f).crossFade().centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(profile_image);
            }
        } else {
            Toast.makeText(this, "Try Again Later", Toast.LENGTH_SHORT).show();
        }

        changeimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onSelectImageClick(view);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                storeData(isImgChanged);
                Intent i = new Intent(UpdateProfile.this, MainActivity.class);
                finish();
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(GoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(UpdateProfile.this, Login.class);
                finish();
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                .build();
        GoogleApiClient = new GoogleApiClient.Builder(this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        GoogleApiClient.connect();
        super.onStart();
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
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(profile_image);
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
                    if (downloadedUri != null)
                        isImgChanged = true;
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
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                }
            });
        }
    }

    private void storeData(boolean isImgChanged) {

        String name = profile_name.getText().toString().trim();
        String phone = profile_phone.getText().toString().trim();
        String email = profile_email.getText().toString().trim();

        if (isImgChanged) {
            ModelUserProfile modelUserProfile = new ModelUserProfile(UserId, name, phone, email,
                    downloadedUri.toString(), "");
            mDatabase.child(uploadId).setValue(modelUserProfile);

            if (!phone.equals("")) {
                String keyPhone = db_detailedUserProfile.child("other_phone").push().getKey();
                ModelDetailedInfo modelDetailedInfoPhone = new ModelDetailedInfo(keyPhone, phone, "Everyone", "Home");
                db_detailedUserProfile.child("other_phone").child(keyPhone).setValue(modelDetailedInfoPhone);
            }
            String keyEmail = db_detailedUserProfile.child("other_email").push().getKey();
            ModelDetailedInfo modelDetailedInfoEmail = new ModelDetailedInfo(keyEmail, email, "Everyone", "Home");
            db_detailedUserProfile.child("other_email").child(keyEmail).setValue(modelDetailedInfoEmail);
        } else {
            if (userImg != null) {
                ModelUserProfile modelUserProfile = new ModelUserProfile(UserId, name, phone, email, userImg.toString(),
                        "");
                mDatabase.child(uploadId).setValue(modelUserProfile);

                if (!phone.equals("")) {
                    String keyPhone = db_detailedUserProfile.child("other_phone").push().getKey();
                    ModelDetailedInfo modelDetailedInfoPhone = new ModelDetailedInfo(keyPhone, phone, "Everyone",
                            "Home");
                    db_detailedUserProfile.child("other_phone").child(keyPhone).setValue(modelDetailedInfoPhone);
                }
                String keyEmail = db_detailedUserProfile.child("other_email").push().getKey();
                ModelDetailedInfo modelDetailedInfoEmail = new ModelDetailedInfo(keyEmail, email, "Everyone", "Home");
                db_detailedUserProfile.child("other_email").child(keyEmail).setValue(modelDetailedInfoEmail);
            } else {
                ModelUserProfile modelUserProfile = new ModelUserProfile(UserId, name, phone, email, defaultPic, "");
                mDatabase.child(uploadId).setValue(modelUserProfile);

                if (!phone.equals("")) {
                    String keyPhone = db_detailedUserProfile.child("other_phone").push().getKey();
                    ModelDetailedInfo modelDetailedInfoPhone = new ModelDetailedInfo(keyPhone, phone, "Everyone",
                            "Home");
                    db_detailedUserProfile.child("other_phone").child(keyPhone).setValue(modelDetailedInfoPhone);
                }
                String keyEmail = db_detailedUserProfile.child("other_email").push().getKey();
                ModelDetailedInfo modelDetailedInfoEmail = new ModelDetailedInfo(keyEmail, email, "Everyone", "Home");
                db_detailedUserProfile.child("other_email").child(keyEmail).setValue(modelDetailedInfoEmail);
            }
        }
    }
}
