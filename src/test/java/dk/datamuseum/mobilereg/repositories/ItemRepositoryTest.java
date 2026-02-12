package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.CaseFile;
import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.entities.ItemClass;
import dk.datamuseum.mobilereg.entities.ItemStatus;
import dk.datamuseum.mobilereg.entities.Picture;
import dk.datamuseum.mobilereg.entities.Subject;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemClassRepository itemClassRepository;

    @Autowired
    private ItemStatusRepository itemStatusRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void minimumLevelChild() {
        Integer level = itemRepository.findMinLevel(11001745);
        assertThat(level).isEqualTo(50);

        level = itemRepository.findMinLevel(11002191);
        assertThat(level).isEqualTo(10000001);

        level = itemRepository.findMinLevel(10000032);
        assertThat(level).isEqualTo(30);
    }

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

        ItemStatus itemStatus = itemStatusRepository.findById(1).orElseThrow(()
                -> new IllegalArgumentException("ItemStatus id not find"));
        // given
        Item item = new Item();
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
        item.setItemextrainfo("");
        item.setItemrestoration("");
        item.setItemreferences("");
        item.setItemremarks("");
        item.setItemacquiretype(1);
        item.setPictures(new ArrayList<Picture>());
        item.setSubjects(new ArrayList<Subject>());
        item.setItemStatus(itemStatus);
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

    /**
     * It shall not be possible to delete item 11001745 as it has
     * children and subjects.
     * The exception is thrown when the updates are flushed to the database.
     */
    @Test
    void deleteItem11001745() throws Exception {
        Optional<Item> optionalItem = itemRepository.findById(11001745);
        assertThat(optionalItem.isPresent()).isTrue();
        Item retrievedItem = optionalItem.get();
        itemRepository.deleteById(11001745);
        assertThatExceptionOfType(ConstraintViolationException.class)
             .isThrownBy(() -> entityManager.flush());
    }

    @Test
    void noDuplicateQRcodes() {
        Optional<Item> optionalItem = itemRepository.findById(10000001);
        assertThat(optionalItem.isPresent()).isTrue();
        Item retrievedItem = optionalItem.get();
        assertThat(retrievedItem.getHeadline()).isEqualTo("ICT mekanisk korthuller");
        retrievedItem.setQrcode(58531224);
        itemRepository.save(retrievedItem);
        assertThatExceptionOfType(ConstraintViolationException.class)
             .isThrownBy(() -> entityManager.flush());
    }
}
