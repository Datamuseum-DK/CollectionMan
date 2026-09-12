package dk.datamuseum.mobilereg.repositories;

import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.entities.ItemClass;
import dk.datamuseum.mobilereg.entities.ItemStatus;
import dk.datamuseum.mobilereg.entities.ReverseLink;

import java.util.List;
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
import org.springframework.dao.DataIntegrityViolationException;
import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
class ReverseLinkRepositoryTest {

    @Autowired
    private ReverseLinkRepository reverseLinkRepositoryTest;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemStatusRepository itemStatusRepository;

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
        log.debug("ReverseLink id: {}", reverseLink.getItemidfrom());
        reverseLinkRepositoryTest.save(reverseLink);

        // then
        List<ReverseLink> retrievedReverseLinks = reverseLinkRepositoryTest.findByItemidto(11000937);
        assertThat(retrievedReverseLinks.size()).isEqualTo(1);
        assertThat(retrievedReverseLinks.get(0).getItemidfrom()).isEqualTo(10000001);
    }

    /**
     * Block link from non-existant item.
     */
     @Test
     void blockLinkFromNonItem() {
        ReverseLink reverseLink = new ReverseLink();
        reverseLink.setItemidfrom(10999999);
        reverseLink.setItemidto(11000937);
        assertThatExceptionOfType(DataIntegrityViolationException.class)
             .isThrownBy(() -> reverseLinkRepositoryTest.save(reverseLink));
     }
    /**
     * Test that links are not deleted when the item pointed to is deleted.
     */
     @Test
     void deleteTargetedItem() {

        ItemStatus itemStatus = itemStatusRepository.findById(1).orElseThrow(()
                -> new IllegalArgumentException("ItemStatus id not find"));

        // Create an item.
        Item targetItem = new Item();
        targetItem.setHeadline("item");
        targetItem.setProducerid(1);
        targetItem.setFileid(1);
        targetItem.setItemStatus(itemStatus);
        itemRepository.save(targetItem);
        int savedItemId = targetItem.getId();

        ReverseLink reverseLink = new ReverseLink();
        reverseLink.setItemidfrom(10000001);
        reverseLink.setItemidto(savedItemId);
        reverseLinkRepositoryTest.save(reverseLink);
        // Delete the target item
        itemRepository.deleteById(savedItemId);
        // then the link must still exist.
        List<ReverseLink> retrievedReverseLinks = reverseLinkRepositoryTest.findByItemidto(savedItemId);
        assertThat(retrievedReverseLinks.size()).isEqualTo(1);
        assertThat(retrievedReverseLinks.get(0).getItemidfrom()).isEqualTo(10000001);
     }
}
