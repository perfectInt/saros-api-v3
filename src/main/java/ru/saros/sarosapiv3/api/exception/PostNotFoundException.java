package ru.saros.sarosapiv3.api.exception;

public class PostNotFoundException extends ObjectNotFoundException {

    public PostNotFoundException() {
        super();
    }

    public PostNotFoundException(String message) {
        super(message);
    }
}
