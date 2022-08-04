package com.mumomu.exquizme.production.exception;

public class ProblemNotFoundException extends NullPointerException {
    public ProblemNotFoundException(String message) {
        super(message);
    }
}
