package com.example.movies_api.services;

import com.example.movies_api.entities.Genre;
import com.example.movies_api.entities.Movie;
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

    public Genre createGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    public Page<Genre> getAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable);
    }

    public Genre getGenreById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre with id " + id + " not found"));
    }

    public Optional<Genre> updateGenre(Long id, String newName) {
        return genreRepository.findById(id).map(genre -> {
            genre.setName(newName);
            return genreRepository.save(genre);
        });
    }

    public void deleteGenre(Long id, boolean force) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre with id " + id + " not found"));

        List<Movie> associatedMovies = genre.getMovies().stream().toList();

        if (!force && !associatedMovies.isEmpty()) {
            throw new IllegalStateException("Cannot delete genre '" + genre.getName() + 
                                            "' because it has " + associatedMovies.size() + " associated movies.");
        }

        if (force) {
            for (Movie movie : associatedMovies) {
                movie.getGenres().remove(genre);
                movieRepository.save(movie);
            }
        }
        genreRepository.deleteById(id);
    }
}
