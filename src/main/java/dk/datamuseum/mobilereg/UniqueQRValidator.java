package dk.datamuseum.mobilereg;

import java.util.Optional;

import org.springframework.beans.BeanWrapperImpl;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import dk.datamuseum.mobilereg.entities.Item;
import dk.datamuseum.mobilereg.repositories.ItemRepository;

/**
 * Example of validation of multiple fields in an entity. Cannot be used for
 * duplicates check, as JPA calls the no-args constructor.
 *
 * @see <a href="https://www.bezkoder.com/spring-boot-custom-validation/">Spring Boot custom Validation example</a>.
 */
@Slf4j
public class UniqueQRValidator implements ConstraintValidator<UniqueQR, Object> {

    private final ItemRepository itemRepository;

    private String itemid;
    private String qrCode;

    public UniqueQRValidator(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void initialize(UniqueQR constraintAnnotation) {
       this.itemid = constraintAnnotation.itemid();
       this.qrCode = constraintAnnotation.qrCode();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // if (itemRepository == null) return true;

        Object itemidValue = new BeanWrapperImpl(value).getPropertyValue(itemid);
        Object qrCodeValue = new BeanWrapperImpl(value).getPropertyValue(qrCode);

        log.info("Validating QR Code: {} itemid: {}", qrCodeValue, itemidValue);

        if (qrCodeValue != null) {
            Optional<Item> duplicateItem = itemRepository.getByQrcode(Integer.parseInt(qrCodeValue.toString()));
            if (duplicateItem.isPresent() && duplicateItem.get().getId() != Integer.parseInt(itemidValue.toString())) {
                return false;
            }
        }
        return true;
    }
}
