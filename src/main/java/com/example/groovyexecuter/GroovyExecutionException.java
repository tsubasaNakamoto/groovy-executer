package com.example.groovyexecuter;

public class GroovyExecutionException extends RuntimeException {
    public GroovyExecutionException(String message) {
        super(message);
    }

    public GroovyExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
