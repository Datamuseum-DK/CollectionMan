package dk.datamuseum.mobilereg.controllers;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.util.HtmlUtils;

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
import static dk.datamuseum.mobilereg.service.RichTextService.richText;

/**
 * Controller for items.
 */
@Slf4j
@Controller
@RequestMapping("/items")
public class ItemController {

    /** Pattern for URLs. */
    static Pattern urlPattern = Pattern.compile("(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:;/~+#-]*[\\w@?^=%&/~+#;])");
    static String  urlReplacement = "<a href='$0'>$0</a>";
    /** Pattern for Item numbers. */
    static Pattern itemPattern = Pattern.compile("(^|\\G|[\\s\\p{Punct}&&[^/]])(1[01]0[01]\\d{4})([\\s\\p{Punct}]|$)", Pattern.MULTILINE);
    static String  itemReplacement = "$1<a href='$2'>$2</a>$3";
    
    static Pattern bitsPattern = Pattern.compile("\\[\\[bits:(300[01]\\d{4})\\]\\]", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
    static String  bitsReplacement = "<a href='https://ta.ddhf.dk/wiki/Bits:$1'>Bits:$1</a>";

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

    /**
     * Get the items class level. 0 is topmost container.
     *
     * @param item - the item to get level from.
     * @return the level as an integer.
     */
    private Integer getItemLevel(Item item) {
        return item.getItemClass().getLevel();
    }

    /**
     * Checks if item can fit in the parent.
     */
    private void checkItemFit(Item item) {
        if (item.getPlacementid() != null) {
            Item parent = itemRepository.findById(item.getPlacementid()).orElseThrow(()
                -> new IllegalArgumentException("Invalid placementid Id:" + item.getPlacementid()));
            checkItemFit(item, parent);
        }
    }

    /**
     * Checks if item can fit in the parent.
     */
    private void checkItemFit(Item item, Item parent) {
        if (getItemLevel(item) <= getItemLevel(parent)) {
            throw new IllegalArgumentException(String.format(
                "Item %d can't be in container %d", item.getId(), parent.getId()));
        }
    }

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

    /**
     * List all locations.
     */
    @ModelAttribute("steder")
    public Iterable<Sted> steder() {
        return stedRepository.findByOrderByStednavn();
    }

    /**
     * List all subjects.
     */
    @ModelAttribute("allSubjects")
    public Iterable<Subject> allSubjects() {
        return subjectRepository.findByOrderByTitle();
    }

    /**
     * List all item classes.
     */
    @ModelAttribute("types")
    public List<ItemClass> itemclasses() {
        return itemClassRepository.findAllOrderByLevelDesc();
    }

    /**
     * List all item statuses.
     */
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
     *
     * @param model - Additional attributes used by the web form.
     */
    @RequestMapping("/addform")
    @PreAuthorize("hasAuthority('ADD_ITEMS')")
    public String showAddForm1(
                Item item,
                Model model) {
        log.debug("Add form for item {}", item.toString());
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
            //TODO parentClass seems unnecessary
            ItemClass parentClass = itemClassRepository.findById(parent.getItemClass().getId()).orElseThrow(()
                -> new IllegalArgumentException("Unable to get item class of parent Id:"  + item.getPlacementid()));
            model.addAttribute("locations", List.of(parent));
            model.addAttribute("types", itemClassRepository.findByLevelGreaterThan(parentClass.getLevel()));
        } else {
            model.addAttribute("locations", itemRepository.findByPlacementidNull());
        }
        return "item-addform";
    }

    /**
     * Create the item.
     *
     * @param item - The item record containing the entered information.
     * @param result - Results from validation of the web form.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template or redirection to factsheet of created item.
     */
    @PostMapping("/additem")
    @PreAuthorize("hasAuthority('ADD_ITEMS')")
    public String addItem(@Valid Item item, BindingResult result, Model model) {
        log.debug("Evaluating item {}", item.toString());
        if (result.hasErrors()) {
            log.debug("Result {}", result.toString());
            return "item-addform";
        }
        checkItemFit(item);
        itemRepository.save(item);
        return String.format("redirect:/items/view/%d", item.getId());
    }

    /**
     * Form for editing an item.
     *
     * @param id item id.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template.
     */
    @GetMapping("/edit")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String showUpdateForm(int id, Model model) {
        log.info("Edit form for {}", id);
        Item item = itemRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("Invalid item Id:" + id));
        model.addAttribute("item", item);
        model.addAttribute("files", fileRepository.findByOrderByTitle());

        return "item-edit";
    }

    /**
     * Show the form for moving an item to another container.
     * This shows the three ways to move as a menu.
     *
     * @param id item id.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template.
     */
    @GetMapping("/move")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String showMoveForm(int id, Model model) {
        Item item = itemRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("Invalid item Id:" + id));
        int level = getItemLevel(item);
        model.addAttribute("level", level);
        model.addAttribute("places", itemRepository.findByPlacementidNull());
        model.addAttribute("headline", item.getHeadline());
        model.addAttribute("itemid", id);

        return "items-move";
    }

    /**
     * Show the navigation for moving a container.
     * This shows the three ways to move as a menu. If placementid is filled out,
     * then it shows a button to select this place and any sub-containers, that
     * have a lower level than the item.
     *
     * @param itemid item id.
     * @param placementid - current selected place - must be an item id.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template.
     */
    @GetMapping("/move-nav")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String showMoveNav(int itemid, Integer placementid, Model model) {
        Item item = itemRepository.findById(itemid).orElseThrow(()
                -> new IllegalArgumentException("Invalid item Id:" + itemid));
        int level = getItemLevel(item);
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
        model.addAttribute("headline", item.getHeadline());
        model.addAttribute("itemid", itemid);

        return "items-move-nav";
    }

    /**
     * Show the hits from searching a container by itemid, QR code, or tekst.
     *
     * @param itemid - item id.
     * @param query - query string.
     * @param model - Additional attributes used by the web form.
     * @param page - page number of paged results.
     * @param size - number of results on a single page.
     * @return name of Thymeleaf template.
     */
    @GetMapping("/move-search")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String showMoveSearch(
            int itemid,
            @RequestParam(name = "q", required=false) String query,
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {

        if (query == null) {
            query = "";
        }
        model.addAttribute("q", query);
        List<Item> items = new ArrayList<Item>();
        if (isNumeric(query)) {
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
        model.addAttribute("itemid", itemid);
        model.addAttribute("pageSize", size);
        return "items-move-search";
    }

    /**
     * Update the location of the item.
     *
     * @param itemid item id.
     * @param placementid - the item id of the new location.
     * @param model - Additional attributes used by the web form.
     * @return redirection to factsheet of created item.
     */
    @PostMapping("/updateplace/{itemid}")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String moveItem(@PathVariable("itemid") int itemid, Integer placementid, Model model) {
        Item itemInDB = itemRepository.findById(itemid).orElseThrow(()
                -> new IllegalArgumentException("Invalid item Id:" + itemid));

        Item parentInDB = itemRepository.findById(placementid).orElseThrow(()
                -> new IllegalArgumentException("Invalid parent Id:" + itemid));
        checkItemFit(itemInDB, parentInDB);

        itemInDB.setPlacementid(placementid);
        log.info("Moving {} to {}", itemInDB.getId(), itemInDB.getPlacementid());
        itemRepository.save(itemInDB);
        return String.format("redirect:/items/view/%d", itemid);
    }

    /**
     * Show the form for scanning QR to move.
     *
     * @param id item id.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template.
     */
    @GetMapping("/qrmove")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String showQRMoveForm(int id, Model model) {
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("Invalid item Id: " + id);
        }
        model.addAttribute("itemid", id);

        return "items-move-qr";
    }

    /**
     * Update the location of the item.
     *
     * @param itemid item id.
     * @param placementid - the new location's QR code - this can be a URL.
     * @param model - Additional attributes used by the web form.
     * @return redirection to factsheet of created item.
     */
    @PostMapping("/qrupdateplace/{itemid}")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String moveQRItem(@PathVariable("itemid") int itemid,
                String placementid, Model model) {
        Item itemInDB = itemRepository.findById(itemid).orElseThrow(()
                -> new IllegalArgumentException("Invalid item Id:" + itemid));
        Integer cleanId = evaluateQRString(placementid);
        Item parentItem = itemRepository.getByQrcode(cleanId);

        checkItemFit(itemInDB, parentItem);
        itemInDB.setPlacementid(parentItem.getId());
        log.info("Moving {} to {}", itemInDB.getId(), itemInDB.getPlacementid());
        itemRepository.save(itemInDB);
        return String.format("redirect:/items/view/%d", itemid);
    }

    /**
     * General update of item.
     * We are not asking for pictures or placement in the form. Therefore
     * these are copied from the database again and saved.
     *
     * @param id - item id.
     * @param item - the updated record.
     * @param result - Results from validation of the web form.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template or redirection to factsheet.
     */
    @PostMapping("/update/{id}")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String updateItem(@PathVariable("id") int id,
            @Valid Item item,
            BindingResult result, Model model) {
        log.debug("Incoming item: {}", item);
        if (result.hasErrors()) {
            item.setId(id);
            return "item-edit";
        }
        Item itemInDB = itemRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException("Invalid item Id:" + id));
        item.setPictures(itemInDB.getPictures());
        item.setPlacementid(itemInDB.getPlacementid());
        checkItemFit(item);
        itemRepository.save(item);
        return String.format("redirect:/items/view/%d", id);
    }

    /**
     * Delete item.
     *
     * @param id item id.
     * @param model - Additional attributes used by the web form.
     * @return redirection to factsheet of created item.
     */
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('DELETE_ITEMS')")
    public String deleteItem(@PathVariable("id") int id, Model model) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + id));
        itemRepository.delete(item);
        log.info(String.format("Deleted item Id %d", id));
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
     * @param query - query string.
     * @param model - Additional attributes used by the web form.
     * @param page - page number of result list.
     * @param size - size of page in number of items.
     * @return name of Thymeleaf template or redirection to factsheet of item.
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
        query = query.strip();
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
     *
     * @param qrInput - The QR value - scanned or entered.
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
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template.
     */
    @GetMapping("/addqrform")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String showAddQRForm(
                @RequestParam(name = "id", required=true) Integer id,
                Model model) {
        //model.addAttribute("id", id.toString());
        model.addAttribute("itemid", id);
        log.info("Form to add QR code to {}", id);
        return "addqrform";
    }

    /**
     * Add QR code to item.
     * TODO: Don't allow duplicate QR codes.
     *
     * @param id item id.
     * @param query QR code, which can be a URL.
     * @param model - Additional attributes used by the web form.
     * @return redirection to factsheet of created item.
     */
    @GetMapping("/addqr")
    @PreAuthorize("hasAuthority('CHANGE_ITEMS')")
    public String addQRCode(
            @RequestParam(name = "id", required=true) Integer id,
            @RequestParam(name = "qr", required=true) String query,
            Model model) {
        log.info("Add QR code {} to {}", query, id);
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
     * @param query QR code, which can be a URL.
     * @param model - Additional attributes used by the web form.
     * @return name of Thymeleaf template.
     * @return name of Thymeleaf template or redirection to factsheet of created item.
     */
    @GetMapping("/qrfind")
    @PreAuthorize("hasAuthority('VIEW_ITEMS')")
    public String findByQRCode(
            @RequestParam(name = "qr", required=true) String query,
            Model model) {
        log.info("QR code query {}", query);
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

    /*
     * Produce HTML with detected links. A simple markdown syntax.
     * item numbers are detected, URLs are detected.
     *
     * @param plainText - the text field from the database.
     * @return HTML escaped text with some HTML tags.
     */
    /*
    private String richText(String plainText) {
        String richDesc = HtmlUtils.htmlEscape(plainText, "UTF-8");
        richDesc = urlPattern.matcher(richDesc).replaceAll(urlReplacement);
        richDesc = itemPattern.matcher(richDesc).replaceAll(itemReplacement);
        richDesc = bitsPattern.matcher(richDesc).replaceAll(bitsReplacement);

        return richDesc;
    }
    */

    private void enrichTextareas(Item item) {
        item.setDescription(richText(item.getDescription()));
        item.setItemextrainfo(richText(item.getItemextrainfo()));
        item.setItemreferences(richText(item.getItemreferences()));
        item.setItemrestoration(richText(item.getItemrestoration()));
        item.setItemremarks(richText(item.getItemremarks()));
        item.setItemusedby(richText(item.getItemusedby()));
    }

    /**
     * Factsheet for item.
     *
     * @param id item id.
     * @param model - Additional attributes used by the web form.
     * @param page - page number of result list.
     * @param size - size of page in number of items.
     * @return name of Thymeleaf template.
     */
    @GetMapping("/view/{id}")
    @PreAuthorize("hasAuthority('VIEW_ITEMS')")
    public String itemFactsheet(@PathVariable("id") int id,
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + id));

        enrichTextareas(item);
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
     * @return redirection to factsheet of created item.
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
        picture.setItemid(id);
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

        log.info("Upload of {} to picture id {}", myFile.getOriginalFilename(), pictureId);
        pictureService.store(myFile, pictureId);
        return String.format("redirect:/items/view/%d", id);
    }

}
