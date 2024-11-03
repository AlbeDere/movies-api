package com.example.movies_api.services;

import com.example.movies_api.entities.Actor;
import com.example.movies_api.entities.Genre;
import com.example.movies_api.entities.Movie;
import com.example.movies_api.exceptions.ResourceNotFoundException;
import com.example.movies_api.repositories.MovieRepository;
import com.example.movies_api.repositories.GenreRepository;
import com.example.movies_api.repositories.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
            Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre with id " + genreId + " not found"));
            genres.add(genre);
        }
        movie.setGenres(genres);
    
        Set<Actor> actors = new HashSet<>();
        for (Long actorId : actorIds) {
            Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor with id " + actorId + " not found"));
            actors.add(actor);
        }
        movie.setActors(actors);
    
        return movieRepository.save(movie);
    }

    // Retrieve all movies
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // Retrieve a specific movie by ID
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id).orElseThrow(() -> 
        new ResourceNotFoundException("Movie with id " + id + " not found"));
}

    // Filter movies by genre
    public List<Movie> getMoviesByGenre(Long genreId) {
        // Check if the genre exists
        genreRepository.findById(genreId).orElseThrow(() -> 
            new ResourceNotFoundException("Genre with id " + genreId + " not found"));
        
        // If the genre exists, retrieve movies associated with it
        return movieRepository.findAll().stream()
            .filter(movie -> movie.getGenres().stream().anyMatch(genre -> genre.getId().equals(genreId)))
            .toList();
    }
    

    // Filter movies by release year
    public List<Movie> getMoviesByReleaseYear(int releaseYear) {
        List<Movie> movies = movieRepository.findAll().stream()
            .filter(movie -> movie.getReleaseYear() == releaseYear)
            .toList();
    
        // If no movies are found for the specified release year, throw an exception
        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("No movies found for release year " + releaseYear);
        }
    
        return movies;
    }
    

    // Get all actors in a specific movie
    public Set<Actor> getActorsByMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new ResourceNotFoundException("Movie with id " + movieId + " not found"));
        return movie.getActors();
    }
    
    
    public List<Movie> getMoviesByActor(Long actorId) {
        Actor actor = actorRepository.findById(actorId)
            .orElseThrow(() -> new ResourceNotFoundException("Actor with id " + actorId + " not found"));
        return actor.getMovies().stream().toList();
    }
    

    // Update movie details
    public Optional<Movie> updateMovie(Long id, Movie updatedMovie, List<Long> genreIds, List<Long> actorIds) {
        Movie existingMovie = movieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movie with id " + id + " not found"));
    
        existingMovie.setTitle(updatedMovie.getTitle());
        existingMovie.setReleaseYear(updatedMovie.getReleaseYear());
        existingMovie.setDuration(updatedMovie.getDuration());
    
        Set<Genre> genres = new HashSet<>();
        for (Long genreId : genreIds) {
            Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre with id " + genreId + " not found"));
            genres.add(genre);
        }
        existingMovie.setGenres(genres);
    
        Set<Actor> actors = new HashSet<>();
        for (Long actorId : actorIds) {
            Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> new ResourceNotFoundException("Actor with id " + actorId + " not found"));
            actors.add(actor);
        }
        existingMovie.setActors(actors);
    
        return Optional.of(movieRepository.save(existingMovie));
    }


    // Remove a movie
    public void deleteMovie(Long id, boolean force) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie with id " + id + " not found"));

        // Check for associated actors and genres
        boolean hasAssociations = !movie.getActors().isEmpty() || !movie.getGenres().isEmpty();

        // If force is false and associations exist, prevent deletion
        if (!force && hasAssociations) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Cannot delete movie '" + movie.getTitle() + 
                    "' because it has associated actors and genres. " +
                    "Use force deletion to remove associations.");
        }

        // If force is true, clear associations
        if (force) {
            // Remove the movie from each actor's movie list
            for (Actor actor : movie.getActors()) {
                actor.getMovies().remove(movie);
                actorRepository.save(actor); // Update actor in the database
            }

            // Remove the movie from each genre's movie list
            for (Genre genre : movie.getGenres()) {
                genre.getMovies().remove(movie);
                genreRepository.save(genre); // Update genre in the database
            }

            // Clear movie's own lists of actors and genres
            movie.getActors().clear();
            movie.getGenres().clear();

            // Update the movie without any associations
            movieRepository.save(movie);
        }

        // Delete the movie
        movieRepository.deleteById(id);
    }
}