package com.example.movies_api.repositories;

import com.example.movies_api.entities.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    List<Actor> findByNameContaining(String name); // Custom query method
}
