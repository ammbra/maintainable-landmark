package org.acme.experiment.exception;

public class LocationNotFoundException extends Throwable {
    public LocationNotFoundException(String msg) {
        super(msg);
    }
}