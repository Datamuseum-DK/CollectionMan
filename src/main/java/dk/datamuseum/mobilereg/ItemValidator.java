package dk.datamuseum.mobilereg;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import dk.datamuseum.mobilereg.repositories.ItemRepository;
import dk.datamuseum.mobilereg.entities.Item;

/**
 * Validate items.
 * Ensure that the QR code isn't already assigned to another item.
 */
public class ItemValidator implements Validator {

    private ItemRepository itemRepository;

    /**
     * This Validator validates only Item instances.
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;

        //ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        //ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty");

        // TODO: Check for uniqueness
        Integer qrCode = item.getQrcode();
        if (qrCode != null) {
            if (qrCode < 12000000) {
                errors.rejectValue("qrcode", "Value of QR code is too low");
            }
        }
    }
}
