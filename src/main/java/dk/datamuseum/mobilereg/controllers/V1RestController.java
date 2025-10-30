package dk.datamuseum.mobilereg.controllers;


import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import dk.datamuseum.mobilereg.entities.Donor;
import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.entities.Picture;
import dk.datamuseum.mobilereg.repositories.DonorRepository;
import dk.datamuseum.mobilereg.repositories.ItemRepository;
import dk.datamuseum.mobilereg.repositories.PictureRepository;

/**
 * Controller for version 1 of the API.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1alpha")
public class V1RestController {


    private final DonorRepository donorRepository;

    private final ItemRepository itemRepository;

    private final PictureRepository pictureRepository;

    /**
     * Constructor.
     */
    public V1RestController(
            DonorRepository donorRepository,
            ItemRepository itemRepository,
            PictureRepository pictureRepository) {
        this.donorRepository = donorRepository;
        this.itemRepository = itemRepository;
        this.pictureRepository = pictureRepository;
    }

    @GetMapping({"", "/"})
    public RepresentationModel<?> rootlevel() {
        RepresentationModel<?> model = new RepresentationModel<>();
        model.add(linkTo(methodOn(V1RestController.class).showDonorList()).withRel("donators"));
        model.add(linkTo(methodOn(V1RestController.class).showLast50Items()).withRel("last50items"));
        model.add(linkTo(methodOn(V1RestController.class).getPictureList()).withRel("pictures"));
        return model;
    }

    /**
     * List donors.
     *
     * @return list
     */
    //@PreAuthorize("hasAuthority('VIEW_DONATORS')")
    @GetMapping(value = "/donators") //, produces = "application/json")
    public Iterable<Donor> showDonorList() {
        return donorRepository.findByOrderByName();
    }

    @GetMapping("/pictures/{id}")
    EntityModel<Picture> onePicture(@PathVariable Integer id) {
        Picture employee = pictureRepository.findById(id) //
        .orElseThrow(() -> new NotFoundException(id));

        return EntityModel.of(employee, //
            linkTo(methodOn(V1RestController.class).onePicture(id)).withSelfRel(),
            linkTo(methodOn(V1RestController.class).getPictureList()).withRel("pictures"));
    }

    /**
     * List pictures.
     *
     * @return list
     */
    @GetMapping(value = "/pictures")
    public CollectionModel<EntityModel<Picture>> getPictureList() {
        List<EntityModel<Picture>> pictures = pictureRepository.findAll().stream()
            .map(picture -> EntityModel.of(picture,
            linkTo(methodOn(FileOpsController.class).downloadFile("pictureoriginal",
                picture.getFilename())).withRel("download"),
            linkTo(methodOn(V1RestController.class).onePicture(picture.getPictureid())).withSelfRel(),
            linkTo(methodOn(V1RestController.class).getPictureList()).withRel("pictures")))
                .collect(Collectors.toList());

        return CollectionModel.of(pictures, linkTo(methodOn(V1RestController.class).getPictureList()).withSelfRel());
    }

    /**
     * List last 50 items based on last change date.
     *
     * @return list
     */
    @GetMapping(value = "/items")
    public Iterable<Item> showLast50Items() {
        Iterable<Item> items = itemRepository.findFirst50ByOrderByLastmodifiedDesc();
        log.debug("JSON: {}", items);
        return items;
    }
}
