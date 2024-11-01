package com.example.movies_api.controllers;

import com.example.movies_api.entities.Actor;
import com.example.movies_api.entities.Movie;
import com.example.movies_api.services.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie); // HTTP 201 Created
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
        return ResponseEntity.ok(movies); // HTTP 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie); // HTTP 200 OK
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @Valid @RequestBody Movie updatedMovie,
                                             @RequestParam List<Long> genreIds,
                                             @RequestParam List<Long> actorIds) {
        Optional<Movie> movie = movieService.updateMovie(id, updatedMovie, genreIds, actorIds);
        return movie.map(ResponseEntity::ok) // HTTP 200 OK
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // HTTP 404 Not Found if movie not found
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }

    @GetMapping("/{movieId}/actors")
    public ResponseEntity<List<Actor>> getActorsByMovie(@PathVariable Long movieId) {
        Set<Actor> actors = movieService.getActorsByMovie(movieId);
        List<Actor> actorList = new ArrayList<>(actors);
        return ResponseEntity.ok(actorList); // HTTP 200 OK
    }
}
