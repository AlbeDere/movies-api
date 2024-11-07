package com.example.movies_api.controllers;

import com.example.movies_api.entities.Genre;
import com.example.movies_api.services.GenreService;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping
    public ResponseEntity<Genre> createGenre(@Valid @RequestBody Genre genre) {
        Genre createdGenre = genreService.createGenre(genre);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGenre);
    }

    @GetMapping
    public ResponseEntity<List<Genre>> getAllGenres(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        
        Pageable pageable = (size != null) ? PageRequest.of(page, size) : Pageable.unpaged();
    
        Page<Genre> genres = genreService.getAllGenres(pageable);
        return ResponseEntity.ok(genres.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable Long id) {
        Genre genre = genreService.getGenreById(id);
        return ResponseEntity.ok(genre);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Genre> updateGenre(@PathVariable Long id, @Valid @RequestBody Genre genre) {
        Genre updatedGenre = genreService.updateGenre(id, genre.getName());
        return ResponseEntity.ok(updatedGenre);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGenre(@PathVariable Long id, 
                                            @RequestParam(defaultValue = "false") boolean force) {
        try {
            genreService.deleteGenre(id, force);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
