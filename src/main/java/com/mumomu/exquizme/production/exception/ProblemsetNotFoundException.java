package com.mumomu.exquizme.production.exception;

public class ProblemsetNotFoundException extends NullPointerException {
    public ProblemsetNotFoundException(String message) {
        super(message);
    }
}
