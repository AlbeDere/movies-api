package com.example.movies_api.controllers;

import com.example.movies_api.entities.Genre;
import com.example.movies_api.services.GenreService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    // Create a new genre
    @PostMapping
    public ResponseEntity<Genre> createGenre(@Valid @RequestBody Genre genre) {
        Genre createdGenre = genreService.createGenre(genre);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGenre); // HTTP 201 Created
    }

    // Retrieve all genres
    @GetMapping
    public ResponseEntity<List<Genre>> getAllGenres() {
        List<Genre> genres = genreService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    // Retrieve a specific genre by ID
    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable Long id) {
        Genre genre = genreService.getGenreById(id);  // Directly calls the service, which throws exception if not found
        return ResponseEntity.ok(genre);  // If found, returns the genre
    }

    // Update an existing genre's name
    @PatchMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(@PathVariable Long id, @Valid @RequestBody Genre genre) {
        // Attempt to update the genre
        Optional<Genre> updatedGenre = genreService.updateGenre(id, genre.getName());
        
        // Return updated genre with HTTP 200 OK if found, otherwise return HTTP 404 Not Found
        return updatedGenre.map(ResponseEntity::ok) // HTTP 200 OK
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // HTTP 404 Not Found
    }
    

    // Delete a genre
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);  // Service handles exception if genre does not exist
        return ResponseEntity.noContent().build();  // Returns 204 No Content on successful deletion
    }
}
