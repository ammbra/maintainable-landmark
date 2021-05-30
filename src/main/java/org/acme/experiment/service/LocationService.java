package org.acme.experiment.service;

import org.acme.experiment.exception.LocationNotFoundException;
import org.acme.experiment.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class LocationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationService.class);

    @Inject
    LocationRepository repository;

    public List<Location> getAll() {
        return repository.listAll();
    }

    @Transactional
    public List<Location> findByAddress(String address) {
        return repository.findByAddress(address);
    }

    @Transactional
    public Location findByName(String name) {
        Location location;
        try {
            location = repository.findByName(name);
        } catch (LocationNotFoundException e) {
            LOGGER.debug("Location not found for the given name", e);
            location = new Location();
            location.setMessage(e.getMessage());
        }
        return location;
    }

    @Transactional
    public void create(Location location) {
        repository.persist(location);
    }

    @Transactional
    public long deleteById(UUID id) {
        return repository.delete("id=?1", id);
    }
}


