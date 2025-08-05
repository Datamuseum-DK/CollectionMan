package dk.datamuseum.mobilereg;

import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.repositories.ItemRepository;

import java.util.Optional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;


    /**
     * Incomplete, as all non-null fields must be set.
     */
    @Test
    @Disabled
    void givenItemEntity_whenSaveItem_thenItemIsPersisted() {
        // given
        Item item = new Item();
        item.setId(1);
        item.setHeadline("item");

        // when
        itemRepository.save(item);

        // then
        Optional<Item> retrievedItem = itemRepository.findById(1);
        assertThat(retrievedItem.isPresent()).isTrue();
        assertThat(retrievedItem.get().getHeadline()).isEqualTo("item");
    }
}
