package dk.datamuseum.mobilereg.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dk.datamuseum.mobilereg.model.StoredFile;

/**
 * Implementation of Picture service.
 * Calls storage service for all storage.
 */
@Slf4j
@Service
public class PictureServiceImpl implements PictureService {

    private final StorageService storageService;

    @Autowired
    public PictureServiceImpl(StorageService storageService) {
        this.storageService = storageService;

        for (String subDir : subDirs) {
            storageService.init(subDir);
        }
    }

    @Override
    public void store(MultipartFile file, int pictureId) {
        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file.");
        }

        ScalingService scalingService = new ScalingService(storageService);
        try {
            scalingService.setSourceFile(file);
        } catch (IOException e) {
            throw new StorageFileNotFoundException("Could not read image input", e);
        }
        // Scale and store thumbnail synchrounously
        scalingService.store(subDirs[0], pictureId, maxDims[0]);

        // Scale and store the asynchrounously
        for (int i = 1; i < subDirs.length; i++) {
            scalingService.storeAsync(subDirs[i], pictureId, maxDims[i]);
        }
    }

    private boolean isLegalDir(String reqSubDir) {
        for (String subDir : subDirs) {
            if (reqSubDir.equals(subDir)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public StoredFile getByPath(String reqSubDir, String filename) {

        String path = reqSubDir + "/" + filename;
        if (!isLegalDir(reqSubDir)) {
            log.info("Not a legal subdirectory for picture: {}", reqSubDir);
            throw new StorageFileNotFoundException("Could not read file: " + path);
        }
        StoredFile fileRec = new StoredFile();
        fileRec.setFilename(filename);
        fileRec.setSubDir(reqSubDir);

        Path file = storageService.load(reqSubDir, filename);
        Resource resource = new FileSystemResource(file);
        if (!resource.exists()) {
            log.info("Could not read file: {}", path);
            throw new StorageFileNotFoundException("Could not read file: " + path);
        }
        fileRec.setContentResource(resource);
        fileRec.setContentType(MediaType.IMAGE_JPEG);
        return fileRec;
    }

    @Override
    public void deleteAll() {
        for (String subDir : subDirs) {
            storageService.deleteAll(subDir);
        }
    }

    @Override
    public void delete(String filename) {
        for (String subDir : subDirs) {
            storageService.delete(subDir, filename);
        }
    }
}
