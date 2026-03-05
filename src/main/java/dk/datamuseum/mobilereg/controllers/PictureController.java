package dk.datamuseum.mobilereg.controllers;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.entities.Picture;
import dk.datamuseum.mobilereg.repositories.ItemRepository;
import dk.datamuseum.mobilereg.repositories.PictureRepository;
import dk.datamuseum.mobilereg.service.PictureService;
import dk.datamuseum.mobilereg.service.Utilities;

/**
 * Controller for pictures.
 */
@Slf4j
@Controller
@RequestMapping("/pictures")
public class PictureController {

    private final ItemRepository itemRepository;

    private final PictureRepository pictureRepository;

    private final PictureService pictureService;

    private final Utilities utilities;

    /**
     * Constructor.
     */
    public PictureController(
                ItemRepository itemRepository,
                PictureRepository pictureRepository,
                PictureService pictureService,
                Utilities utilities) {
        this.itemRepository = itemRepository;
        this.pictureRepository = pictureRepository;
        this.pictureService = pictureService;
        this.utilities = utilities;
    }

    /*
    @RequestMapping({"", "/", "/view"})
    public String showPictureList(Model model) {
        model.addAttribute("pictures", pictureRepository.findByOrderByPicturenavn());
        return "pictures";
    }
    */

    @PreAuthorize("hasAuthority('VIEW_ITEMS')")
    @GetMapping("/view/{id}")
    public String showFactsheet(@PathVariable("id") int id, Model model) throws NotFoundException {
        Picture picture = pictureRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Invalid picture Id:" + id));
        model.addAttribute("picture", picture);

        return "pictures-view";
    }

    /**
     * Delete picture.
     * This also removes the physical files in the picture service.
     */
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    @GetMapping("/delete/{id}")
    public String deletePicture(@PathVariable("id") int id, Model model) {
        Picture picture = pictureRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("Invalid picture Id:" + id));
        Integer returnItem = picture.getItemid();
        pictureRepository.delete(picture);
        log.info("Deleted picture Id {} for item Id {}", id, returnItem);

        String filename = picture.getOriginal();
        filename = filename.substring(filename.lastIndexOf('/') + 1);
        log.debug("Deleted filename: {}", filename);
        pictureService.delete(filename);

        return String.format("redirect:/pictures/view/%d", returnItem);
    }

    /**
     * Show the form for moving a picture to another container.
     * This shows the three ways to move as a menu.
     *
     * @param pictureid picture id.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template.
     */
    @GetMapping("/move")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String showMoveForm(int pictureid,
                Model model) throws NotFoundException {
        Picture picture = pictureRepository.findById(pictureid).orElseThrow(()
                -> new NotFoundException("Invalid picture Id:" + pictureid));
        model.addAttribute("places", itemRepository.findByPlacementidNull());
        model.addAttribute("title", picture.getTitle());
        model.addAttribute("pictureid", pictureid);

        return "pictures-move";
    }

    /**
     * Show the navigation for moving a picture.
     * This shows the three ways to move as a menu.
     *
     * @param pictureid picture id to move.
     * @param placementid - current selected place - must be an item id.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template.
     */
    @GetMapping("/move-nav")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String showMoveNav(int pictureid, Integer placementid, Model model) {
        Picture picture = pictureRepository.findById(pictureid).orElseThrow(()
                -> new IllegalArgumentException("Invalid picture Id:" + pictureid));
        int level = Integer.MAX_VALUE;
        if (placementid != null) {
            Item currPlaceItem = itemRepository.findById(placementid).orElseThrow(()
                -> new IllegalArgumentException("Invalid placement Id:" + placementid));
            model.addAttribute("placename", currPlaceItem.getHeadline());
            model.addAttribute("places", itemRepository.findContainers(placementid, level));
            model.addAttribute("placementid", placementid);
        } else {
            model.addAttribute("places", itemRepository.findByPlacementidNull());
        }
        model.addAttribute("level", level);
        model.addAttribute("title", picture.getTitle());
        model.addAttribute("pictureid", pictureid);

        return "pictures-move-nav";
    }

    /**
     * Show the hits from searching a container by pictureid, QR code, or tekst.
     *
     * @param pictureid picture id to move.
     * @param query - query string.
     * @param model - Additional attributes used by the web form.
     * @param page - page number of paged results.
     * @param size - number of results on a single page.
     * @return name of Thymeleaf template.
     */
    @GetMapping("/move-search")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String showMoveSearch(
            int pictureid,
            @RequestParam(name = "q", required=false) String query,
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {

        if (query == null) {
            query = "";
        }
        model.addAttribute("q", query);
        List<Item> items = new ArrayList<Item>();
        if (Utilities.isNumeric(query)) {
            Integer cleanId = Integer.parseInt(query);
            items = itemRepository.findByIdOrQrcode(cleanId, cleanId);
            model.addAttribute("currentPage", 1);
            model.addAttribute("totalItems", items.size());
            model.addAttribute("totalPages", 1);
        } else {
            Pageable paging = PageRequest.of(page - 1, size);
            Page<Item> pagedItems =  itemRepository.findByFulltextContaining(query, paging);
            model.addAttribute("currentPage", pagedItems.getNumber() + 1);
            model.addAttribute("totalItems", pagedItems.getTotalElements());
            model.addAttribute("totalPages", pagedItems.getTotalPages());
            items = pagedItems.getContent();
        }
        model.addAttribute("items", items);
        model.addAttribute("pictureid", pictureid);
        model.addAttribute("pageSize", size);
        return "pictures-move-search";
    }

    /**
     * Show the form for scanning QR to move.
     *
     * @param pictureid picture id to move.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template.
     */
    @GetMapping("/qrmove")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String showQRMoveForm(int pictureid, Model model) {
        if (!pictureRepository.existsById(pictureid)) {
            throw new IllegalArgumentException("Invalid picture Id: " + pictureid);
        }
        model.addAttribute("pictureid", pictureid);

        return "pictures-move-qr";
    }

    /**
     * Update the location of the item.
     *
     * @param pictureid picture id.
     * @param placementid - the item id of the new location.
     * @param model - Additional attributes used by the web form.
     * @return redirection to factsheet of created item.
     */
    @PostMapping("/updateplace/{pictureid}")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String moveItem(@PathVariable("pictureid") int pictureid,
                Integer placementid,
                Model model) {
        Item parentItem = itemRepository.findById(placementid).orElseThrow(()
                -> new IllegalArgumentException("Invalid new item Id for: " + pictureid));
        return moveUpdateDB(pictureid, parentItem);
    }

    /**
     * Update the location of the item from QR scan.
     *
     * @param pictureid picture id.
     * @param placementid - the new location's QR code - this can be a URL.
     * @param model - Additional attributes used by the web form.
     * @return redirection to factsheet of created item.
     */
    @PostMapping("/qrupdateplace/{pictureid}")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String moveQRItem(@PathVariable("pictureid") int pictureid,
                String placementid,
                Model model) {
        Integer cleanQR = utilities.evaluateQRString(placementid);
        Item parentItem = itemRepository.getByQrcode(cleanQR).orElseThrow(()
                -> new IllegalArgumentException("QR-koden er ikke registreret:" + cleanQR));
        return moveUpdateDB(pictureid, parentItem);
    }

    private String moveUpdateDB(int pictureid, Item parentItem) {
        Picture pictureInDB = pictureRepository.findById(pictureid).orElseThrow(()
                -> new IllegalArgumentException("Invalid picture Id:" + pictureid));
        pictureInDB.setItemid(parentItem.getId());
        int itemid = pictureInDB.getItemid();
        log.info("Moving picture {} to item {}", pictureInDB.getPictureid(), itemid);
        pictureRepository.save(pictureInDB);
        return String.format("redirect:/items/view/%d", itemid);
    }


}
