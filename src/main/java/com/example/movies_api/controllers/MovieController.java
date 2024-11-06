package com.example.movies_api.controllers;

import com.example.movies_api.entities.Actor;
import com.example.movies_api.entities.Movie;
import com.example.movies_api.exceptions.ResourceNotFoundException;
import com.example.movies_api.services.MovieService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
                                             @RequestParam(required = false) List<Long> genreIds,
                                             @RequestParam(required = false) List<Long> actorIds) {
        Movie createdMovie = movieService.createMovie(movie, genreIds, actorIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie); // HTTP 201 Created
    }

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies(
            @RequestParam(required = false) Long genre,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long actor,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> moviePage;
        
        // Use different service methods depending on the filter criteria provided
        if (genre != null) {
            moviePage = movieService.getMoviesByGenre(genre, pageable);
        } else if (year != null) {
            moviePage = movieService.getMoviesByReleaseYear(year, pageable);
        } else if (actor != null) {
            moviePage = movieService.getMoviesByActor(actor, pageable);
        } else {
            moviePage = movieService.getAllMovies(pageable);
        }
        
        // Return only the content of the Page<Movie> object for a cleaner response
        return ResponseEntity.ok(moviePage.getContent());
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
    public ResponseEntity<String> deleteMovie(@PathVariable Long id, 
                                              @RequestParam(defaultValue = "false") boolean force) {
        try {
            movieService.deleteMovie(id, force);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content for successful deletion
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); // HTTP 400 Bad Request for relationship conflict
        }
    }

    // Get all actors in a specific movie with pagination
    @GetMapping("/{movieId}/actors")
    public ResponseEntity<List<Actor>> getActorsByMovie(
            @PathVariable Long movieId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Actor> actors = movieService.getActorsByMovie(movieId, pageable);
        
        return ResponseEntity.ok(actors.getContent());
    }

    // Search movies by title endpoint
    @GetMapping("/search")
    public ResponseEntity<?> searchMoviesByTitle(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Validate that the title is not empty
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "Title parameter cannot be empty."));
        }

        Pageable pageable = PageRequest.of(page, size);

        try {
            // Search movies by title
            Page<Movie> moviePage = movieService.searchMoviesByTitle(title, pageable);

            // If no movies are found, return a message with a 404 status
            if (moviePage.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "No movies found matching the title: " + title));
            }

            // Return the list of movies if found
            return ResponseEntity.ok(moviePage.getContent());

        } catch (ResourceNotFoundException e) {
            // Return 404 with custom message if ResourceNotFoundException is thrown
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }

}
