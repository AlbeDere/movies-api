package com.example.movies_api.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.movies_api.entities.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    Page<Actor> findByNameContainingIgnoreCase(String name, Pageable pageable);
    @Query("SELECT a FROM Actor a JOIN a.movies m WHERE m.id = :movieId")
    Page<Actor> findByMovieId(@Param("movieId") Long movieId, Pageable pageable);
    Optional<Actor> findByName(String name);

}