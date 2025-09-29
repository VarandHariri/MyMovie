package com.example.mymovie.model;

import java.util.List;

public class VideoResponse {
    private int id;
    private List<Video> results;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public List<Video> getResults() { return results; }
    public void setResults(List<Video> results) { this.results = results; }

    public static class Video {
        private String id;
        private String key;
        private String name;
        private String site;
        private String type;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSite() { return site; }
        public void setSite(String site) { this.site = site; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}