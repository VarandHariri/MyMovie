package com.example.mymovie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymovie.R;
import com.example.mymovie.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> movies;
    private Context context;
    private OnItemClickListener listener;
    private int orientation;

    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    public MovieAdapter(Context context) {
        this(context, LinearLayoutManager.HORIZONTAL);
    }

    public MovieAdapter(Context context, int orientation) {
        this.context = context;
        this.movies = new ArrayList<>();
        this.orientation = orientation;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies != null ? movies : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addMovies(List<Movie> newMovies) {
        if (movies == null) {
            movies = new ArrayList<>();
        }
        int startPosition = movies.size();
        movies.addAll(newMovies);
        notifyItemRangeInserted(startPosition, newMovies.size());
    }

    public void clearMovies() {
        if (movies != null) {
            int itemCount = movies.size();
            movies.clear();
            notifyItemRangeRemoved(0, itemCount);
        }
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        if (this.orientation != orientation) {
            this.orientation = orientation;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Gunakan layout yang berbeda berdasarkan orientation
        int layoutRes;
        if (orientation == LinearLayoutManager.VERTICAL) {
            layoutRes = R.layout.item_movie_vertical;
        } else {
            layoutRes = R.layout.item_movie;
        }

        View view = LayoutInflater.from(context).inflate(layoutRes, parent, false);
        return new MovieViewHolder(view, orientation);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView posterImage;
        private TextView titleText;
        private TextView ratingText;
        private TextView yearText;
        private int viewOrientation;

        public MovieViewHolder(@NonNull View itemView, int orientation) {
            super(itemView);
            this.viewOrientation = orientation;

            posterImage = itemView.findViewById(R.id.movie_poster);
            titleText = itemView.findViewById(R.id.movie_title);

            if (orientation == LinearLayoutManager.VERTICAL) {
                ratingText = itemView.findViewById(R.id.movie_rating);
                yearText = itemView.findViewById(R.id.movie_year);
            }

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(movies.get(position));
                }
            });
        }

        public void bind(Movie movie) {
            // Set title
            titleText.setText(movie.getTitle());

            // Load poster image
            String posterUrl = movie.getPosterUrl();
            if (posterUrl != null) {
                Glide.with(context)
                        .load(posterUrl)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(posterImage);
            } else {
                posterImage.setImageResource(R.drawable.placeholder);
            }

            // Untuk layout vertical
            if (viewOrientation == LinearLayoutManager.VERTICAL) {
                if (ratingText != null) {
                    ratingText.setText(movie.getFormattedRating());
                }

                if (yearText != null) {
                    yearText.setText(movie.getReleaseYear());
                }
            }
        }
        private String getPosterUrl(Movie movie) {
            if (movie.getPoster_path() != null && !movie.getPoster_path().isEmpty()) {
                return movie.getPosterUrl();
            }
            return movie.getPosterUrl();
        }

        private String formatRating(double rating) {
            return String.format("%.1f", rating);
        }

        private String extractYear(String releaseDate) {
            if (releaseDate != null && releaseDate.length() >= 4) {
                return releaseDate.substring(0, 4);
            }
            return "N/A";
        }
    }
}