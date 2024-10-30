package com.example.movies_api.services;

import com.example.movies_api.entities.Actor;
import com.example.movies_api.entities.Movie;
import com.example.movies_api.repositories.ActorRepository;
import com.example.movies_api.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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

    // Retrieve all actors
    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

    // Retrieve a specific actor by ID
    public Optional<Actor> getActorById(Long id) {
        return actorRepository.findById(id);
    }

    // Filter actors by name
    public List<Actor> getActorsByName(String name) {
        return actorRepository.findAll().stream()
            .filter(actor -> actor.getName().toLowerCase().contains(name.toLowerCase()))
            .toList();
    }

    // Fetch all movies an actor has appeared in
    public Set<Movie> getMoviesByActor(Long actorId) {
        return actorRepository.findById(actorId)
            .map(Actor::getMovies)
            .orElseGet(Set::of);
    }

    // Update actor details (PATCH method)
    public Optional<Actor> updateActor(Long id, Actor updatedActor) {
        Optional<Actor> existingActorOptional = actorRepository.findById(id);
        if (existingActorOptional.isPresent()) {
            Actor existingActor = existingActorOptional.get();
            
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
        return Optional.empty();
    }

    // Remove an actor from the database
    public void deleteActor(Long id) {
        actorRepository.deleteById(id);
    }
}
