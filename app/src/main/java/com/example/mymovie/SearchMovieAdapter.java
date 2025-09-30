package com.example.mymovie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymovie.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class SearchMovieAdapter extends RecyclerView.Adapter<SearchMovieAdapter.SearchMovieViewHolder> {
    private List<Movie> movies;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    public SearchMovieAdapter(Context context) {
        this.context = context;
        this.movies = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies != null ? movies : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void clearMovies() {
        if (movies != null) {
            int itemCount = movies.size();
            movies.clear();
            notifyItemRangeRemoved(0, itemCount);
        }
    }

    @NonNull
    @Override
    public SearchMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie_search, parent, false);
        return new SearchMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchMovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    class SearchMovieViewHolder extends RecyclerView.ViewHolder {
        private ImageView posterImage;
        private TextView titleText, ratingText, yearText;

        public SearchMovieViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.movie_poster);
            titleText = itemView.findViewById(R.id.movie_title);
            ratingText = itemView.findViewById(R.id.movie_rating);
            yearText = itemView.findViewById(R.id.movie_year);

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

            // Set rating dan tahun
            ratingText.setText(String.format("%.1f", movie.getVote_average()));

            if (movie.getRelease_date() != null && !movie.getRelease_date().isEmpty()) {
                String year = movie.getRelease_date().substring(0, 4);
                yearText.setText(year);
            } else {
                yearText.setText("N/A");
            }
        }
    }
}