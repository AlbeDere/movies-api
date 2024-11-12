package com.example.movies_api.services;

import com.example.movies_api.dto.MovieUpdateDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    public Movie createMovie(Movie movie, List<Long> genreIds, List<Long> actorIds) {
        Set<Genre> genres = new HashSet<>();
        
        if (genreIds != null) {
            for (Long genreId : genreIds) {
                Genre genre = genreRepository.findById(genreId)
                    .orElseThrow(() -> new ResourceNotFoundException("Genre with id " + genreId + " not found"));
                genres.add(genre);
            }
        }
        movie.setGenres(genres);
    
        Set<Actor> actors = new HashSet<>();
        
        if (actorIds != null) {
            for (Long actorId : actorIds) {
                Actor actor = actorRepository.findById(actorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Actor with id " + actorId + " not found"));
                actors.add(actor);
            }
        }
        movie.setActors(actors);
    
        return movieRepository.save(movie);
    }

    public Page<Movie> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    public Optional<Page<Movie>> getMoviesByGenre(Long genreId, Pageable pageable) {
        Optional<Genre> genre = genreRepository.findById(genreId);
        if (genre.isPresent()) {
            return Optional.of(movieRepository.findByGenres_Id(genreId, pageable));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Page<Movie>> getMoviesByReleaseYear(int releaseYear, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByReleaseYear(releaseYear, pageable);
        return movies.isEmpty() ? Optional.empty() : Optional.of(movies);
    }

    public Optional<Page<Actor>> getActorsByMovie(Long movieId, Pageable pageable) {
        Optional<Movie> movie = movieRepository.findById(movieId);
        if (movie.isPresent()) {
            return Optional.of(actorRepository.findByMovieId(movieId, pageable));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Page<Movie>> getMoviesByActor(Long actorId, Pageable pageable) {
        Optional<Actor> actor = actorRepository.findById(actorId);
        if (actor.isPresent()) {
            return Optional.of(movieRepository.findByActors_Id(actorId, pageable));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Movie> updateMovie(Long id, MovieUpdateDTO movieUpdateDTO) {
        Optional<Movie> existingMovie = movieRepository.findById(id);
        if (existingMovie.isPresent()) {
            Movie movie = existingMovie.get();
            movie.setTitle(movieUpdateDTO.getTitle());
            movie.setReleaseYear(movieUpdateDTO.getReleaseYear());
            movie.setDuration(movieUpdateDTO.getDuration());

            Set<Genre> genres = new HashSet<>();
            if (movieUpdateDTO.getGenreNames() != null) {
                for (String genreName : movieUpdateDTO.getGenreNames()) {
                    Genre genre = genreRepository.findByName(genreName)
                        .orElseThrow(() -> new ResourceNotFoundException("Genre with name " + genreName + " not found"));
                    genres.add(genre);
                }
            }
            movie.setGenres(genres);

            if (movieUpdateDTO.getActorNames() == null) {
            } else if (movieUpdateDTO.getActorNames().isEmpty()) {
                movie.getActors().clear();
            } else {
                Set<Actor> actors = new HashSet<>();
                for (String actorName : movieUpdateDTO.getActorNames()) {
                    Actor actor = actorRepository.findByName(actorName)
                        .orElseThrow(() -> new ResourceNotFoundException("Actor with name " + actorName + " not found"));
                    actors.add(actor);
                }
                movie.setActors(actors);
            }

            return Optional.of(movieRepository.save(movie));
        } else {
            return Optional.empty();
        }
    }

    public void deleteMovie(Long id, boolean force) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie with id " + id + " not found"));

        boolean hasAssociations = !movie.getActors().isEmpty() || !movie.getGenres().isEmpty();

        if (!force && hasAssociations) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Cannot delete movie '" + movie.getTitle() + 
                    "' because it has associated actors and genres. " +
                    "Use force deletion to remove associations.");
        }

        if (force) {
            for (Actor actor : movie.getActors()) {
                actor.getMovies().remove(movie);
                actorRepository.save(actor);
            }

            for (Genre genre : movie.getGenres()) {
                genre.getMovies().remove(movie);
                genreRepository.save(genre);
            }

            movie.getActors().clear();
            movie.getGenres().clear();

            movieRepository.save(movie);
        }

        movieRepository.deleteById(id);
    }

    public Optional<Page<Movie>> searchMoviesByTitle(String title, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByTitleContainingIgnoreCase(title, pageable);
        return movies.isEmpty() ? Optional.empty() : Optional.of(movies);
    }
}
