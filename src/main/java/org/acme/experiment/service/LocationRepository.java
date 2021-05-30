package org.acme.experiment.service;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.acme.experiment.exception.LocationNotFoundException;
import org.acme.experiment.model.Location;

import javax.inject.Singleton;
import javax.transaction.Transactional;
import java.util.List;

@Singleton
public class LocationRepository implements PanacheRepository<Location> {

    public List<Location> findByAddress(String address) {
        return find("address like ?1",  "%"+address+"%").list();
    }

    public Location findByName(String name) throws LocationNotFoundException {
        return find("name=?1", name).stream().findFirst().orElseThrow(() -> new LocationNotFoundException("No location is associated to this name " + name));
    }
}