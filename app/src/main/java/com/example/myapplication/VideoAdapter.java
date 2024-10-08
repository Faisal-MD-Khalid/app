package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<VideoModel> videoList;
    private Context context;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    public VideoAdapter(List<VideoModel> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoModel video = videoList.get(position);

        holder.videoTitle.setText(video.getTitle());
        holder.likeCount.setText(String.valueOf(video.getLikeCount()));
        holder.dislikeCount.setText(String.valueOf(video.getDislikeCount()));
        holder.videoView.setVideoURI(Uri.parse(video.getVideoUrl()));
        holder.videoView.seekTo(1);

        String currentUserId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        holder.likeButton.setOnClickListener(view -> handleLike(video, holder, currentUserId));
        holder.dislikeButton.setOnClickListener(view -> handleDislike(video, holder, currentUserId));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        TextView videoTitle, likeCount, dislikeCount;
        ImageButton likeButton, dislikeButton;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            videoTitle = itemView.findViewById(R.id.videoTitleTextView);
            likeCount = itemView.findViewById(R.id.likeCountTextView);
            dislikeCount = itemView.findViewById(R.id.dislikeCountTextView);
            likeButton = itemView.findViewById(R.id.likeButton);
            dislikeButton = itemView.findViewById(R.id.dislikeButton);
        }
    }

    private void handleLike(VideoModel video, VideoViewHolder holder, String currentUserId) {
        if (currentUserId == null) {
            Toast.makeText(context, "Please log in to like a video", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the user already liked the video
        if (video.getLikedUsers().contains(currentUserId)) {
            Toast.makeText(context, "You have already liked this video", Toast.LENGTH_SHORT).show();
        } else {
            // Remove from dislikes if disliked before
            if (video.getDislikedUsers().contains(currentUserId)) {
                video.getDislikedUsers().remove(currentUserId);
                video.setDislikeCount(video.getDislikeCount() - 1);
            }

            // Add to likes
            video.getLikedUsers().add(currentUserId);
            video.setLikeCount(video.getLikeCount() + 1);

            updateFirestore(video, holder);
        }
    }

    private void handleDislike(VideoModel video, VideoViewHolder holder, String currentUserId) {
        if (currentUserId == null) {
            Toast.makeText(context, "Please log in to dislike a video", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the user already disliked the video
        if (video.getDislikedUsers().contains(currentUserId)) {
            Toast.makeText(context, "You have already disliked this video", Toast.LENGTH_SHORT).show();
        } else {
            // Remove from likes if liked before
            if (video.getLikedUsers().contains(currentUserId)) {
                video.getLikedUsers().remove(currentUserId);
                video.setLikeCount(video.getLikeCount() - 1);
            }

            // Add to dislikes
            video.getDislikedUsers().add(currentUserId);
            video.setDislikeCount(video.getDislikeCount() + 1);

            updateFirestore(video, holder);
        }
    }

    private void updateFirestore(VideoModel video, VideoViewHolder holder) {
        firestore.collection("myvideos").document(video.getVideoId())
                .update(
                        "likeCount", video.getLikeCount(),
                        "dislikeCount", video.getDislikeCount(),
                        "likedUsers", video.getLikedUsers(),
                        "dislikedUsers", video.getDislikedUsers()
                )
                .addOnSuccessListener(aVoid -> {
                    holder.likeCount.setText(String.valueOf(video.getLikeCount()));
                    holder.dislikeCount.setText(String.valueOf(video.getDislikeCount()));
                });
    }
}
