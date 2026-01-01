package dk.datamuseum.mobilereg;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.stereotype.Component;

import dk.datamuseum.mobilereg.repositories.ItemRepository;
import dk.datamuseum.mobilereg.entities.Item;

/**
 * Validate items.
 * Ensure that the QR code isn't already assigned to another item.
 * Could also be used for testing if the item fits in the container.
 */
@Slf4j
@Component
public class ItemValidator implements Validator {

    private ItemRepository itemRepository;

    /**
     * Constructor.
     */
    public ItemValidator(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * This Validator validates only Item instances.
     */
    @Override
    public boolean supports(final Class<?> clazz) {
        return Item.class.equals(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        Item item = (Item) target;

        //ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        //ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty");

        Integer qrCode = item.getQrcode();
        if (qrCode != null) {
            Optional<Item> duplicateItem = itemRepository.getByQrcode(qrCode);
            if (duplicateItem.isPresent()
                    && duplicateItem.get().getId() != item.getId()) {
                errors.rejectValue("qrcode", "DuplicateQR",
                        "QR Code is already registered");
            }
        }
    }
}
