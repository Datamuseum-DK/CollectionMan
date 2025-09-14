package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.Sted;

import java.util.Optional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
//@AutoConfigureTestDatabase(replace=Replace.NONE)
class StedRepositoryTest {

    @Autowired
    private StedRepository stedRepository;

    private Log logger = LogFactory.getLog(StedRepositoryTest.class);

    @Test
    void givenStedEntity_whenSaveSted_thenStedIsPersisted() {
        // given
        Sted sted = new Sted();
        sted.setStednavn("sted");

        // when
        logger.info(String.format("Sted id: %d", sted.getId()));
        stedRepository.save(sted);
        int generatedID = (int)sted.getId();
        logger.info(String.format("Sted id: %d", generatedID));
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
