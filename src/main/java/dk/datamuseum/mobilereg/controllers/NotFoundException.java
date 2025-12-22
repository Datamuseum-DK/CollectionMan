package dk.datamuseum.mobilereg.controllers;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundException extends RuntimeException {

    /*
    NotFoundException(Integer id) {
        super("Could not find resource " + id);
    }
    */

    NotFoundException(String message) {
        super(message);
    }
}
