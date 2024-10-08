package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;
    NavigationView navigationView;
    // Firebase Storage reference
    private StorageReference storageRef;
    // Firestore reference
    private FirebaseFirestore firestore;
    // Current user
    private FirebaseUser currentUser;

    // Activity result launcher for choosing a video file
    private final ActivityResultLauncher<String> chooseVideoLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            result -> {
                if (result != null) {
                    uploadVideo(result);
                } else {
                    // Handle null result (user canceled or error)
                    Toast.makeText(MainActivity.this, "Failed to choose video", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Toast.makeText(MainActivity.this, "current user is " + currentUser, Toast.LENGTH_SHORT).show();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        navigationView.bringToFront();

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Check authentication status
        checkAuthenticationStatus();

        bottomNavigationView.setBackground(null);

        // Handle bottom navigation item clicks
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                // Load HomeFragment
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.shorts) {
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            } else if (item.getItemId() == R.id.video) {
                startActivity(new Intent(MainActivity.this, VideoActivity.class));
            } else if (item.getItemId() == R.id.library) {
                replaceFragment(new LibraryFragment());
            } else if (item.getItemId() == R.id.rating) {
                startActivity(new Intent(MainActivity.this, RatingActivity.class));
            }
            return true;
        });

        fab.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), addvideoActivity.class)));

        // Handle drawer item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                // Load HomeFragment
                replaceFragment(new HomeFragment());
                drawerLayout.closeDrawers();
            } else if (item.getItemId() == R.id.nav_weather) {
                startActivity(new Intent(MainActivity.this, WeatherActivity.class));
                drawerLayout.closeDrawers();
            } else if (item.getItemId() == R.id.nav_logout) {
                logout();
                return true;
            } else if (item.getItemId() == R.id.nav_detect) {
                startActivity(new Intent(MainActivity.this, DetectionActivity.class));
                drawerLayout.closeDrawers();
            } else if (item.getItemId() == R.id.nav_share) {
                startActivity(new Intent(MainActivity.this, CalenderActivity.class));
                drawerLayout.closeDrawers();
            }
            return true; // Indicate that the event is not consumed for other items
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void checkAuthenticationStatus() {
        // Check authentication status
        if (currentUser != null) {
            // User is signed in
            String username = currentUser.getDisplayName();
            if (username != null) {
                Toast.makeText(MainActivity.this, "Welcome  " + username, Toast.LENGTH_SHORT).show();
                // Optionally set the username to a TextView in your layout
                // TextView usernameTextView = findViewById(R.id.usernameTextView);
                // usernameTextView.setText(username);
            } else {
                Toast.makeText(MainActivity.this, "User has no display name", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "No user is signed in", Toast.LENGTH_SHORT).show();
            // Redirect to login activity if necessary
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

    }

    private void logout() {
        // Clear session data or perform any other logout actions
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Clear session using the clearSession() method from LoginActivity
        LoginActivity.clearSession(MainActivity.this);

        // Redirect the user to the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity
    }

    // Upload the selected video file to Firebase Storage and create references in Firestore
    private void uploadVideo(android.net.Uri videoUri) {
        // Create a reference to the location where the video will be saved in Storage
        StorageReference videoRef = storageRef.child("videos/" + System.currentTimeMillis() + ".mp4");

        // Upload the video file
        videoRef.putFile(videoUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Video upload successful
                    Toast.makeText(MainActivity.this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
                    // Save video metadata and create empty like/dislike documents
                    saveVideoMetadataAndCreateEmptyDocuments(videoRef.getName());
                })
                .addOnFailureListener(e -> {
                    // Video upload failed
                    Toast.makeText(MainActivity.this, "Failed to upload video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Save video metadata (filename, title) to Firestore and create empty documents for likes and dislikes
    private void saveVideoMetadataAndCreateEmptyDocuments(String filename) {
        // Save video metadata (filename and title) to Firestore
        VideoMetadata metadata = new VideoMetadata(filename);
        firestore.collection("videos")
                .document(filename)
                .set(metadata)
                .addOnSuccessListener(aVoid -> {
                    // Video metadata saved successfully
                    Toast.makeText(MainActivity.this, "Video metadata saved to Firestore", Toast.LENGTH_SHORT).show();
                    // Create empty documents for likes and dislikes
                    createEmptyLikesDislikesDocuments(filename);
                })
                .addOnFailureListener(e -> {
                    // Failed to save video metadata
                    Toast.makeText(MainActivity.this, "Failed to save video metadata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Create empty documents in "likes" and "dislikes" collections for the uploaded video
    private void createEmptyLikesDislikesDocuments(String filename) {
        // Create a document reference for likes
        firestore.collection("likes").document(filename)
                .set(new VideoLikes())
                .addOnSuccessListener(aVoid -> {
                    // Document for likes created successfully
                    Toast.makeText(MainActivity.this, "Likes document created in Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to create document for likes
                    Toast.makeText(MainActivity.this, "Failed to create likes document in Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Create a document reference for dislikes
        firestore.collection("dislikes").document(filename)
                .set(new VideoDislikes())
                .addOnSuccessListener(aVoid -> {
                    // Document for dislikes created successfully
                    Toast.makeText(MainActivity.this, "Dislikes document created in Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to create document for dislikes
                    Toast.makeText(MainActivity.this, "Failed to create dislikes document in Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private class VideoLikes {
        public VideoLikes() {
            // Default constructor required for Firestore
        }
    }

    private class VideoDislikes {
        public VideoDislikes() {
            // Default constructor required for Firestore
        }
    }
}
