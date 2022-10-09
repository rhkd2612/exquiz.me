package com.mumomu.exquizme.production.exception;

public class ProblemOptionNotFoundException extends NullPointerException {
    public ProblemOptionNotFoundException(String message) {
        super(message);
    }
}
