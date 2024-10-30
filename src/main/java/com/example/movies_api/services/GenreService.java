package com.example.movies_api.services;

import com.example.movies_api.entities.Genre;
import com.example.movies_api.entities.Movie;
import com.example.movies_api.repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Optional<Genre> getGenreById(Long id) {
        return genreRepository.findById(id);
    }

    // Update an existing genre's name
    public Optional<Genre> updateGenre(Long id, String newName) {
        Optional<Genre> genreOptional = genreRepository.findById(id);
        if (genreOptional.isPresent()) {
            Genre genre = genreOptional.get();
            genre.setName(newName);
            return Optional.of(genreRepository.save(genre));
        }
        return Optional.empty();
    }

    // Remove a genre from the database
    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }

    // Fetch all movies in a specific genre (optional)
    public List<Movie> getMoviesByGenre(Long genreId) {
        Optional<Genre> genreOptional = genreRepository.findById(genreId);
        return genreOptional.map(genre -> genre.getMovies().stream().collect(Collectors.toList())).orElseGet(LinkedList::new);
    }
}
