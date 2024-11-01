package com.example.movies_api.services;

import com.example.movies_api.entities.Genre;
import com.example.movies_api.exceptions.ResourceNotFoundException;
import com.example.movies_api.repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    // Create a new genre
    public Genre createGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    // Retrieve all genres
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    // Retrieve a specific genre by ID
    public Genre getGenreById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre with id " + id + " not found"));
    }

    // Update an existing genre's name
    public Optional<Genre> updateGenre(Long id, String newName) {
        return genreRepository.findById(id).map(genre -> {
            genre.setName(newName);
            return genreRepository.save(genre);
        });
    }

    // Remove a genre from the database
    public void deleteGenre(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new ResourceNotFoundException("Genre with id " + id + " not found");
        }
        genreRepository.deleteById(id);
    }

}
