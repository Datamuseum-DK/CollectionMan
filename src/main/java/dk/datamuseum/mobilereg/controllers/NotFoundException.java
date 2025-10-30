package dk.datamuseum.mobilereg.controllers;

class NotFoundException extends RuntimeException {

  NotFoundException(Integer id) {
    super("Could not find resource " + id);
  }
}
