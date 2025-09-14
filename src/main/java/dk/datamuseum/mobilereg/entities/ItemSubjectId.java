package dk.datamuseum.mobilereg.entities;

import lombok.Data;

/**
 * A combined key for itemids and subjectids.
 */
@Data
public class ItemSubjectId {

    private int itemid;

    private int subjectid;

}
