package dk.datamuseum.mobilereg.controllers;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import dk.datamuseum.mobilereg.entities.Donor;
import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.entities.Picture;
import dk.datamuseum.mobilereg.entities.Producer;
import dk.datamuseum.mobilereg.repositories.DonorRepository;
import dk.datamuseum.mobilereg.repositories.ItemRepository;
import dk.datamuseum.mobilereg.repositories.PictureRepository;
import dk.datamuseum.mobilereg.repositories.ProducerRepository;

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

    private final ProducerRepository producerRepository;

    /**
     * Constructor.
     */
    public V1RestController(
            DonorRepository donorRepository,
            ItemRepository itemRepository,
            PictureRepository pictureRepository,
            ProducerRepository producerRepository) {
        this.donorRepository = donorRepository;
        this.itemRepository = itemRepository;
        this.pictureRepository = pictureRepository;
        this.producerRepository = producerRepository;
    }

    /**
     * Toplevel view.
     */
    @GetMapping({"", "/"})
    public RepresentationModel<?> rootlevel() {
        RepresentationModel<?> model = new RepresentationModel<>();
        model.add(linkTo(methodOn(V1RestController.class).allDonors()).withRel("donors"));
        model.add(linkTo(methodOn(V1RestController.class).last50Items()).withRel("last50items"));
        model.add(linkTo(methodOn(V1RestController.class).allItems()).withRel("items"));
        model.add(linkTo(methodOn(V1RestController.class).allProducers()).withRel("producers"));
        model.add(linkTo(methodOn(V1RestController.class).allPictures()).withRel("pictures"));
        return model;
    }

    /**
     * List donors.
     *
     * @return list
     */
    @PreAuthorize("hasAuthority('VIEW_DONATORS')")
    @GetMapping("/donors")
    public CollectionModel<EntityModel<Donor>> allDonors() {
        List<EntityModel<Donor>> donors = donorRepository.findAll().stream()
            .map(donor -> donorAddlinks(donor))
                .collect(Collectors.toList());
        return CollectionModel.of(donors,
                linkTo(methodOn(V1RestController.class).allDonors()).withSelfRel());
    }

    // public Iterable<Donor> showDonorList() {
    //     return donorRepository.findByOrderByName();
    // }

    /**
     * Return attributes of one donor.
     *
     * @param id - donor id.
     * @return donor with added links in HAL format.
     */
    @PreAuthorize("hasAuthority('VIEW_DONATORS')")
    @GetMapping("/donors/{id}")
    EntityModel<Donor> oneDonor(@PathVariable Integer id) {
        Donor donor = donorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        return donorAddlinks(donor);
    }

    private EntityModel<Donor> donorAddlinks(Donor donor) {
        return EntityModel.of(donor,
            linkTo(methodOn(V1RestController.class).oneDonor(donor.getId())).withSelfRel(),
            linkTo(methodOn(V1RestController.class).allDonors()).withRel("donors"));
    }

    /**
     * List producers.
     *
     * @return list
     */
    @PreAuthorize("hasAuthority('VIEW_PRODUCERS')")
    @GetMapping("/producers")
    public CollectionModel<EntityModel<Producer>> allProducers() {
        List<EntityModel<Producer>> producers = producerRepository.findAll().stream()
            .map(producer -> producerAddlinks(producer))
                .collect(Collectors.toList());
        return CollectionModel.of(producers,
                linkTo(methodOn(V1RestController.class).allProducers()).withSelfRel());
    }

    /**
     * Return attributes of one producer.
     *
     * @param id - producer id.
     * @return producer with added links in HAL format.
     */
    @PreAuthorize("hasAuthority('VIEW_PRODUCERS')")
    @GetMapping("/producers/{id}")
    EntityModel<Producer> oneProducer(@PathVariable Integer id) {
        Producer producer = producerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        return producerAddlinks(producer);
    }

    private EntityModel<Producer> producerAddlinks(Producer producer) {
        return EntityModel.of(producer,
            linkTo(methodOn(V1RestController.class).oneProducer(producer.getProducerid())).withSelfRel(),
            linkTo(methodOn(V1RestController.class).allProducers()).withRel("producers"));
    }

    /**
     * Return attributes of one picture.
     *
     * @param id - picture id.
     * @return picture with added links in HAL format.
     */
    @PreAuthorize("hasAuthority('VIEW_PICTURES')")
    @GetMapping("/pictures/{id}")
    EntityModel<Picture> onePicture(@PathVariable Integer id) {
        Picture picture = pictureRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        return pictureAddlinks(picture);
    }

    /**
     * List pictures.
     *
     * @return list
     */
    @PreAuthorize("hasAuthority('VIEW_PICTURES')")
    @GetMapping("/pictures")
    public CollectionModel<EntityModel<Picture>> allPictures() {
        List<EntityModel<Picture>> pictures = pictureRepository.findAll().stream()
            .map(picture -> pictureAddlinks(picture))
                .collect(Collectors.toList());
        return CollectionModel.of(pictures,
                linkTo(methodOn(V1RestController.class).allPictures()).withSelfRel());
    }

    private EntityModel<Picture> pictureAddlinks(Picture picture) {
        return EntityModel.of(picture,
            linkTo(methodOn(FileOpsController.class).downloadFile("pictureoriginal",
                picture.getFilename())).withRel("download"),
            linkTo(methodOn(V1RestController.class).onePicture(picture.getPictureid())).withSelfRel(),
            linkTo(methodOn(V1RestController.class).allPictures()).withRel("pictures"),
            linkTo(methodOn(V1RestController.class).oneItem(picture.getItemid())).withRel("item")
            );
    }

    /**
     * List last 50 items based on last change date.
     *
     * @return list
     */
    @PreAuthorize("hasAuthority('VIEW_ITEMS')")
    @GetMapping("/last50items")
    public Iterable<Item> last50Items() {
        Iterable<Item> items = itemRepository.findFirst50ByOrderByLastmodifiedDesc();
        log.debug("JSON: {}", items);
        return items;
    }

    /**
     * List all items.
     *
     * @return list
     */
    @PreAuthorize("hasAuthority('VIEW_ITEMS')")
    @GetMapping("/items")
    public CollectionModel<EntityModel<Item>> allItems() {
        List<EntityModel<Item>> items = itemRepository.findAll().stream()
            .map(item -> itemAddlinks(item)).collect(Collectors.toList());

        return CollectionModel.of(items,
                linkTo(methodOn(V1RestController.class).allItems()).withSelfRel());
    }

    @PreAuthorize("hasAuthority('VIEW_ITEMS')")
    @GetMapping("/items/{id}")
    EntityModel<Item> oneItem(@PathVariable Integer id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));

        return itemAddlinks(item);
    }

    private EntityModel<Item> itemAddlinks(Item item) {
        return EntityModel.of(item, 
            linkTo(methodOn(V1RestController.class).oneItem(item.getId())).withSelfRel(),
            linkTo(methodOn(V1RestController.class).oneProducer(item.getProducerid())).withRel("producer"),
            linkTo(methodOn(V1RestController.class).oneDonor(item.getDonorid())).withRel("donor"),
            linkTo(methodOn(V1RestController.class).allItems()).withRel("items"));
    }

}

    /*
    public Iterable<Item> allItems() {
        Iterable<Item> items = itemRepository.findAll();
        return items;
    }
    */
