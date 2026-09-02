package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.Sted;

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
class StedRepositoryTest {

    @Autowired
    private StedRepository stedRepository;

    @Test
    void givenStedEntity_whenSaveSted_thenStedIsPersisted() {
        // given
        Sted sted = new Sted();
        sted.setStednavn("sted");

        // when
        log.info(String.format("Sted id: {}", sted.getId()));
        stedRepository.save(sted);
        int generatedID = (int)sted.getId();
        log.info(String.format("Sted id: {}", generatedID));
        assertThat(generatedID).isGreaterThan(0);

        // then
        Optional<Sted> retrievedSted = stedRepository.findById(generatedID);
        assertThat(retrievedSted.isPresent()).isTrue();
        assertThat(retrievedSted.get().getStednavn()).isEqualTo("sted");
    }


    @Test
    @DisplayName("Lookup sted #1")
    void lookupUkendt() {
        Optional<Sted> retrievedSted = stedRepository.findById(1);
        assertThat(retrievedSted.isPresent()).isTrue();
        assertThat(retrievedSted.get().getStednavn()).isEqualTo("Ukendt");
    }
}
