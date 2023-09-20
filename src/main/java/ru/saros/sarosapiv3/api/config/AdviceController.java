package ru.saros.sarosapiv3.api.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.saros.sarosapiv3.api.exception.ObjectAlreadyExistsException;
import ru.saros.sarosapiv3.api.exception.ObjectNotFoundException;
import ru.saros.sarosapiv3.api.exception.PasswordException;

@ControllerAdvice
public class AdviceController {

    @ExceptionHandler(value = {ObjectNotFoundException.class})
    public ResponseEntity<Object> handleNotFound(ObjectNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ObjectAlreadyExistsException.class})
    public ResponseEntity<Object> handleAlreadyExists(ObjectAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {PasswordException.class})
    public ResponseEntity<Object> handlePasswordException(PasswordException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }
}
