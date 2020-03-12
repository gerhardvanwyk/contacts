package org.wyk.contact.exception;

public class InvalidIdException extends RuntimeException {
    public InvalidIdException(String error) {
        super(error);
    }
}
