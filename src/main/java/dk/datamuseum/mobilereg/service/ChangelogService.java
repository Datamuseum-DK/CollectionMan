package dk.datamuseum.mobilereg.service;

import org.springframework.stereotype.Service;

import dk.datamuseum.mobilereg.entities.ActivityType;
import dk.datamuseum.mobilereg.entities.Activity;
import dk.datamuseum.mobilereg.repositories.ActivityTypeRepository;
import dk.datamuseum.mobilereg.repositories.ActivityRepository;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ChangelogService {

    private ActivityRepository activityRepository;

    private ActivityTypeRepository activityTypeRepository;

    private ActivityType getTypeById(int typeId) {
        Optional<ActivityType> retrievedType = activityTypeRepository.findById(typeId);
        // assertThat(retrievedType.isPresent()).isTrue();
        return retrievedType.get();
    }

    public ChangelogService(ActivityRepository activityRepository,
                           ActivityTypeRepository activityTypeRepository) {
        this.activityRepository = activityRepository;
        this.activityTypeRepository = activityTypeRepository;
    }

    /**
     * Log an activity for an item.
     *
     * @param typeId - type of activity and must exist in activity_types table.
     * @param itemId - item id.
     * @param note - the information to log.
     */
    public void logActivity(int typeId, int itemId, String note) {
        Activity activity = new Activity();
        activity.setNote(note);
        activity.setItemid(itemId);
        ActivityType activityType = getTypeById(typeId);
        activity.setActivityType(activityType);
        // activity.setCreator("sr");
        // activity.setCreated(LocalDateTime.parse("2023-12-31T12:23:34"));
        // activity.setLastmodified(LocalDateTime.parse("2024-04-30T16:20:38"));
        activityRepository.save(activity);
    }

    public Iterable<Activity> getActvitiesForItem(int itemid) {
        return activityRepository.findByItemidOrderByCreated(itemid);
    }
}
