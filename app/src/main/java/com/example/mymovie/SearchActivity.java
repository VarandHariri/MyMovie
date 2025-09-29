package com.example.mymovie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovie.adapter.SearchMovieAdapter;
import com.example.mymovie.model.Movie;
import com.example.mymovie.model.MovieResponse;
import com.example.mymovie.network.ApiClient;
import com.example.mymovie.network.MovieService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    private static final String API_KEY = "13991a4ec029782d650857fa1d158fea";
    private static final String TAG = "SearchActivity";

    private EditText searchEditText;
    private ImageView searchButton, backButton;
    private RecyclerView searchResultsRecyclerView;
    private TextView searchResultsTitle, emptyStateText;
    private SearchMovieAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initializeViews();
        setupRecyclerView();
        setupSearchFunctionality();
    }

    private void initializeViews() {
        searchEditText = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);
        backButton = findViewById(R.id.back_button);
        searchResultsRecyclerView = findViewById(R.id.search_results_recycler_view);
        searchResultsTitle = findViewById(R.id.search_results_title);
        emptyStateText = findViewById(R.id.empty_state_text);
    }

    private void setupRecyclerView() {
        searchAdapter = new SearchMovieAdapter(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        searchResultsRecyclerView.setLayoutManager(gridLayoutManager);
        searchResultsRecyclerView.setAdapter(searchAdapter);

        searchAdapter.setOnItemClickListener(this::openMovieDetails);
    }

    private void setupSearchFunctionality() {
        searchButton.setOnClickListener(v -> performSearch());

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();

        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a movie title", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Searching for: " + query);
        searchMovies(query);
    }

    private void searchMovies(String query) {
        // Show loading
        searchResultsTitle.setText("Searching for \"" + query + "\"...");
        emptyStateText.setVisibility(View.GONE);

        MovieService service = ApiClient.getClient().create(MovieService.class);
        Call<MovieResponse> call = service.searchMovies(API_KEY, query, 1);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().getResults();
                    if (movies != null && !movies.isEmpty()) {
                        searchResultsTitle.setText("Search Results (" + movies.size() + " movies)");
                        searchAdapter.setMovies(movies);
                        emptyStateText.setVisibility(View.GONE);
                    } else {
                        searchResultsTitle.setText("No results for \"" + query + "\"");
                        searchAdapter.clearMovies();
                        emptyStateText.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openMovieDetails(Movie movie) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("movie_id", movie.getId());
        startActivity(intent);
    }
}