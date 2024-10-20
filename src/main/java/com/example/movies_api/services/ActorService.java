package com.example.movies_api.services;

import com.example.movies_api.entities.Actor;
import com.example.movies_api.repositories.ActorRepository;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Actor getActorById(Long id) {
        return actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found"));
    }

    public List<Actor> getActorsByName(String name) {
        return actorRepository.findByNameContainingIgnoreCase(name);
    }

    public Actor updateActor(Long id, Actor updatedActor) {
        Actor actor = getActorById(id);
        actor.setName(updatedActor.getName());
        actor.setBirthDate(updatedActor.getBirthDate());
        return actorRepository.save(actor);
    }

    public void deleteActor(Long id) {
        actorRepository.deleteById(id);
    }
}
