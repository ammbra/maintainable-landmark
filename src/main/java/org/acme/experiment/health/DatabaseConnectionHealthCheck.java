package org.acme.experiment.health;

import io.agroal.api.AgroalDataSource;
import lombok.SneakyThrows;
import org.acme.experiment.service.LocationRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Readiness
@ApplicationScoped
public class DatabaseConnectionHealthCheck implements HealthCheck {

    private boolean databaseUp;

    @Inject
    AgroalDataSource defaultDataSource;

    @Override
    public HealthCheckResponse call() {

        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Database connection health check");

        try {
            simulateDatabaseConnectionVerification();
            responseBuilder.up();
        } catch (IllegalStateException e) {
            // cannot access the database
            responseBuilder.down();
        }

        return responseBuilder.build();
    }

    @SneakyThrows
    private void simulateDatabaseConnectionVerification() {
        databaseUp = defaultDataSource.getConnection() != null;
        if (!databaseUp) {
            throw new IllegalStateException("Cannot contact database");
        }
    }
}