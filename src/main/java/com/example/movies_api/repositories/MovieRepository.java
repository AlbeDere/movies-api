package com.example.movies_api.repositories;

import com.example.movies_api.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByReleaseYear(int releaseYear);
    List<Movie> findByGenres_Id(Long genreId);
    List<Movie> findByActors_Id(Long actorId);
}
