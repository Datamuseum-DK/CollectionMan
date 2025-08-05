package dk.datamuseum.mobilereg.service;

// import java.awt.Image;
import java.awt.image.BufferedImage;
// import java.io.InputStream;
import java.io.IOException;
// import java.io.OutputStream;
// import java.net.MalformedURLException;
// import java.nio.file.Files;
import java.nio.file.Path;
// import java.nio.file.Paths;
// import java.nio.file.StandardCopyOption;
// import java.util.stream.Stream;
import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
// import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
// import org.springframework.util.FileSystemUtils;
// import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

// import dk.datamuseum.mobilereg.MobileRegProperties;
import dk.datamuseum.mobilereg.model.StoredFile;

/**
 * Implementation of Picture service.
 * Calls storage service for all storage.
 */
@Service
public class PictureServiceImpl implements PictureService {

    private StorageService storageService;

    private ScalingService scalingService;

    private Log logger = LogFactory.getLog(PictureServiceImpl.class);

    @Autowired
    public PictureServiceImpl(StorageService storageService, ScalingService scalingService) {
        this.storageService = storageService;
        this.scalingService = scalingService;

        for (String subDir : subDirs) {
            storageService.init(subDir);
        }
    }

    @Override
    public void store(MultipartFile file, int pictureId) {
        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file.");
        }
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new StorageFileNotFoundException("Could not read image input", e);
        }

        // Store synchrounously
        scalingService.store(originalImage, subDirs[0], pictureId, maxDims[0]);

        for (int i = 1; i < subDirs.length; i++) {
            scalingService.storeAsync(originalImage, subDirs[i], pictureId, maxDims[i]);
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
            logger.info(String.format("Not a legal subdirectory for picture: %s", reqSubDir));
            throw new StorageFileNotFoundException("Could not read file: " + path);
        }
        StoredFile fileRec = new StoredFile();
        fileRec.setFilename(filename);
        fileRec.setSubDir(reqSubDir);

        Path file = storageService.load(reqSubDir, filename);
        Resource resource = new FileSystemResource(file);
        if (!resource.exists()) {
            logger.info(String.format("Could not read file: %s", path));
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
}
