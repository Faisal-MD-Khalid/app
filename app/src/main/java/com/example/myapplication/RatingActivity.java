package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RatingActivity extends AppCompatActivity {
    private Button button;
    private RatingBar ratingBar;
    private EditText commentEditText;
    private TextView globalRatingTextView;
    private int myRating = 0;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        button = findViewById(R.id.button);
        ratingBar = findViewById(R.id.ratingBar);
        commentEditText = findViewById(R.id.commentEditText);
        globalRatingTextView = findViewById(R.id.globalRatingTextView);

        ratingBar.setOnRatingBarChangeListener((ratingBar, v, b) -> {
            int rating = (int) v;
            String message = null;
            myRating = (int) ratingBar.getRating();
            switch (rating) {
                case 1:
                    message = "Sorry to hear that!";
                    break;
                case 2:
                    message = "You always accept suggestion";
                    break;
                case 3:
                    message = "Great! Good enough";
                    break;
                case 4:
                    message = "Great! Thank you";
                    break;
                case 5:
                    message = "Awesome! you are the best";
                    break;
            }
            Toast.makeText(RatingActivity.this, message, Toast.LENGTH_SHORT).show();
        });

        button.setOnClickListener(v -> {
            // Get the comment from the EditText
            String comment = commentEditText.getText().toString().trim();

            // Get current user
            String username = "Anonymous"; // Default username if not authenticated

            // Save rating and comment to Firestore
            saveRatingToFirestore(myRating, comment, username);
        });

        // Fetch and display global average rating
        fetchGlobalRating();
    }

    private void saveRatingToFirestore(int rating, String comment, String username) {
        // Create a new document in a collection named "ratings"
        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("rating", rating);
        ratingData.put("comment", comment);
        ratingData.put("username", username); // Include the username

        firestore.collection("ratings")
                .add(ratingData)
                .addOnSuccessListener(documentReference -> {
                    // Rating saved successfully
                    Toast.makeText(RatingActivity.this, "Rating and comment saved to Firestore", Toast.LENGTH_SHORT).show();
                    // Fetch and update global average rating after saving new rating
                    fetchGlobalRating();
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    Toast.makeText(RatingActivity.this, "Failed to save rating and comment to Firestore", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchGlobalRating() {
        firestore.collection("ratings")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int totalRatings = 0;
                        double averageRating = 0.0;

                        for (DocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            if (data != null && data.containsKey("rating")) {
                                int rating = Integer.parseInt(data.get("rating").toString());
                                totalRatings += rating;
                            }
                        }

                        // Calculate average rating
                        if (task.getResult().size() > 0) {
                            averageRating = (double) totalRatings / task.getResult().size();
                        }

                        // Update TextView with global average rating
                        globalRatingTextView.setText(String.format("Global Average Rating: %.1f", averageRating));
                    } else {
                        // Handle errors
                        Toast.makeText(RatingActivity.this, "Failed to fetch global rating", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
