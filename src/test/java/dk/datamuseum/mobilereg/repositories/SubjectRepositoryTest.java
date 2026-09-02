package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.Subject;

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
class SubjectRepositoryTest {

    @Autowired
    private SubjectRepository subjectRepository;

    @Test
    void givenSubjectEntity_whenSaveSubject_thenSubjectIsPersisubject() {
        // given
        Subject subject = new Subject();
        subject.setTitle("subject");

        // when
        log.info(String.format("Subject id: {}", subject.getSubjectid()));
        subjectRepository.save(subject);
        int generatedID = (int)subject.getSubjectid();
        log.info(String.format("Subject id: {}", generatedID));
        assertThat(generatedID).isGreaterThan(0);

        // then
        Optional<Subject> retrievedSubject = subjectRepository.findById(generatedID);
        assertThat(retrievedSubject.isPresent()).isTrue();
        assertThat(retrievedSubject.get().getTitle()).isEqualTo("subject");
    }


    @Test
    @DisplayName("Lookup subject #79")
    void lookupUkendt() {
        Optional<Subject> retrievedSubject = subjectRepository.findById(79);
        assertThat(retrievedSubject.isPresent()).isTrue();
        assertThat(retrievedSubject.get().getTitle()).isEqualTo("Korthuller");
    }
}
