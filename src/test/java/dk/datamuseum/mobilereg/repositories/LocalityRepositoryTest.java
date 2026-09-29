package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.Locality;
import dk.datamuseum.mobilereg.entities.LocalityType;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class LocalityRepositoryTest {

    @Autowired
    private LocalityRepository localityRepository;

    @Autowired
    private LocalityTypeRepository localityTypeRepository;

    @Test
    void givenLocalityEntity_whenSaveLocality_thenLocalityIsPersisted() {
        Optional<LocalityType> optionalLocalityType = localityTypeRepository.findById(1);
        assertThat(optionalLocalityType.isPresent()).isTrue();
        LocalityType retrievedLocalityType = optionalLocalityType.get();
        // given
        Locality locality = new Locality();
        locality.setName("sted");
        locality.setLocalitytype(retrievedLocalityType);

        // when
        log.debug("Locality id: {}", locality.getId());
        localityRepository.save(locality);
        int generatedID = locality.getId();
        log.debug("Locality id: {}", generatedID);
        assertThat(generatedID).isGreaterThan(0);

        // then
        Optional<Locality> retrievedLocality = localityRepository.findById(generatedID);
        assertThat(retrievedLocality.isPresent()).isTrue();
        assertThat(retrievedLocality.get().getName()).isEqualTo("sted");
        localityRepository.deleteById(generatedID);
    }


    @Test
    @DisplayName("Lookup locality #1")
    void lookupUkendt() {
        Optional<Locality> retrievedLocality = localityRepository.findById(1);
        assertThat(retrievedLocality.isPresent()).isTrue();
        assertThat(retrievedLocality.get().getName()).isEqualTo("Ukendt");
    }
}
