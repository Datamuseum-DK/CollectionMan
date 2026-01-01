package dk.datamuseum.mobilereg;

import java.util.Optional;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.repositories.ItemRepository;

/**
 * Example of a field validator for JPA. You annotate the field with @QRCode,
 * and it will then call isValid(). This doesn't work for finding duplicates,
 * as the validator will find the item it is about to update.
 */
@Slf4j
public class QRCodeValidator implements ConstraintValidator<QRCode, Integer> {

    private final ItemRepository itemRepository;

    public QRCodeValidator(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public boolean isValid(Integer qrCode, ConstraintValidatorContext context) {

        log.debug("Validating QR Code {}", qrCode);

        // if (qrCode != null) {
        //     return !itemRepository.existsByQrcode(qrCode);
        // }
        return true;
    }
}
