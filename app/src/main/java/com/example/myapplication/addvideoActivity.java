package com.example.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class addvideoActivity extends AppCompatActivity {
    VideoView videoView;
    Button browse, upload;
    Uri videouri;
    EditText vtitle;
    MediaController mediaController;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseFirestore firestore; // Firestore reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addvideo);

        // Initialize views
        vtitle = findViewById(R.id.videoTitleEditText);
        videoView = findViewById(R.id.videoView);
        browse = findViewById(R.id.browse);
        upload = findViewById(R.id.upload);

        // Firebase initialization
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("myvideos");
        firestore = FirebaseFirestore.getInstance(); // Initialize Firestore

        // Setup VideoView media controller
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();

        // Browse button click listener
        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(addvideoActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent intent = new Intent();
                                intent.setType("video/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent, 101);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                Toast.makeText(addvideoActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        // Upload button click listener
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processVideoUpload();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            videouri = data.getData();
            videoView.setVideoURI(videouri);
        }
    }

    // Get file extension for selected video
    public String getExtension() {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videouri));
    }

    // Upload video to Firebase Storage and save metadata to Firestore
    // Upload video to Firebase Storage and save metadata to Firestore
    public void processVideoUpload() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Media Uploader");
        pd.show();

        // Create a unique filename using current time
        final StorageReference uploader = storageReference.child("myvideos/" + System.currentTimeMillis() + "." + getExtension());
        uploader.putFile(videouri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL for the uploaded video
                        uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Fetch the video title from EditText
                                String videoTitle = vtitle.getText().toString();
                                String videoUrl = uri.toString();
                                int initialLikeCount = 0;
                                int initialDislikeCount = 0;

                                // Create video data map
                                Map<String, Object> videoData = new HashMap<>();
                                videoData.put("title", videoTitle);
                                videoData.put("vurl", videoUrl);
                                videoData.put("likeCount", initialLikeCount);
                                videoData.put("dislikeCount", initialDislikeCount);
                                videoData.put("likes", new ArrayList<String>());    // Initialize empty likes array
                                videoData.put("dislikes", new ArrayList<String>()); // Initialize empty dislikes array

                                // Generate unique document ID for Firestore
                                String videoKey = firestore.collection("myvideos").document().getId();

                                // Upload video data to Firestore
                                firestore.collection("myvideos").document(videoKey)
                                        .set(videoData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Toast.makeText(addvideoActivity.this, "Successfully uploaded", Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(addvideoActivity.this, "Failed to upload: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(addvideoActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        // Update progress dialog
                        float progress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        pd.setMessage("Uploaded: " + (int) progress + "%");
                    }
                });
    }

}
