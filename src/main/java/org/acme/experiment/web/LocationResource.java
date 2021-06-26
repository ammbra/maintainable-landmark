package org.acme.experiment.web;

import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import org.acme.experiment.model.Location;
import org.acme.experiment.service.LocationService;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Path("/api/location")
@GraphQLApi
public class LocationResource {
    @Inject
    LocationService locationService;

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @Query("findAllLocations")
    public List<Location> findAll() {
        return this.locationService.getAll();
    }

    @GET
    @Path("/async/{address}")
    @Produces(MediaType.APPLICATION_JSON)
    @Query("asyncFindLocationsByAddress")
    public CompletionStage<List<Location>> asyncFindByPartialAddress(@PathParam("address") String address) {
        return CompletableFuture.supplyAsync(() -> locationService.findByAddress(address));
    }

    @GET
    @Path("{address}")
    @Produces(MediaType.APPLICATION_JSON)
    @Query("findAllLocationsByAddress")
    public List<Location> findByPartialAddress(@PathParam("address") String address) {
        return locationService.findByAddress(address);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @CacheResult(cacheName = "location-by-name")
    @Query("findAllLocationsByName")
    public CompletionStage<Location> findByName(@CacheKey@QueryParam("name") String name) {
        return CompletableFuture.supplyAsync(() -> locationService.findByName(name));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void create(Location location) {
        locationService.create(location);
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Query("deleteLocationById")
    public Long deleteById(@PathParam("id") UUID id) {
        return locationService.deleteById(id);
    }

}