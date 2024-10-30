package com.example.movies_api.services;

import com.example.movies_api.entities.Actor;
import com.example.movies_api.entities.Genre;
import com.example.movies_api.entities.Movie;
import com.example.movies_api.repositories.MovieRepository;
import com.example.movies_api.repositories.GenreRepository;
import com.example.movies_api.repositories.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final ActorRepository actorRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository, GenreRepository genreRepository, ActorRepository actorRepository) {
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.actorRepository = actorRepository;
    }

    // Create a new movie
    public Movie createMovie(Movie movie, List<Long> genreIds, List<Long> actorIds) {
        Set<Genre> genres = new HashSet<>();
        for (Long genreId : genreIds) {
            genreRepository.findById(genreId).ifPresent(genres::add);
        }
        movie.setGenres(genres);

        Set<Actor> actors = new HashSet<>();
        for (Long actorId : actorIds) {
            actorRepository.findById(actorId).ifPresent(actors::add);
        }
        movie.setActors(actors);

        return movieRepository.save(movie);
    }

    // Retrieve all movies
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // Retrieve a specific movie by ID
    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    // Filter movies by genre
    public List<Movie> getMoviesByGenre(Long genreId) {
        return movieRepository.findAll().stream()
            .filter(movie -> movie.getGenres().stream().anyMatch(genre -> genre.getId().equals(genreId)))
            .toList();
    }

    // Filter movies by release year
    public List<Movie> getMoviesByReleaseYear(int releaseYear) {
        return movieRepository.findAll().stream()
            .filter(movie -> movie.getReleaseYear() == releaseYear)
            .toList();
    }

    // Get all actors in a specific movie
    public Set<Actor> getActorsByMovie(Long movieId) {
        return movieRepository.findById(movieId)
            .map(Movie::getActors)
            .orElseGet(Set::of);
    }
    
    public List<Movie> getMoviesByActor(Long actorId) {
        Optional<Actor> actor = actorRepository.findById(actorId);
        if (actor.isPresent()) {
            return actor.get().getMovies().stream().toList();
        }
        return List.of();
    }

    // Update movie details
    public Optional<Movie> updateMovie(Long id, Movie updatedMovie, List<Long> genreIds, List<Long> actorIds) {
        Optional<Movie> existingMovieOptional = movieRepository.findById(id);
        if (existingMovieOptional.isPresent()) {
            Movie existingMovie = existingMovieOptional.get();
            existingMovie.setTitle(updatedMovie.getTitle());
            existingMovie.setReleaseYear(updatedMovie.getReleaseYear());
            existingMovie.setDuration(updatedMovie.getDuration());

            Set<Genre> genres = new HashSet<>();
            for (Long genreId : genreIds) {
                genreRepository.findById(genreId).ifPresent(genres::add);
            }
            existingMovie.setGenres(genres);

            Set<Actor> actors = new HashSet<>();
            for (Long actorId : actorIds) {
                actorRepository.findById(actorId).ifPresent(actors::add);
            }
            existingMovie.setActors(actors);

            return Optional.of(movieRepository.save(existingMovie));
        }
        return Optional.empty();
    }

    // Remove a movie
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }
}
