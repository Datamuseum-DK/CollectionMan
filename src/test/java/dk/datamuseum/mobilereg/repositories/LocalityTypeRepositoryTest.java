package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.entities.Locality;
import dk.datamuseum.mobilereg.entities.LocalityType;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class LocalityTypeRepositoryTest {

    @Autowired
    private LocalityRepository localityRepository;

    @Autowired
    private LocalityTypeRepository localityTypeRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @Transactional
    void readThenSaveType() {
        Optional<LocalityType> optionalLocalityType = localityTypeRepository.findById(1);
        assertThat(optionalLocalityType.isPresent()).isTrue();
        LocalityType retrievedLocalityType = optionalLocalityType.get();
        assertThat(retrievedLocalityType.getTitle()).isEqualTo("Legacy localities");
        List<Locality> localities = retrievedLocalityType.getLocalities();
        String prevName = "";
        for (Locality l : localities) {
            assertThat(l.getName().compareTo(prevName) >= 0).isTrue();
            //System.out.println("=======>" + l.getName());
            prevName = l.getName();
        }
        assertThat(localities.size()).isEqualTo(3);
        localityTypeRepository.save(retrievedLocalityType);
    }

    /**
     * Create a new locality type
     */
    @Test
    //@Disabled
    void givenLocalityTypeEntity_whenSaved_thenIsPersisted() {
        LocalityType localityType = new LocalityType();

        localityType.setTitle("Countries");
        // when
        localityTypeRepository.save(localityType);
        int savedLocalityTypeId = localityType.getId();

        // then
        Optional<LocalityType> retrievedLocalityType = localityTypeRepository.findById(savedLocalityTypeId);
        assertThat(retrievedLocalityType.isPresent()).isTrue();
        assertThat(retrievedLocalityType.get().getTitle()).isEqualTo("Countries");
    }

    /**
     * It shall not be possible to delete item class 1 as it has
     * items
     * The exception is thrown when the updates are flushed to the database.
     */
    @Test
    @Disabled
    void deleteLocalityType1() {
        Optional<LocalityType> optionalLocalityType = localityTypeRepository.findById(1);
        assertThat(optionalLocalityType.isPresent()).isTrue();
        LocalityType retrievedLocalityType = optionalLocalityType.get();
        assertThatExceptionOfType(DataIntegrityViolationException.class)
             .isThrownBy(() -> localityTypeRepository.deleteById(1));
    }

}
