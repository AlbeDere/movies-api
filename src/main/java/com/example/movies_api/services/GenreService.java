package com.example.movies_api.services;

import com.example.movies_api.entities.Genre;
import com.example.movies_api.entities.Movie; // Import Movie entity
import com.example.movies_api.exceptions.ResourceNotFoundException;
import com.example.movies_api.repositories.GenreRepository;
import com.example.movies_api.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository, MovieRepository movieRepository) {
        this.genreRepository = genreRepository;
        this.movieRepository = movieRepository;
    }

    // Create a new genre
    public Genre createGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    // Retrieve all genres
    public Page<Genre> getAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable);
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

    // Remove a genre from the database with an option for forced deletion
    public void deleteGenre(Long id, boolean force) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre with id " + id + " not found"));

        // Check for associated movies
        List<Movie> associatedMovies = genre.getMovies().stream().toList();

        // If force is false and there are associated movies, prevent deletion
        if (!force && !associatedMovies.isEmpty()) {
            throw new IllegalStateException("Cannot delete genre '" + genre.getName() + 
                                            "' because it has " + associatedMovies.size() + " associated movies.");
        }

        // If force is true, detach the genre from all associated movies
        if (force) {
            for (Movie movie : associatedMovies) {
                movie.getGenres().remove(genre);  // Remove genre from movie's genre list
                movieRepository.save(movie);      // Update movie in the database
            }
        }

        // Proceed to delete the genre
        genreRepository.deleteById(id);
    }
}
