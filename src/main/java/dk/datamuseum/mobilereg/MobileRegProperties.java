package dk.datamuseum.mobilereg;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Read properties for the application.
 * All properties are prefixed with "mobilereg.".
 */
@Configuration
@ConfigurationProperties("mobilereg")
@Data
public class MobileRegProperties {

    /**
     * Folder location for storing pictures.
     */
    private String storageRootDir = "storage-dir";

    /**
     * Property name for URL prefixes in QR labels.
     */
    private String qrUrlPrefixes = "qr-urlprefixes";

}
