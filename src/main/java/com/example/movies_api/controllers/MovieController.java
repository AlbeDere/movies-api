package com.example.movies_api.controllers;

import com.example.movies_api.entities.Actor;
import com.example.movies_api.entities.Movie;
import com.example.movies_api.services.MovieService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    public ResponseEntity<Movie> createMovie(@Valid @RequestBody Movie movie,
                                              @RequestParam List<Long> genreIds,
                                              @RequestParam List<Long> actorIds) {
        Movie createdMovie = movieService.createMovie(movie, genreIds, actorIds);
        return ResponseEntity.ok(createdMovie);
    }

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies(@RequestParam(required = false) Long genre,
                                                    @RequestParam(required = false) Integer year,
                                                    @RequestParam(required = false) Long actor) {
        if (genre != null) {
            return ResponseEntity.ok(movieService.getMoviesByGenre(genre));
        } else if (year != null) {
            return ResponseEntity.ok(movieService.getMoviesByReleaseYear(year));
        } else if (actor != null) {
            return ResponseEntity.ok(movieService.getMoviesByActor(actor));
        }
        List<Movie> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Optional<Movie> movie = movieService.getMovieById(id);
        return movie.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @Valid @RequestBody Movie updatedMovie,
                                              @RequestParam List<Long> genreIds,
                                              @RequestParam List<Long> actorIds) {
        Optional<Movie> movie = movieService.updateMovie(id, updatedMovie, genreIds, actorIds);
        return movie.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{movieId}/actors")
    public ResponseEntity<List<Actor>> getActorsByMovie(@PathVariable Long movieId) {
        Set<Actor> actors = movieService.getActorsByMovie(movieId);
        // Convert Set<Actor> to List<Actor>
        List<Actor> actorList = new ArrayList<>(actors);
        return ResponseEntity.ok(actorList);
    }
}
