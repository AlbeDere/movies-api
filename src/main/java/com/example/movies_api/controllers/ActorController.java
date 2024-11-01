package com.example.movies_api.controllers;

import com.example.movies_api.entities.Actor;
import com.example.movies_api.entities.Movie;
import com.example.movies_api.services.ActorService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/actors")
public class ActorController {

    private final ActorService actorService;

    @Autowired
    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @PostMapping
    public ResponseEntity<Actor> createActor(@Validated @RequestBody Actor actor) {
        Actor createdActor = actorService.createActor(actor);
        return ResponseEntity.ok(createdActor);
    }

    @GetMapping
    public ResponseEntity<List<Actor>> getAllActors(@RequestParam(required = false) String name) {
        if (name != null) {
            return ResponseEntity.ok(actorService.getActorsByName(name));
        }
        List<Actor> actors = actorService.getAllActors();
        return ResponseEntity.ok(actors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Actor> getActorById(@PathVariable Long id) {
        Optional<Actor> actor = actorService.getActorById(id);
        return actor.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Actor> updateActor(@PathVariable Long id, @Valid @RequestBody Actor updatedActor) {
        Optional<Actor> actor = actorService.updateActor(id, updatedActor);
        return actor.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable Long id) {
        actorService.deleteActor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{actorId}/movies")
    public ResponseEntity<List<Movie>> getMoviesByActor(@PathVariable Long actorId) {
        Set<Movie> movies = actorService.getMoviesByActor(actorId);
        // Convert Set<Movie> to List<Movie>
        List<Movie> movieList = new ArrayList<>(movies);
        return ResponseEntity.ok(movieList);
    }
}
