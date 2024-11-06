package com.example.movies_api.repositories;

import com.example.movies_api.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findByReleaseYear(int releaseYear, Pageable pageable);
    Page<Movie> findByGenres_Id(Long genreId, Pageable pageable);
    Page<Movie> findByActors_Id(Long actorId, Pageable pageable);
    @Query("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Movie> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);
}

