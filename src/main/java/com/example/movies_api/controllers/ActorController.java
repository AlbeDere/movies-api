package com.example.movies_api.controllers;

import com.example.movies_api.entities.Actor;
import com.example.movies_api.entities.Movie;
import com.example.movies_api.exceptions.InvalidPaginationException;
import com.example.movies_api.exceptions.ResourceNotFoundException;
import com.example.movies_api.services.ActorService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/actors")
public class ActorController {

    private final ActorService actorService;

    @Autowired
    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @PostMapping
    public ResponseEntity<Actor> createActor(@Valid @RequestBody Actor actor) {
        Actor createdActor = actorService.createActor(actor);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdActor);
    }

    @GetMapping
    public ResponseEntity<List<Actor>> getAllActors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false) Integer size) {
        
        if (page < 0 || (size != null && size <= 0)) {
            throw new InvalidPaginationException("Invalid pagination parameters: page must be non-negative and size must be positive.");
        }

        Pageable pageable = (size != null) ? PageRequest.of(page, size) : Pageable.unpaged();

        if (name != null) {
            Page<Actor> actors = actorService.getActorsByName(name, pageable);
            return ResponseEntity.ok(actors.getContent());
        } else {
            Page<Actor> actors = actorService.getAllActors(pageable);
            return ResponseEntity.ok(actors.getContent());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Actor> getActorById(@PathVariable Long id) {
        Actor actor = actorService.getActorById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Actor with id " + id + " not found"));
        return ResponseEntity.ok(actor);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Actor> updateActor(@PathVariable Long id, @Valid @RequestBody Actor updatedActor) {
        Optional<Actor> actor = actorService.updateActor(id, updatedActor);
        
        return actor.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteActor(@PathVariable Long id, 
                                            @RequestParam(defaultValue = "false") boolean force) {
        try {
            actorService.deleteActor(id, force);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{actorId}/movies")
    public ResponseEntity<List<Movie>> getMoviesByActor(@PathVariable Long actorId, Pageable pageable) {
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new InvalidPaginationException("Invalid pagination parameters: page must be non-negative and size must be positive.");
        }
        
        Page<Movie> movies = actorService.getMoviesByActor(actorId, pageable);
        return ResponseEntity.ok(movies.getContent());
    }
}
