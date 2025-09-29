package com.example.mymovie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovie.adapter.MovieAdapter;
import com.example.mymovie.model.Movie;
import com.example.mymovie.model.MovieResponse;
import com.example.mymovie.network.ApiClient;
import com.example.mymovie.network.MovieService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private static final String API_KEY = "13991a4ec029782d650857fa1d158fea";
    private static final String TAG = "HomeActivity";

    // Genre IDs
    private static final int GENRE_ADVENTURE = 12;
    private static final int GENRE_ANIMATION = 16;
    private static final int GENRE_COMEDY = 35;
    private static final int GENRE_ACTION = 28;

    private RecyclerView adventureRecyclerView, animationRecyclerView, comedyRecyclerView, heroesRecyclerView;
    private MovieAdapter adventureAdapter, animationAdapter, comedyAdapter, heroesAdapter;
    private EditText searchBar;
    private ImageView searchIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeViews();
        setupSearchBar();
        setupRecyclerViews();
        loadMovies();
    }

    private void initializeViews() {
        searchBar = findViewById(R.id.search_bar);
        searchIcon = findViewById(R.id.search_icon);

        adventureRecyclerView = findViewById(R.id.frame_2);
        animationRecyclerView = findViewById(R.id.frame_3);
        comedyRecyclerView = findViewById(R.id.frame_4);
        heroesRecyclerView = findViewById(R.id.frame_5);
    }

    private void setupSearchBar() {
        if (searchIcon != null) {
            searchIcon.setOnClickListener(v -> openSearchActivity());
        }

        if (searchBar != null) {
            searchBar.setOnClickListener(v -> openSearchActivity());
            searchBar.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    openSearchActivity();
                    searchBar.clearFocus();
                }
            });

            searchBar.setFocusable(false);
            searchBar.setFocusableInTouchMode(false);
        }
    }

    private void setupRecyclerViews() {
        // Setup Adventure Movies RecyclerView
        if (adventureRecyclerView != null) {
            adventureAdapter = new MovieAdapter(this);
            setupRecyclerView(adventureRecyclerView, adventureAdapter);
        }

        // Setup Animation Movies RecyclerView
        if (animationRecyclerView != null) {
            animationAdapter = new MovieAdapter(this);
            setupRecyclerView(animationRecyclerView, animationAdapter);
        }

        // Setup Comedy Movies RecyclerView
        if (comedyRecyclerView != null) {
            comedyAdapter = new MovieAdapter(this);
            setupRecyclerView(comedyRecyclerView, comedyAdapter);
        }

        // Setup Heroes (Action) Movies RecyclerView
        if (heroesRecyclerView != null) {
            heroesAdapter = new MovieAdapter(this);
            setupRecyclerView(heroesRecyclerView, heroesAdapter);
        }

        setAdapterClickListeners();
    }

    private void setupRecyclerView(RecyclerView recyclerView, MovieAdapter adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void setAdapterClickListeners() {
        MovieAdapter.OnItemClickListener clickListener = this::openMovieDetails;

        if (adventureAdapter != null) {
            adventureAdapter.setOnItemClickListener(clickListener);
        }
        if (animationAdapter != null) {
            animationAdapter.setOnItemClickListener(clickListener);
        }
        if (comedyAdapter != null) {
            comedyAdapter.setOnItemClickListener(clickListener);
        }
        if (heroesAdapter != null) {
            heroesAdapter.setOnItemClickListener(clickListener);
        }
    }

    private void loadMovies() {
        loadMoviesByGenre(GENRE_ADVENTURE, adventureAdapter, "Adventure");
        loadMoviesByGenre(GENRE_ANIMATION, animationAdapter, "Animation");
        loadMoviesByGenre(GENRE_COMEDY, comedyAdapter, "Comedy");
        loadMoviesByGenre(GENRE_ACTION, heroesAdapter, "Action");
    }

    private void loadMoviesByGenre(int genreId, MovieAdapter adapter, String genreName) {
        if (adapter == null) {
            Log.w(TAG, "Adapter is null for genre: " + genreName);
            return;
        }

        MovieService service = ApiClient.getClient().create(MovieService.class);
        Call<MovieResponse> call = service.getMoviesByGenre(API_KEY, genreId, 1);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    if (movies != null && !movies.isEmpty()) {
                        // DEBUG: Log data movie yang diterima
                        Log.d(TAG, "Loaded " + movies.size() + " movies for genre: " + genreName);
                        for (Movie movie : movies) {
                            Log.d(TAG, "Movie: " + movie.getTitle() +
                                    ", Poster: " + movie.getPoster_path() +
                                    ", Full URL: " + movie.getPosterUrl());
                        }
                        adapter.setMovies(movies);
                    } else {
                        Log.e(TAG, "No movies found for genre: " + genreName + " (ID: " + genreId + ")");
                        showEmptyStateMessage(genreName);
                    }
                } else {
                    Log.e(TAG, "API Error for " + genreName + ": " + response.code() + " - " + response.message());
                    showErrorMessage("Failed to load " + genreName + " movies: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Log.e(TAG, "Network Error for " + genreName + ": " + t.getMessage());
                showErrorMessage("Network error loading " + genreName + " movies: " + t.getMessage());
            }
        });
    }

    private void showErrorMessage(String message) {
        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showEmptyStateMessage(String genreName) {
        Log.w(TAG, genreName + " movies list is empty");
        // Bisa ditambahkan UI empty state jika diperlukan
    }

    private void openSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        // Optional: tambahkan animasi transisi
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void openMovieDetails(Movie movie) {
        if (movie == null) {
            Log.e(TAG, "Movie object is null");
            Toast.makeText(this, "Error: Movie data is unavailable", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("movie_id", movie.getId());
        intent.putExtra("movie_title", movie.getTitle());

        String backdropUrl = movie.getBackdropUrl();
        if (backdropUrl != null) {
            intent.putExtra("backdrop_url", backdropUrl);
        }

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onSearchClick(View view) {
        openSearchActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}