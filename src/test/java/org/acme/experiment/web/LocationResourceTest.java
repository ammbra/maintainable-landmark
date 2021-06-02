package org.acme.experiment.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.quarkus.cache.CacheManager;
import io.quarkus.cache.runtime.caffeine.CaffeineCache;
import io.quarkus.test.junit.QuarkusTest;
import lombok.SneakyThrows;
import org.acme.experiment.model.Location;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class LocationResourceTest {

    private final SimpleMeterRegistry registry = new SimpleMeterRegistry();
    @Inject
    CacheManager cacheManager;

    @Test
    public void shouldReturnAllLocations() {
        given()
          .when().get("/api/location/all")
          .then()
             .statusCode(200)
             .body(notNullValue());
    }

    @Test
    public void shouldReturnAllLocationsByPartialAddress() {
        given()
                .when().get("/api/location/Museum")
                .then()
                .statusCode(200)
                .body("$.size()", is(2));
    }

    @Test
    public void shouldReturnLocationByName() {
        given()
                .when().get("/api/location?name=Hermitage%20Amsterdam")
                .then()
                .statusCode(200)
                .body(notNullValue());
    }

    @SneakyThrows
    @Test
    public void shouldCreateLocation() {
        Location location = new Location();
        location.setAddress("Sample Address");
        location.setCity("Sample City");
        location.setDiscount(20);
        location.setCountry("A country");
        location.setPrice(100D);
        location.setName("A location");
        location.setRating(0);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(location);
        given().header("Content-type", "application/json")
                .and().body(json)
                .when().post("/api/location")
                .then().statusCode(204);
    }

    @Test
    public void shouldDeleteLocation() {
        given().when().delete("/api/location/"+UUID.randomUUID())
                .then().statusCode(200);
    }

    @Test
    void shouldMeasureCachedLocationsByName() {
        //given
        CaffeineCache cache = (CaffeineCache) cacheManager.getCache("location-by-name").get();
        registry.gauge("cache.size", cache, value -> cache.getSize());

        //when
        for (int i = 0; i < 5; i++) {
            given()
                    .when().get("/api/location?name=Hermitage%20Amsterdam")
                    .then().statusCode(200).body(notNullValue());
            given()
                    .when().get("/api/location?name=Hermitage")
                    .then().statusCode(200).body(notNullValue());
            given()
                    .when().get("/api/location?name=National")
                    .then().statusCode(200).body(notNullValue());
        }

        //then
        Meter gauge = registry.find("cache.size").meter();
        List<Measurement> measurements = new ArrayList<>();
        gauge.measure().iterator().forEachRemaining(measurements::add);
        assertEquals(3, measurements.get(0).getValue());
    }
}