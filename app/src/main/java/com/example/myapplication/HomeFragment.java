package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView videoRecyclerView;
    private VideoAdapter videoAdapter;
    private List<VideoModel> videoList;
    private FirebaseFirestore firestore;
    private Button previousButton, nextButton;

    // Pagination variables
    private static final int PAGE_SIZE = 2;
    private int currentPage = 0;
    private int totalPages = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        videoRecyclerView = view.findViewById(R.id.videoRecyclerView);
        previousButton = view.findViewById(R.id.previous_button);
        nextButton = view.findViewById(R.id.next_button);
        videoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firestore = FirebaseFirestore.getInstance();
        videoList = new ArrayList<>();

        loadVideosFromFirestore();

        previousButton.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                displayPage();
            } else {
                Toast.makeText(getContext(), "You are on the first page", Toast.LENGTH_SHORT).show();
            }
        });

        nextButton.setOnClickListener(v -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                displayPage();
            } else {
                Toast.makeText(getContext(), "You are on the last page", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadVideosFromFirestore() {
        firestore.collection("myvideos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            videoList.clear();
                            for (DocumentSnapshot doc : task.getResult()) {
                                // Fetch the list of users who liked and disliked the video
                                List<String> likes = (List<String>) doc.get("likedUsers");
                                List<String> dislikes = (List<String>) doc.get("dislikedUsers");

                                VideoModel video = new VideoModel(
                                        doc.getString("title"),
                                        doc.getString("vurl"),
                                        doc.getLong("likeCount").intValue(),
                                        doc.getLong("dislikeCount").intValue(),
                                        doc.getId(),
                                        likes == null ? new ArrayList<>() : likes,
                                        dislikes == null ? new ArrayList<>() : dislikes
                                );
                                videoList.add(video);
                            }

                            totalPages = (int) Math.ceil((double) videoList.size() / PAGE_SIZE);
                            displayPage();
                        } else {
                            Toast.makeText(getContext(), "Failed to load videos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void displayPage() {
        int start = currentPage * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, videoList.size());

        List<VideoModel> pageItems = new ArrayList<>();
        for (int i = start; i < end; i++) {
            pageItems.add(videoList.get(i));
        }

        videoAdapter = new VideoAdapter(pageItems, getContext());
        videoRecyclerView.setAdapter(videoAdapter);
    }
}
