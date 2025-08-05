package dk.datamuseum.mobilereg;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Read properties for the application.
 * All properties are prefixed with "mobilereg.".
 */
@Configuration
@ConfigurationProperties("mobilereg")
public class MobileRegProperties {

        /**
         * Folder location for storing pictures
         */
        private String storageRootDir = "storage-dir";

        /**
         * Get the folder path for storage.
         */
        public String getStorageRootDir() {
                return storageRootDir;
        }

        /**
         * Set the folder path for storage.
         * Used only by Spring Boot.
         *
         * @param storageRootDir - the root directory.
         */
        public void setStorageRootDir(String storageRootDir) {
                this.storageRootDir = storageRootDir;
        }

}
