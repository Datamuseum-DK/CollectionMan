package dk.datamuseum.mobilereg.controllers;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import dk.datamuseum.mobilereg.MobileRegProperties;

import dk.datamuseum.mobilereg.entities.Donor;
import dk.datamuseum.mobilereg.entities.CaseFile;
import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.entities.ItemClass;
import dk.datamuseum.mobilereg.entities.ItemPicture;
import dk.datamuseum.mobilereg.entities.Picture;
import dk.datamuseum.mobilereg.entities.Producer;
import dk.datamuseum.mobilereg.entities.Sted;
import dk.datamuseum.mobilereg.entities.Subject;

import dk.datamuseum.mobilereg.repositories.FileRepository;
import dk.datamuseum.mobilereg.repositories.DonorRepository;
import dk.datamuseum.mobilereg.repositories.ItemRepository;
import dk.datamuseum.mobilereg.repositories.ItemClassRepository;
import dk.datamuseum.mobilereg.repositories.ItemPictureRepository;
import dk.datamuseum.mobilereg.repositories.PictureRepository;
import dk.datamuseum.mobilereg.repositories.ProducerRepository;
import dk.datamuseum.mobilereg.repositories.StedRepository;
import dk.datamuseum.mobilereg.repositories.SubjectRepository;

import dk.datamuseum.mobilereg.service.PictureService;

/**
 * Controller for items.
 */
@Controller
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private DonorRepository donorRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemClassRepository itemClassRepository;
    @Autowired
    private ItemPictureRepository itemPictureRepository;
    @Autowired
    private PictureRepository pictureRepository;
    @Autowired
    private PictureService pictureService;
    @Autowired
    private ProducerRepository producerRepository;
    @Autowired
    private StedRepository stedRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private MobileRegProperties properties;

    private Log logger = LogFactory.getLog(ItemController.class);

    /**
     * List all acquire types.
     * For display on item factsheet.
     */
    @ModelAttribute("acquiretypes")
    public List<Item.Acquired> acquiretypes() {
        return Item.ACQ_OPTIONS;
    }

    /**
     * List all active files to (re)assign to.
     */
    @ModelAttribute("files")
    public Iterable<CaseFile> listActiveFiles() {
        return fileRepository.findByStatusOrderByTitle(true);
    }

    /**
     * List all donors.
     */
    @ModelAttribute("donors")
    public Iterable<Donor> donors() {
        return donorRepository.findByOrderByName();
    }

    /**
     * List all producers.
     */
    @ModelAttribute("producers")
    public Iterable<Producer> producers() {
        return producerRepository.findByOrderByTitle();
    }

    @ModelAttribute("steder")
    public Iterable<Sted> steder() {
        return stedRepository.findByOrderByStednavn();
    }

    @ModelAttribute("allSubjects")
    public Iterable<Subject> allSubjects() {
        return subjectRepository.findByOrderByTitle();
    }

    @ModelAttribute("types")
    public List<ItemClass> itemclasses() {
        return itemClassRepository.findAllOrderByLevelDesc();
    }

    @ModelAttribute("statuses")
    public String[] statuses() {
    String[] itemStatuses = {
          "Godkendt", "Klar", "Kladde", "Udgået", "Intern"
        };
        return itemStatuses;
    }

    /**
     * Show stage 1 in the creation of an item.
     * If placementid is filled out, then set the available types to what's legal.
     */
    @RequestMapping("/addform")
    @PreAuthorize("hasAuthority('ADD_ITEMS')")
    public String showAddForm1(
                Item item,
                Model model) {
        logger.info(String.format("Add form for item %s", item.toString()));
        List<ItemClass> classByLevel = itemclasses();
        if (classByLevel.size() == 0) {
            throw new IllegalArgumentException("No item classes!");
        }
        if (item.getItemClass() == null) {
           item.setItemClass(classByLevel.get(0));
        }
        if (item.getPlacementid() != null) {
            Item parent = itemRepository.findById(item.getPlacementid()).orElseThrow(()
                -> new IllegalArgumentException("Invalid placementid Id:" + item.getPlacementid()));
            ItemClass parentClass = itemClassRepository.findById(parent.getItemClass().getId()).orElseThrow(()
                -> new IllegalArgumentException("Unable to get item class of parent Id:"  + item.getPlacementid()));
            model.addAttribute("locations", itemRepository.findByItemclassLevel(parentClass.getLevel()));
            model.addAttribute("types", itemClassRepository.findByLevelGreaterThan(parentClass.getLevel()));
        } else {
            model.addAttribute("locations", itemRepository.findByItemclassLevel(classByLevel.get(0).getLevel()));
        }
        return "item-addform";
    }

    /**
     * Show stage 2 in the creation of an item.
     * The item is not yet valid.
     */
    @PostMapping("/addform2")
    @PreAuthorize("hasAuthority('ADD_ITEMS')")
    public String showAddForm2(Item item, Model model) {
        return "item-addform2";
    }

    /**
     * Create the item.
     *
     * @param item - The item record containing the entered information.
     * @param result - Results from validation of the web form.
     * @param model - Additional attributes used by the web form.
     * @return redirection to factsheet of created item.
     */
    @PostMapping("/additem")
    @PreAuthorize("hasAuthority('ADD_ITEMS')")
    public String addItem(@Valid Item item, BindingResult result, Model model) {
        logger.debug(String.format("Evaluating item %s", item.toString()));
        if (result.hasErrors()) {
            logger.info(String.format("Result %s", result.toString()));
            return "item-addform";
        }

        itemRepository.save(item);
        return String.format("redirect:/items/view/%d", item.getId());
    }

    /**
     * Form for editing an item.
     *
     * @param id item id.
     */
    @GetMapping("/edit")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String showUpdateForm(int id, Model model) {
        logger.info(String.format("Edit form for %d", id));
        Item item = itemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + id));
        model.addAttribute("item", item);

        return "item-edit";
    }

    /**
     * Show the form for moving an item to another container.
     * This shows the three ways to move as a menu.
     *
     * @param id item id.
     * @param model - Additional attributes used by the web form.
     */
    @GetMapping("/move")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String showMoveForm(int id, Model model) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + id));
        int level = item.getItemClass().getLevel();
        model.addAttribute("level", level);
        model.addAttribute("places", itemRepository.findByPlacementidNull());
        model.addAttribute("headline", item.getHeadline());
        model.addAttribute("itemid", id);

        return "item-move";
    }

    /**
     * Show the navigation for moving a container.
     * This shows the three ways to move as a menu. If placementid is filled out,
     * then it shows a button to select this place and any sub-containers, that
     * have a lower level than the item.
     *
     * @param id item id.
     * @param placementid - current selected place - must be an item id.
     * @param model - Additional attributes used by the web form.
     */
    @GetMapping("/move-nav")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String showMoveNav(int itemid, Integer placementid, Model model) {
        Item item = itemRepository.findById(itemid).orElseThrow(()
                -> new IllegalArgumentException("Invalid item Id:" + itemid));
        int level = item.getItemClass().getLevel();
        if (placementid != null) {
            Item currPlaceItem = itemRepository.findById(placementid).orElseThrow(()
                -> new IllegalArgumentException("Invalid placement Id:" + placementid));
            //int placelevel = currPlaceItem.getItemClass().getLevel();
            model.addAttribute("places", itemRepository.findContainers(placementid, itemclasses().get(0).getLevel()));
            model.addAttribute("placementid", placementid);
        } else {
            model.addAttribute("places", itemRepository.findByPlacementidNull());
        }
        model.addAttribute("level", level);
        model.addAttribute("headline", item.getHeadline());
        model.addAttribute("itemid", itemid);

        return "item-move";
    }

    /**

    /**
     * Update the location of the item.
     *
     * @param id item id.
     * @param placementid - the new location.
     * @param model - Additional attributes used by the web form.
     */
    @PostMapping("/updateplace/{itemid}")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String moveItem(@PathVariable("itemid") int itemid, String placementid, Model model) {
        Item itemInDB = itemRepository.findById(itemid).orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + itemid));
        Integer cleanId = evaluateQRString(placementid);
        itemInDB.setPlacementid(cleanId);
        logger.info(String.format("Moving %d to %d", itemInDB.getId(), itemInDB.getPlacementid()));
        itemRepository.save(itemInDB);
        return String.format("redirect:/items/view/%d", itemid);
    }

    /**
     * Show the form for scanning QR to move.
     *
     * @param id item id.
     * @param model - Additional attributes used by the web form.
     */
    @GetMapping("/qrmove")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String showQRMoveForm(int id, Model model) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + id));
        model.addAttribute("itemid", id);

        return "items-move-qr";
    }

    /**
     * Update the location of the item.
     *
     * @param id item id.
     * @param placementid - the new location's QR code.
     * @param model - Additional attributes used by the web form.
     */
    @PostMapping("/qrupdateplace/{itemid}")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String moveQRItem(@PathVariable("itemid") int itemid, String placementid, Model model) {
        Item itemInDB = itemRepository.findById(itemid).orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + itemid));
        Integer cleanId = evaluateQRString(placementid);
        Item parentItem = itemRepository.getByQrcode(cleanId);
        // TODO: Check for legal level.
        itemInDB.setPlacementid(parentItem.getId());
        logger.info(String.format("Moving %d to %d", itemInDB.getId(), itemInDB.getPlacementid()));
        itemRepository.save(itemInDB);
        return String.format("redirect:/items/view/%d", itemid);
    }

    /**
     * General update of item.
     * We are not asking for pictures or subjects in the form. Therefore
     * these are copied from the database again and saved.
     *
     * @param id item id.
     * @param result - Results from validation of the web form.
     * @param model - Additional attributes used by the web form.
     */
    @PostMapping("/update/{id}")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String updateItem(@PathVariable("id") int id, @Valid Item item, BindingResult result, Model model) {
        logger.debug(item);
        if (result.hasErrors()) {
            item.setId(id);
            return "item-edit";
        }
        Item itemInDB = itemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + id));
        item.setPictures(itemInDB.getPictures());
        item.setPlacementid(itemInDB.getPlacementid());
        itemRepository.save(item);
        return String.format("redirect:/items/view/%d", id);
    }

    /**
     * Delete item.
     *
     * @param id item id.
     */
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE_ITEMS')")
    public String deleteItem(@PathVariable("id") int id, Model model) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + id));
        itemRepository.delete(item);
        logger.info(String.format("Deleted item Id %d", id));
        return "redirect:/items";
    }

    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int i = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Run a search and return results.
     *
     * @param page - page number of result list.
     * @param size - size of page in number of items.
     */
    @RequestMapping({"", "/", "/view"})
    @PreAuthorize("hasAuthority('VIEW_ITEMS')")
    public String showItemList(
            @RequestParam(name = "q", required=false) String query,
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {

        if (query == null) {
            query = "";
        }
        model.addAttribute("q", query);

        if (isNumeric(query)) {
            Integer cleanId = Integer.parseInt(query);
            List<Item> directitem = itemRepository.findByIdOrQrcode(cleanId, cleanId);
            if (directitem.size() > 0) {
                return String.format("redirect:/items/view/%d", directitem.get(0).getId());
            }
        }
        List<Item> items = new ArrayList<Item>();
        Pageable paging = PageRequest.of(page - 1, size);
        Page<Item> pagedItems =  itemRepository.findByFulltextContaining(query, paging);

        items = pagedItems.getContent();
        model.addAttribute("items", items);
        model.addAttribute("currentPage", pagedItems.getNumber() + 1);
        model.addAttribute("totalItems", pagedItems.getTotalElements());
        model.addAttribute("totalPages", pagedItems.getTotalPages());
        model.addAttribute("pageSize", size);
        return "items";
    }

    /*
     * Get rid of URL part.
     * Also works if the string is just a number.
     */
    private Integer evaluateQRString(String qrInput) {
        String prefixProperty = properties.getQrUrlPrefixes();
        String[] prefixes = prefixProperty.split("\\s*,\\s*");

        for (String prefix : prefixes) {
            if (qrInput.startsWith(prefix)) {
                qrInput = qrInput.substring(prefix.length());
                break;
            }
        }
        if (isNumeric(qrInput)) {
            return Integer.parseInt(qrInput);
        } else
            throw new IllegalArgumentException("QR code is not numeric");
    }

    /**
     * Show form to scan a QR and add it to an item.
     *
     * @param id item id to add code to.
     */
    @GetMapping("/addqrform")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String showAddQRForm(
                @RequestParam(name = "id", required=true) Integer id,
                Model model) {
        //model.addAttribute("id", id.toString());
        model.addAttribute("itemid", id);
        logger.info(String.format("Form to add QR code to %d", id));
        return "addqrform";
    }

    /**
     * Add QR code to item.
     * TODO: Don't allow duplicate QR codes.
     *
     * @param id item id.
     * @param qr QR code, which can be a URL.
     */
    @GetMapping("/addqr")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String addQRCode(
            @RequestParam(name = "id", required=true) Integer id,
            @RequestParam(name = "qr", required=true) String query,
            Model model) {
        logger.info(String.format("Add QR code %s to %d", query, id));
        if (query == null)
            throw new IllegalArgumentException("No QR code");
        Integer cleanId = evaluateQRString(query);
        Item duplicateItem = itemRepository.getByQrcode(cleanId);
        if (duplicateItem != null) {
            throw new IllegalArgumentException("QR code already assigned to item Id: " + duplicateItem.getId());
        }
        Item directitem = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item Id: " + id));
        directitem.setQrcode(cleanId);
        itemRepository.save(directitem);
        return String.format("redirect:/items/view/%d", directitem.getId());
    }

    /**
     * Lookup item on QR code.
     *
     * @param id item id.
     */
    @GetMapping("/qrfind")
    @PreAuthorize("hasAuthority('VIEW_ITEMS')")
    public String findByQRCode(
            @RequestParam(name = "qr", required=true) String query,
            Model model) {
        logger.info(String.format("QR code query %s", query));
        if (query == null)
            throw new IllegalArgumentException("No QR code");
        Integer cleanId = evaluateQRString(query);
        Item directitem = itemRepository.getByQrcode(cleanId);
        if (directitem != null) {
            return String.format("redirect:/items/view/%d", directitem.getId());
        }
        model.addAttribute("qr", cleanId);
        return "qrresult";
    }

    /**
     * Factsheet for item.
     *
     * @param id item id.
     * @param model - Additional attributes used by the web form.
     * @param page - page number of result list.
     * @param size - size of page in number of items.
     */
    @GetMapping("/view/{id}")
    @PreAuthorize("hasAuthority('VIEW_ITEMS')")
    public String itemFactsheet(@PathVariable("id") int id,
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + id));
        model.addAttribute("item", item);
        CaseFile file = fileRepository.findById(item.getFileid())
                .orElseThrow(() -> new IllegalArgumentException("Invalid file Id:" + id));
        model.addAttribute("file", file);
        if (item.getDonorid() != null)
            model.addAttribute("donor", donorRepository.findById(item.getDonorid()));
        else
            model.addAttribute("donor",  Optional.empty());
        if (item.getProducerid() != null)
            model.addAttribute("producer", producerRepository.findById(item.getProducerid()));
        else
            model.addAttribute("producer", Optional.empty());
        //model.addAttribute("notes", noteRepository.findByItemId(id));
        model.addAttribute("parents", itemRepository.findParentContainers(id));

        List<Item> children = new ArrayList<Item>();
        Pageable paging = PageRequest.of(page - 1, size);
        Page<Item> pagedChildren =  itemRepository.findByPlacementidOrderByHeadline(id, paging);
        children = pagedChildren.getContent();
        model.addAttribute("children", children);
        model.addAttribute("currentPage", pagedChildren.getNumber() + 1);
        model.addAttribute("totalChildren", pagedChildren.getTotalElements());
        model.addAttribute("totalPages", pagedChildren.getTotalPages());
        model.addAttribute("pageSize", size);

        return "items-view";
    }

    /**
     * Upload file for transfer. The stored filename is the identifier from
     * the picture table.
     *
     * @param id - Item id
     * @param myFile - file with filename and content.
     */
    @RequestMapping(value = "/pictureupload", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ADD_PICTURES')")
    public String importPicture(
            @RequestParam("file") MultipartFile myFile,
            @RequestParam("id") int id) {

        //String originalName = myFile.getOriginalFilename();

        if (myFile == null || myFile.isEmpty()) {
            return String.format("redirect:/items/view/%d", id);
        }
        Item item = itemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + id));

        Picture picture = new Picture();
        picture.setTitle(item.getHeadline());
        picture.setOriginal("");
        picture.setMedium("");
        picture.setLow("");
        pictureRepository.save(picture);

        var pictureId = picture.getPictureid();
        picture.setOriginal(String.format("pictureoriginal/%d.jpg", pictureId));
        picture.setMedium(String.format("picturemedium/%d.jpg", pictureId));
        picture.setLow(String.format("picturelow/%d.jpg", pictureId));
        pictureRepository.save(picture);

        ItemPicture itemPicture = new ItemPicture();
        itemPicture.setItemid(id);
        itemPicture.setPictureid(pictureId);
        itemPictureRepository.save(itemPicture);

        logger.info(String.format("Upload of %s to picture id %d", myFile.getOriginalFilename(), pictureId));
        pictureService.store(myFile, pictureId);
        return String.format("redirect:/items/view/%d", id);
    }

}
