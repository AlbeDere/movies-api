package com.example.movies_api.controllers;

import com.example.movies_api.entities.Movie;
import com.example.movies_api.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @PostMapping
    public Movie createMovie(@RequestBody Movie movie) {
        return movieService.createMovie(movie);
    }

    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id);
    }

    @GetMapping("/filter")
    public List<Movie> filterMovies(@RequestParam(required = false) Long genreId,
                                    @RequestParam(required = false) Integer year,
                                    @RequestParam(required = false) Long actorId) {
        if (genreId != null) {
            return movieService.getMoviesByGenre(genreId);
        } else if (year != null) {
            return movieService.getMoviesByYear(year);
        } else if (actorId != null) {
            return movieService.getMoviesByActor(actorId);
        }
        return movieService.getAllMovies();
    }

    @PatchMapping("/{id}")
    public Movie updateMovie(@PathVariable Long id, @RequestBody Movie updatedMovie) {
        return movieService.updateMovie(id, updatedMovie);
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }
}
