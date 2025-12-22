package dk.datamuseum.mobilereg.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Global exception handler.
 *
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/HttpStatus.html">HttpStatus</a>.
 */
@Slf4j
@ControllerAdvice
class GlobalExceptionHandler {

    /**
     * 400 Bad Request.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        log.info("{}: {}", HttpStatus.BAD_REQUEST, ex.getMessage());
        model.addAttribute("httpStatus", HttpStatus.BAD_REQUEST);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    /**
     * 403 Forbidden.
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        log.info("{}: {}", HttpStatus.FORBIDDEN, ex.getMessage());
        model.addAttribute("httpStatus", HttpStatus.FORBIDDEN);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    /**
     * 404 Not Found.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public String handleResourceNotFound(NotFoundException ex, Model model) {
        log.info("{}: {}", HttpStatus.NOT_FOUND, ex.getMessage());
        model.addAttribute("httpStatus", HttpStatus.NOT_FOUND);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    /**
     * 500 Internal Server Error.
     * For any other exception not caught.
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        log.error("{}: {}", HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        model.addAttribute("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR);
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

}
