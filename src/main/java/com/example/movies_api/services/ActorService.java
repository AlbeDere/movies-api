package com.example.movies_api.services;

import com.example.movies_api.entities.Actor;
import com.example.movies_api.entities.Movie;
import com.example.movies_api.exceptions.ResourceNotFoundException;
import com.example.movies_api.repositories.ActorRepository;
import com.example.movies_api.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ActorService {

    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;

    @Autowired
    public ActorService(ActorRepository actorRepository, MovieRepository movieRepository) {
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
    }

    // Create a new actor
    public Actor createActor(Actor actor) {
        return actorRepository.save(actor);
    }

    // Retrieve all actors with pagination
    public Page<Actor> getAllActors(Pageable pageable) {
        return actorRepository.findAll(pageable);
    }

    // Retrieve a specific actor by ID
    public Actor getActorById(Long id) {
        return actorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Actor with id " + id + " not found"));
    }

    // Filter actors by name
    public List<Actor> getActorsByName(String name) {
        List<Actor> matchingActors = actorRepository.findAll().stream()
            .filter(actor -> actor.getName().toLowerCase().contains(name.toLowerCase()))
            .toList();

        if (matchingActors.isEmpty()) {
            throw new ResourceNotFoundException("No actors found with name containing: " + name);
        }
        
        return matchingActors;
    }

    // Fetch all movies an actor has appeared in
    public Set<Movie> getMoviesByActor(Long actorId) {
        Actor actor = actorRepository.findById(actorId)
            .orElseThrow(() -> new ResourceNotFoundException("Actor with id " + actorId + " not found"));
        
        return actor.getMovies();
    }

    // Update actor details (PATCH method)
    public Optional<Actor> updateActor(Long id, Actor updatedActor) {
        Actor existingActor = actorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Actor with id " + id + " not found"));

        // Only update fields that are not null in updatedActor
        if (updatedActor.getName() != null) {
            existingActor.setName(updatedActor.getName());
        }
        if (updatedActor.getBirthDate() != null) {
            existingActor.setBirthDate(updatedActor.getBirthDate());
        }
        // Update associated movies if any
        if (updatedActor.getMovies() != null) {
            existingActor.setMovies(updatedActor.getMovies());
        }

        return Optional.of(actorRepository.save(existingActor));
    }

    // Remove an actor from the database
    public void deleteActor(Long id, boolean force) {
        Actor actor = actorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Actor with id " + id + " not found"));

        // Check for associated movies
        List<Movie> associatedActor = actor.getMovies().stream().toList();

        // If force is false and there are associated movies, prevent deletion
        if (!force && !associatedActor.isEmpty()) {
            throw new IllegalStateException("Cannot delete actor '" + actor.getName() + 
                                            "' because it has " + associatedActor.size() + " associated movies.");
        }

        if (force) {
            for (Movie movie : associatedActor) {
                movie.getActors().remove(actor);  // Remove actor from movie's actor list
                movieRepository.save(movie);      // Update movie in the database
            }
        }
        actorRepository.deleteById(id);
    }
}
