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

    public Actor createActor(Actor actor) {
        return actorRepository.save(actor);
    }

    public Page<Actor> getAllActors(Pageable pageable) {
        return actorRepository.findAll(pageable);
    }

    public Optional<Actor> getActorById(Long id) {
        return actorRepository.findById(id);
    }

    public Page<Actor> getActorsByName(String name, Pageable pageable) {
        Page<Actor> matchingActors = actorRepository.findByNameContainingIgnoreCase(name, pageable);

        if (matchingActors.isEmpty()) {
            throw new ResourceNotFoundException("No actors found with name containing: " + name);
        }

        return matchingActors;
    }

    public Page<Movie> getMoviesByActor(Long actorId, Pageable pageable) {
        Optional<Actor> actor = actorRepository.findById(actorId);
        if (actor.isEmpty()) {
            throw new ResourceNotFoundException("Actor with id " + actorId + " not found");
        }

        return movieRepository.findByActors_Id(actorId, pageable);
    }

    public Optional<Actor> updateActor(Long id, Actor updatedActor) {
        Optional<Actor> existingActor = actorRepository.findById(id);
        if (existingActor.isEmpty()) {
            throw new ResourceNotFoundException("Actor with id " + id + " not found");
        }

        Actor actorToUpdate = existingActor.get();

        if (updatedActor.getName() != null) {
            actorToUpdate.setName(updatedActor.getName());
        }
        if (updatedActor.getBirthDate() != null) {
            actorToUpdate.setBirthDate(updatedActor.getBirthDate());
        }
        if (updatedActor.getMovies() != null) {
            actorToUpdate.setMovies(updatedActor.getMovies());
        }

        return Optional.of(actorRepository.save(actorToUpdate));
    }

    public void deleteActor(Long id, boolean force) {
        Optional<Actor> actor = actorRepository.findById(id);
        if (actor.isEmpty()) {
            throw new ResourceNotFoundException("Actor with id " + id + " not found");
        }

        Set<Movie> associatedActor = actor.get().getMovies();

        if (!force && !associatedActor.isEmpty()) {
            throw new IllegalStateException("Cannot delete actor '" + actor.get().getName() + 
                                            "' because it has " + associatedActor.size() + " associated movies.");
        }

        if (force) {
            for (Movie movie : associatedActor) {
                movie.getActors().remove(actor.get());
                movieRepository.save(movie);
            }
        }
        actorRepository.deleteById(id);
    }
}
