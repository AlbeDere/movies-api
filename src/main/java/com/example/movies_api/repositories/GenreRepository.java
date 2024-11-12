package com.example.movies_api.repositories;

import com.example.movies_api.entities.Genre;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByName(String name);
 }
