package com.example.movies_api.dto;

import java.util.List;

public class MovieUpdateDTO {
    private String title;
    private int releaseYear;
    private int duration;
    private List<String> genreNames;
    private List<String> actorNames;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getReleaseYear() { return releaseYear; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public List<String> getGenreNames() { return genreNames; }
    public void setGenreNames(List<String> genreNames) { this.genreNames = genreNames; }
    public List<String> getActorNames() { return actorNames; }
    public void setActorNames(List<String> actorNames) { this.actorNames = actorNames; }
}
