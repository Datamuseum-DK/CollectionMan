package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.ReverseLink;

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
class ReverseLinkRepositoryTest {

    @Autowired
    private ReverseLinkRepository reverseLinkRepositoryTest;

    /**
     * Create reverse link.
     */
    @Test
    void givenReverseLink_whenSaved_thenIsPersisted() {
        // given
        ReverseLink reverseLink = new ReverseLink();
        reverseLink.setItemidfrom(10000001);
        reverseLink.setItemidto(11000937);

        // when
        log.info(String.format("ReverseLink id: {}", reverseLink.getItemidfrom()));
        reverseLinkRepositoryTest.save(reverseLink);
        //int generatedID = (int)reverseLink.getId();
        //log.info(String.format("ReverseLink id: {}", generatedID));
        //assertThat(generatedID).isGreaterThan(0);

        // then
        //Optional<ReverseLink> retrievedReverseLink = reverseLinkRepositoryTest.findById(generatedID);
        //assertThat(retrievedReverseLink.isPresent()).isTrue();
        //assertThat(retrievedReverseLink.get().getReverseLinknavn()).isEqualTo("reverseLink");
    }
}
