package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.Activity;
import dk.datamuseum.mobilereg.entities.ActivityType;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.*;

@Slf4j
@DataJpaTest
//@AutoConfigureTestDatabase(replace=Replace.NONE)
class ActivityRepositoryTest {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityTypeRepository activityTypeRepository;

    @Autowired
    private EntityManager entityManager;

    private ActivityType type1;

    private void getType1() {
        if (type1 == null) {   // Don't retrieve it twice.
            Optional<ActivityType> retrievedType1 = activityTypeRepository.findById(1);
            assertThat(retrievedType1.isPresent()).isTrue();
            type1 = retrievedType1.get();
        }
    }

    @Test
    void activityIsPersisted() {
        // given
        getType1();
        Activity activity = new Activity();
        activity.setNote("activity");
        activity.setItemid(11000937);
        activity.setActivityType(type1);
        activity.setCreator("dummy");  // No known user at this layer
        //activity.setCreated(LocalDateTime.parse("2023-12-31T12:23:34"));
        //activity.setLastmodified(LocalDateTime.parse("2024-04-30T16:20:38"));

        // when
        // log.info(String.format("Activity id: %d", activity.getId()));
        activityRepository.save(activity);
        int generatedID = (int)activity.getId();
        log.debug(String.format("Activity id: %d", generatedID));
        assertThat(generatedID).isGreaterThan(0);

        // then
        Optional<Activity> retrievedActivity = activityRepository.findById(generatedID);
        assertThat(retrievedActivity.isPresent()).isTrue();
        Activity probe = retrievedActivity.get();
        assertThat(probe.getNote()).isEqualTo("activity");
        log.info("Stored activity: {}", probe);
        // Clean up
        activityRepository.delete(probe);
    }


    /**
     * Activity 1 is loaded as test data with Liquibase.
     */
    @Test
    @DisplayName("Lookup activity #1")
    void lookupActivity1() {
        Activity probe;
        Optional<Activity> retrievedActivity = activityRepository.findById(1);
        assertThat(retrievedActivity.isPresent()).isTrue();
        probe = retrievedActivity.get();
        assertThat(probe.getNote()).isEqualTo("Fra [[genstand:10000030|Ukendt lokation]] til [[genstand:11001745|Palle 461]].");
    }

    @Test
    @DisplayName("Can't delete type 1")
    void deleteType1() {
        getType1();
        activityTypeRepository.delete(type1);

        Optional<Activity> retrievedActivity = activityRepository.findById(1);
        assertThat(retrievedActivity.isPresent()).isTrue(); // Is not deleted
    }
}
