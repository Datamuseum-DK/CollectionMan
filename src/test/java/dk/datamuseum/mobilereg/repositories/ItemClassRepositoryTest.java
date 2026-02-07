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
class ItemClassRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemClassRepository itemClassRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void readThenSaveItem() {
        Optional<ItemClass> optionalItemClass = itemClassRepository.findById(1);
        assertThat(optionalItemClass.isPresent()).isTrue();
        ItemClass retrievedItemClass = optionalItemClass.get();
        assertThat(retrievedItemClass.getName()).isEqualTo("Artefakt");
        itemClassRepository.save(retrievedItemClass);
    }

    /**
     * Create a new item class.
     */
    @Test
    //@Disabled
    void givenItemClassEntity_whenSaved_thenIsPersisted() {
        ItemClass itemClass = new ItemClass();
        itemClass.setLevel(11);
        itemClass.setName("Exhibition");
        // when
        itemClassRepository.save(itemClass);
        int savedItemClassId = itemClass.getId();

        // then
        Optional<ItemClass> retrievedItemClass = itemClassRepository.findById(savedItemClassId);
        assertThat(retrievedItemClass.isPresent()).isTrue();
        assertThat(retrievedItemClass.get().getName()).isEqualTo("Exhibition");
    }

    /**
     * It shall not be possible to delete item class 1 as it has
     * items
     * The exception is thrown when the updates are flushed to the database.
     */
    @Test
    void deleteItemClass1() {
        Optional<ItemClass> optionalItemClass = itemClassRepository.findById(1);
        assertThat(optionalItemClass.isPresent()).isTrue();
        ItemClass retrievedItemClass = optionalItemClass.get();
        itemClassRepository.deleteById(1);
        assertThatExceptionOfType(ConstraintViolationException.class)
             .isThrownBy(() -> entityManager.flush());
    }

}
