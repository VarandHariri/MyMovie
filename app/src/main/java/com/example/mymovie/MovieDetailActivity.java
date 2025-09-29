package com.example.mymovie;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mymovie.model.MovieDetail;
import com.example.mymovie.model.VideoResponse;
import com.example.mymovie.network.ApiClient;
import com.example.mymovie.network.MovieService;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailActivity extends AppCompatActivity {
    private static final String TAG = "MovieDetailActivity";
    private static final String API_KEY = "13991a4ec029782d650857fa1d158fea";

    private YouTubePlayerView youTubePlayerView;
    private ImageView backdropImage;
    private ImageView playButton;
    private ImageView backButton;
    private ProgressBar loadingIndicator;
    private String trailerKey;
    private boolean isTrailerPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_movie_detail);
            Log.d(TAG, "Layout inflated successfully");

            initViews();

            int movieId = getIntent().getIntExtra("movie_id", -1);
            Log.d(TAG, "Movie ID received: " + movieId);

            if (movieId != -1) {
                loadMovieDetails(movieId);
                loadMovieVideos(movieId);
            } else {
                Toast.makeText(this, "Invalid movie ID", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error opening movie details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        try {
            Log.d(TAG, "Initializing views...");

            youTubePlayerView = findViewById(R.id.youtube_player_view);
            backdropImage = findViewById(R.id.backdrop_image);
            playButton = findViewById(R.id.play_button);
            backButton = findViewById(R.id.back_button);
            loadingIndicator = findViewById(R.id.loading_indicator);

            Log.d(TAG, "Views initialized successfully");

            setupYouTubePlayer();

            youTubePlayerView.setVisibility(View.GONE);
            backdropImage.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.GONE);

        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
            throw e;
        }
    }

    private void setupYouTubePlayer() {
        try {
            Log.d(TAG, "Setting up YouTube player...");

            IFramePlayerOptions options = new IFramePlayerOptions.Builder()
                    .controls(1)
                    .fullscreen(1)
                    .autoplay(0)
                    .build();

            youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(YouTubePlayer youTubePlayer) {
                    Log.d(TAG, "YouTube Player ready");
                }


                public void onError(YouTubePlayer youTubePlayer, String error) {
                    Log.e(TAG, "YouTube Player error: " + error);
                    runOnUiThread(() -> {
                        Toast.makeText(MovieDetailActivity.this, "Trailer not available: " + error, Toast.LENGTH_SHORT).show();
                        showBackdropOnlyUI();
                    });
                }
            }, options);

            getLifecycle().addObserver(youTubePlayerView);
            Log.d(TAG, "YouTube player setup completed");

        } catch (Exception e) {
            Log.e(TAG, "Error setting up YouTube player: " + e.getMessage(), e);
            // Fallback: hide YouTube player completely
            if (youTubePlayerView != null) {
                youTubePlayerView.setVisibility(View.GONE);
            }
        }
    }

    private void loadMovieDetails(int movieId) {
        Log.d(TAG, "Loading movie details for ID: " + movieId);

        try {
            MovieService service = ApiClient.getClient().create(MovieService.class);
            Call<MovieDetail> call = service.getMovieDetails(movieId, API_KEY);

            call.enqueue(new Callback<MovieDetail>() {
                @Override
                public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            displayMovieDetails(response.body());
                        } else {
                            Log.e(TAG, "Failed to load movie details. Code: " + response.code());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing movie details response", e);
                    }
                }

                @Override
                public void onFailure(Call<MovieDetail> call, Throwable t) {
                    Log.e(TAG, "Network error loading movie details: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading movie details", e);
        }
    }

    private void loadMovieVideos(int movieId) {
        Log.d(TAG, "Loading videos for movie ID: " + movieId);

        runOnUiThread(() -> {
            if (loadingIndicator != null) {
                loadingIndicator.setVisibility(View.VISIBLE);
            }
        });

        try {
            MovieService service = ApiClient.getClient().create(MovieService.class);
            Call<VideoResponse> call = service.getMovieVideos(movieId, API_KEY);

            call.enqueue(new Callback<VideoResponse>() {
                @Override
                public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                    runOnUiThread(() -> {
                        if (loadingIndicator != null) {
                            loadingIndicator.setVisibility(View.GONE);
                        }
                    });

                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            VideoResponse videoResponse = response.body();
                            if (videoResponse.getResults() != null && !videoResponse.getResults().isEmpty()) {
                                for (VideoResponse.Video video : videoResponse.getResults()) {
                                    if ("YouTube".equals(video.getSite()) && "Trailer".equals(video.getType())) {
                                        trailerKey = video.getKey();
                                        Log.d(TAG, "Trailer found: " + trailerKey);
                                        showTrailerAvailableUI();
                                        return;
                                    }
                                }
                            }
                        }
                        showBackdropOnlyUI();
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing videos response", e);
                        showBackdropOnlyUI();
                    }
                }

                @Override
                public void onFailure(Call<VideoResponse> call, Throwable t) {
                    runOnUiThread(() -> {
                        if (loadingIndicator != null) {
                            loadingIndicator.setVisibility(View.GONE);
                        }
                    });
                    Log.e(TAG, "Network error loading videos: " + t.getMessage());
                    showBackdropOnlyUI();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading videos", e);
            runOnUiThread(() -> {
                if (loadingIndicator != null) {
                    loadingIndicator.setVisibility(View.GONE);
                }
            });
            showBackdropOnlyUI();
        }
    }

    private void showTrailerAvailableUI() {
        runOnUiThread(() -> {
            try {
                if (youTubePlayerView != null) {
                    youTubePlayerView.setVisibility(View.GONE);
                }
                if (backdropImage != null) {
                    backdropImage.setVisibility(View.VISIBLE);
                }
                if (playButton != null) {
                    playButton.setVisibility(View.VISIBLE);
                }

                String backdropUrl = getIntent().getStringExtra("backdrop_url");
                if (backdropUrl != null && !backdropUrl.isEmpty() && backdropImage != null) {
                    Glide.with(MovieDetailActivity.this)
                            .load(backdropUrl)
                            .into(backdropImage);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error showing trailer UI", e);
            }
        });
    }

    private void showBackdropOnlyUI() {
        runOnUiThread(() -> {
            try {
                if (youTubePlayerView != null) {
                    youTubePlayerView.setVisibility(View.GONE);
                }
                if (backdropImage != null) {
                    backdropImage.setVisibility(View.VISIBLE);
                }
                if (playButton != null) {
                    playButton.setVisibility(View.GONE);
                }

                // Load backdrop image
                String backdropUrl = getIntent().getStringExtra("backdrop_url");
                if (backdropUrl != null && !backdropUrl.isEmpty() && backdropImage != null) {
                    Glide.with(MovieDetailActivity.this)
                            .load(backdropUrl)
                            .into(backdropImage);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error showing backdrop UI", e);
            }
        });
    }

    public void onPlayTrailerClick(View view) {
        try {
            if (trailerKey != null && !isTrailerPlaying && youTubePlayerView != null) {
                Log.d(TAG, "Playing trailer: " + trailerKey);

                youTubePlayerView.setVisibility(View.VISIBLE);
                if (backdropImage != null) {
                    backdropImage.setVisibility(View.GONE);
                }
                if (playButton != null) {
                    playButton.setVisibility(View.GONE);
                }
                isTrailerPlaying = true;

                youTubePlayerView.getYouTubePlayerWhenReady(youTubePlayer -> {
                    try {
                        youTubePlayer.loadVideo(trailerKey, 0);
                    } catch (Exception e) {
                        Log.e(TAG, "Error playing video", e);
                        runOnUiThread(() -> {
                            Toast.makeText(MovieDetailActivity.this, "Error playing trailer", Toast.LENGTH_SHORT).show();
                            showBackdropOnlyUI();
                        });
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in play trailer click", e);
            Toast.makeText(this, "Error playing trailer", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayMovieDetails(MovieDetail movie) {
        try {
            Log.d(TAG, "Displaying movie details: " + movie.getTitle());

            // Set title
            TextView title = findViewById(R.id.movie_title);
            if (title != null) {
                title.setText(movie.getTitle() != null ? movie.getTitle().toUpperCase() : "No Title");
            }

            // Set basic info
            TextView info = findViewById(R.id.movie_info);
            if (info != null) {
                StringBuilder infoText = new StringBuilder();

                if (movie.getRelease_date() != null && movie.getRelease_date().length() >= 4) {
                    infoText.append(movie.getRelease_date().substring(0, 4));
                } else {
                    infoText.append("N/A");
                }

                infoText.append(" • ")
                        .append(String.format("%.1f", movie.getVote_average()))
                        .append("/10");

                if (movie.getRuntime() > 0) {
                    infoText.append(" • ")
                            .append(movie.getRuntime())
                            .append(" min");
                }

                info.setText(infoText.toString());
            }

            // Set genres
            TextView genres = findViewById(R.id.movie_genres);
            if (genres != null) {
                StringBuilder genreText = new StringBuilder();
                if (movie.getGenres() != null && !movie.getGenres().isEmpty()) {
                    for (MovieDetail.Genre genre : movie.getGenres()) {
                        genreText.append(genre.getName()).append(" • ");
                    }
                    if (genreText.length() > 2) {
                        genreText.setLength(genreText.length() - 3);
                    }
                } else {
                    genreText.append("No genres available");
                }
                genres.setText(genreText.toString());
            }

            // Set overview
            TextView overview = findViewById(R.id.movie_overview);
            if (overview != null) {
                overview.setText(movie.getOverview() != null ? movie.getOverview() : "No overview available");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error displaying movie details", e);
        }
    }

    public void onBackClick(View view) {
        try {
            if (isTrailerPlaying && youTubePlayerView != null) {
                youTubePlayerView.getYouTubePlayerWhenReady(youTubePlayer -> {
                    try {
                        youTubePlayer.pause();
                    } catch (Exception e) {
                        Log.e(TAG, "Error pausing video", e);
                    }
                });

                youTubePlayerView.setVisibility(View.GONE);
                if (backdropImage != null) {
                    backdropImage.setVisibility(View.VISIBLE);
                }
                if (playButton != null) {
                    playButton.setVisibility(trailerKey != null ? View.VISIBLE : View.GONE);
                }
                isTrailerPlaying = false;
            } else {
                finish();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in back click", e);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (youTubePlayerView != null) {
                youTubePlayerView.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy", e);
        }
    }
}