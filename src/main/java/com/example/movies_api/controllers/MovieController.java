package com.example.movies_api.controllers;

import com.example.movies_api.dto.MovieUpdateDTO;
import com.example.movies_api.entities.Actor;
import com.example.movies_api.entities.Movie;
import com.example.movies_api.exceptions.InvalidPaginationException;
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
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies(
            @RequestParam(required = false) Long genre,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long actor,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {

        if (page < 0 || (size != null && size <= 0)) {
            throw new InvalidPaginationException("Invalid pagination parameters: page must be non-negative and size must be positive.");
        }

        Pageable pageable = (size != null) ? PageRequest.of(page, size) : Pageable.unpaged();
        Page<Movie> moviePage;

        if (genre != null) {
            moviePage = movieService.getMoviesByGenre(genre, pageable).orElseThrow(() ->
                new ResourceNotFoundException("No movies found for genre " + genre));
        } else if (year != null) {
            moviePage = movieService.getMoviesByReleaseYear(year, pageable).orElseThrow(() ->
                new ResourceNotFoundException("No movies found for year " + year));
        } else if (actor != null) {
            moviePage = movieService.getMoviesByActor(actor, pageable).orElseThrow(() ->
                new ResourceNotFoundException("No movies found for actor " + actor));
        } else {
            moviePage = movieService.getAllMovies(pageable);
        }

        return ResponseEntity.ok(moviePage.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        Movie movie = movieService.getMovieById(id).orElseThrow(() ->
            new ResourceNotFoundException("Movie with id " + id + " not found"));
        return ResponseEntity.ok(movie);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @Valid @RequestBody MovieUpdateDTO movieUpdateDTO) {
        Optional<Movie> movie = movieService.updateMovie(id, movieUpdateDTO);
        return movie.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMovie(@PathVariable Long id,
                                              @RequestParam(defaultValue = "false") boolean force) {
        try {
            movieService.deleteMovie(id, force);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    @GetMapping("/{movieId}/actors")
    public ResponseEntity<List<Actor>> getActorsByMovie(
            @PathVariable Long movieId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {

        if (page < 0 || (size != null && size <= 0)) {
            throw new InvalidPaginationException("Invalid pagination parameters: page must be non-negative and size must be positive.");
        }

        Pageable pageable = (size != null) ? PageRequest.of(page, size) : Pageable.unpaged();
        Page<Actor> actors = movieService.getActorsByMovie(movieId, pageable).orElseThrow(() ->
            new ResourceNotFoundException("No actors found for movie with id " + movieId));

        return ResponseEntity.ok(actors.getContent());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchMoviesByTitle(
            @RequestParam String title,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {

        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "Title parameter cannot be empty."));
        }

        if (page < 0 || (size != null && size <= 0)) {
            throw new InvalidPaginationException("Invalid pagination parameters: page must be non-negative and size must be positive.");
        }

        Pageable pageable = (size != null) ? PageRequest.of(page, size) : Pageable.unpaged();

        try {
            Page<Movie> moviePage = movieService.searchMoviesByTitle(title, pageable).orElseThrow(() ->
                new ResourceNotFoundException("No movies found matching the title: " + title));

            return ResponseEntity.ok(moviePage.getContent());

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }
}
