package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class VideoActivity extends AppCompatActivity {

    private WebView webView;
    private Button btnLike, btnDislike;
    private FirebaseFirestore firestore;
    private DocumentReference videoRef;
    private DatabaseReference ratingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Load video content
        loadVideo();
    }

    private void loadVideo() {
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadData("<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/Q_fFattg8N0\" frameborder=\"0\" allowfullscreen></iframe>", "text/html", "utf-8");
    }

    }



