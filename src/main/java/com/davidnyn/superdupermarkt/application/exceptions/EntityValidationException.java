package com.davidnyn.superdupermarkt.application.exceptions;

public class EntityValidationException extends Exception {

    public EntityValidationException(String errorMessage)
    {
        super(errorMessage);
    }
}
