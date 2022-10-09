package com.mumomu.exquizme.production.exception;

public class HostNotFoundException extends NullPointerException {
    public HostNotFoundException(String message) {
        super(message);
    }
}
