package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.CaseFile;
import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.entities.ItemClass;
import dk.datamuseum.mobilereg.entities.Picture;
import dk.datamuseum.mobilereg.entities.Subject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemClassRepository itemClassRepository;

    @Test
    void readThenSaveItem() {
        Optional<Item> optionalItem = itemRepository.findById(10000001);
        assertThat(optionalItem.isPresent()).isTrue();
        Item retrievedItem = optionalItem.get();
        assertThat(retrievedItem.getHeadline()).isEqualTo("ICT mekanisk korthuller");
        itemRepository.save(retrievedItem);
    }

    /**
     * Incomplete, as all non-null fields must be set.
     */
    @Test
    //@Disabled
    void givenItemEntity_whenSaveItem_thenItemIsPersisted() {
        ItemClass itemClass = itemClassRepository.findById(1).orElseThrow(()
                -> new IllegalArgumentException("Itemclass id not find"));

        CaseFile caseFile = fileRepository.findById(1).orElseThrow(()
                -> new IllegalArgumentException("FileCase id not find"));

        // given
        Item item = new Item();
        //item.setId(1);
        //item.setItemtemporary(0);
        item.setItemdeleted(0);
        item.setHeadline("item");
        item.setDescription("item");
        item.setItemsize("");
        item.setItemweight("");
        item.setItemmodeltype("");
        item.setItemserialno("");
        item.setFileid(1);
        item.setItemClass(itemClass);
        item.setItemreceivedby("SMR");
        item.setItemusedby("SMR");
        item.setItemusedfor("");
        item.setItemusedwhere("");
        item.setItemextrainfo("");
        item.setItemrestoration("");
        item.setItemreferences("");
        item.setItemremarks("");
        //item.setIteminternal(0);
        item.setItemacquiretype(1);
        item.setPictures(new ArrayList<Picture>());
        item.setSubjects(new ArrayList<Subject>());
        item.setLastmodified(LocalDateTime.parse("2014-04-30T06:20:38"));
        item.setItemdatingfrom(LocalDate.parse("1950-01-01"));
        item.setItemdatingto(LocalDate.parse("1969-12-31"));
        item.setItemreceived(LocalDate.parse("1988-12-31"));
        item.setProducerid(1);

        // when
        itemRepository.save(item);
        int savedItemId = item.getId();

        // then
        Optional<Item> retrievedItem = itemRepository.findById(savedItemId);
        assertThat(retrievedItem.isPresent()).isTrue();
        assertThat(retrievedItem.get().getHeadline()).isEqualTo("item");
    }
}
