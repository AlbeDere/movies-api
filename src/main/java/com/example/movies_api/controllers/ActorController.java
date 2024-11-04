package com.example.movies_api.controllers;

import com.example.movies_api.entities.Actor;
import com.example.movies_api.entities.Movie;
import com.example.movies_api.services.ActorService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // Create a new actor
    @PostMapping
    public ResponseEntity<Actor> createActor(@Valid @RequestBody Actor actor) {
        Actor createdActor = actorService.createActor(actor);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdActor); // HTTP 201 Created
    }

    // Get all actors with pagination
    @GetMapping
    public ResponseEntity<List<Actor>> getAllActors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Actor> actorPage = actorService.getAllActors(pageable);
        return ResponseEntity.ok(actorPage.getContent()); // Returns only the content of the paginated actors
    }

    // Retrieve a specific actor by ID
    @GetMapping("/{id}")
    public ResponseEntity<Actor> getActorById(@PathVariable Long id) {
        Actor actor = actorService.getActorById(id); // Throws ResourceNotFoundException if not found
        return ResponseEntity.ok(actor); // Directly return the actor
    }

    // Update actor details
// Update actor details
    @PatchMapping("/{id}")
    public ResponseEntity<Actor> updateActor(@PathVariable Long id, @Valid @RequestBody Actor updatedActor) {
        // Attempt to update the actor
        Optional<Actor> actor = actorService.updateActor(id, updatedActor);
        
        // Return updated actor with HTTP 200 OK if found, otherwise return HTTP 404 Not Found
        return actor.map(ResponseEntity::ok) // HTTP 200 OK
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()); // HTTP 404 Not Found
    }


    // Delete an actor
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteActor(@PathVariable Long id, 
                                            @RequestParam(defaultValue = "false") boolean force) {
        try {
            actorService.deleteActor(id, force);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content for successful deletion
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // HTTP 400 Bad Request for relationship conflict
        }
    }

    // Get all movies an actor has appeared in
    @GetMapping("/{actorId}/movies")
    public ResponseEntity<List<Movie>> getMoviesByActor(@PathVariable Long actorId) {
        Set<Movie> movies = actorService.getMoviesByActor(actorId); // Throws ResourceNotFoundException if not found
        List<Movie> movieList = new ArrayList<>(movies);
        return ResponseEntity.ok(movieList);
    }
}
