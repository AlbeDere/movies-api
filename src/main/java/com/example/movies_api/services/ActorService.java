package com.example.movies_api.services;

import com.example.movies_api.entities.Actor;
import com.example.movies_api.entities.Movie;
import com.example.movies_api.repositories.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ActorService {

    @Autowired
    private ActorRepository actorRepository;

    public Actor createActor(Actor actor) {
        return actorRepository.save(actor);
    }

    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

    public Optional<Actor> getActorById(Long id) {
        return actorRepository.findById(id);
    }

    public List<Actor> getActorsByName(String name) {
        return actorRepository.findByNameContaining(name);
    }

    public Actor updateActor(Long id, Actor actor) {
        actor.setId(id);
        return actorRepository.save(actor);
    }

    public void deleteActor(Long id) {
        actorRepository.deleteById(id);
    }

    public List<Movie> getMoviesByActor(Long actorId) {
        Actor actor = actorRepository.findById(actorId).orElse(null);
        return actor != null ? new ArrayList<>(actor.getMovies()) : null;
    }
}
