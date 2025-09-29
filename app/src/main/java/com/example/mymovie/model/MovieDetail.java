package com.example.mymovie.model;

import java.util.List;

public class MovieDetail {
    private int id;
    private String title;
    private String overview;
    private String poster_path;
    private String backdrop_path;
    private double vote_average;
    private String release_date;
    private int runtime;
    private List<Genre> genres;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }

    public String getPoster_path() { return poster_path; }
    public void setPoster_path(String poster_path) { this.poster_path = poster_path; }

    public String getBackdrop_path() { return backdrop_path; }
    public void setBackdrop_path(String backdrop_path) { this.backdrop_path = backdrop_path; }

    public double getVote_average() { return vote_average; }
    public void setVote_average(double vote_average) { this.vote_average = vote_average; }

    public String getRelease_date() { return release_date; }
    public void setRelease_date(String release_date) { this.release_date = release_date; }

    public int getRuntime() { return runtime; }
    public void setRuntime(int runtime) { this.runtime = runtime; }

    public List<Genre> getGenres() { return genres; }
    public void setGenres(List<Genre> genres) { this.genres = genres; }

    public String getBackdropUrl() {
        return "https://image.tmdb.org/t/p/w780" + backdrop_path;
    }

    public static class Genre {
        private int id;
        private String name;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}