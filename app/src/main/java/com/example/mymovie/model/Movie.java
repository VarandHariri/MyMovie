package com.example.mymovie.model;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    private int id;
    private String title;
    private String overview;
    private String poster_path;
    private String backdrop_path;
    private double vote_average;
    private String release_date;
    private List<Integer> genre_ids;

    public Movie(int id, String title, String overview, String poster_path,
                 String backdrop_path, double vote_average, String release_date,
                 List<Integer> genre_ids) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.poster_path = poster_path;
        this.backdrop_path = backdrop_path;
        this.vote_average = vote_average;
        this.release_date = release_date;
        this.genre_ids = genre_ids;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview != null ? overview : "";
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPoster_path() {
        return poster_path != null ? poster_path : "";
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getBackdrop_path() {
        return backdrop_path != null ? backdrop_path : "";
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date != null ? release_date : "";
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public List<Integer> getGenre_ids() {
        return genre_ids != null ? genre_ids : new ArrayList<>();
    }

    public void setGenre_ids(List<Integer> genre_ids) {
        this.genre_ids = genre_ids;
    }

    public String getPosterUrl() {
        if (poster_path != null && !poster_path.isEmpty() && !poster_path.equals("null")) {
            String cleanPath = poster_path.startsWith("/") ? poster_path.substring(1) : poster_path;
            return "https://image.tmdb.org/t/p/w500/" + cleanPath;
        }
        return null;
    }

    public String getBackdropUrl() {
        if (backdrop_path != null && !backdrop_path.isEmpty() && !backdrop_path.equals("null")) {
            String cleanPath = backdrop_path.startsWith("/") ? backdrop_path.substring(1) : backdrop_path;
            return "https://image.tmdb.org/t/p/w780/" + cleanPath;
        }
        return null;
    }

    public String getReleaseYear() {
        if (release_date != null && release_date.length() >= 4 && !release_date.equals("null")) {
            return release_date.substring(0, 4);
        }
        return "N/A";
    }

    public String getFormattedRating() {
        if (vote_average > 0) {
            return String.format("%.1f/10", vote_average);
        }
        return "No rating";
    }

    public String getShortOverview() {
        if (overview != null && overview.length() > 150) {
            return overview.substring(0, 150) + "...";
        }
        return overview != null ? overview : "No overview available";
    }

    public boolean hasPoster() {
        return poster_path != null && !poster_path.isEmpty() && !poster_path.equals("null");
    }

    public boolean hasBackdrop() {
        return backdrop_path != null && !backdrop_path.isEmpty() && !backdrop_path.equals("null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return id == movie.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", vote_average=" + vote_average +
                ", release_date='" + release_date + '\'' +
                '}';
    }
}