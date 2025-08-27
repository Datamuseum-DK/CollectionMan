package dk.datamuseum.mobilereg.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import dk.datamuseum.mobilereg.MobileRegProperties;

/**
 * File-based implementation of storage service.
 */
@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    private Log logger = LogFactory.getLog(FileSystemStorageService.class);

    @Autowired
    public FileSystemStorageService(MobileRegProperties properties) {
        if (properties.getStorageRootDir().trim().length() == 0) {
            throw new StorageException("File upload location can not be Empty."); 
        }
        this.rootLocation = Paths.get(properties.getStorageRootDir().trim());
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void store(MultipartFile file, String subDir) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }
            Path destinationFile = rootLocation.resolve(
                            Paths.get(file.getOriginalFilename()))
                            .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new StorageException("Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    @Override
    public OutputStream getOutputHandle(String subDir, String fileName) {
        OutputStream os;

        Path destinationFile = rootLocation.resolve(
                Paths.get(subDir, fileName)).normalize().toAbsolutePath();
        if (!destinationFile.getParent().getParent().equals(rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new StorageException("Cannot store file outside current directory.");
            }
        try {
            os = new FileOutputStream(destinationFile.toFile());
        } catch (IOException e) {
            throw new StorageException("Failed to create file.", e);
        }
        return os;
    }

    @Override
    public Path load(String subDir, String filename) {
        return rootLocation.resolve(
                Paths.get(subDir, filename));
    }

    @Override
    public Resource loadAsResource(String subDir, String filename) {
        try {
            Path file = load(subDir, filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException(
                            "Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll(String subDir) {
        Path subDirLocation = rootLocation.resolve(subDir);
        FileSystemUtils.deleteRecursively(subDirLocation.toFile());
    }

    @Override
    public void delete(String subDir, String filename) {
        File file = load(subDir, filename).toFile();
        logger.debug(String.format("Deleted file: %s", file.toString()));
        file.delete();
    }

    /**
     * Create subdirectories in the root directory.
     * This is to be used by the clients of the service to coexist.
     */
    @Override
    public void init(String subDir) {
        try {
            Files.createDirectories(rootLocation.resolve(subDir));
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
