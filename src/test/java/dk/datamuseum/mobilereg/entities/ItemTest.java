package dk.datamuseum.mobilereg.entities;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ItemTest {
    
    @Test
    public void whenCalledGetName_thenCorrect() {
        Item item = new Item();
        item.setId(100);
        
        assertThat(item.getId()).isEqualTo(100);
    }
    
}
