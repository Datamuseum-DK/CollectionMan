package dk.datamuseum.mobilereg.service;

import java.awt.Image;
import java.awt.image.BufferedImage;
//import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
//import java.net.MalformedURLException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.stream.Stream;
import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
//import org.springframework.util.FileSystemUtils;
//import org.springframework.util.StringUtils;
//import org.springframework.web.multipart.MultipartFile;

//import dk.datamuseum.mobilereg.MobileRegProperties;
//import dk.datamuseum.mobilereg.model.StoredFile;

/**
 * Scaling of images.
 * Calls storage service for all storage.
 */
@Service
public class ScalingService {

    @Autowired
    private StorageService storageService;

    private Log logger = LogFactory.getLog(ScalingService.class);

    private BufferedImage resizeImage(BufferedImage image, int maxDim) {
        int width, height;
        double scaling;

        width = image.getWidth();
        height = image.getHeight();
        if (Math.max(width, height) > maxDim) {
            scaling = (0.0 + Math.max(width, height)) / maxDim;
            width = Double.valueOf(width / scaling).intValue();
            height = Double.valueOf(height / scaling).intValue();
        }
        logger.info(String.format("New dimensions %dx%d", width, height));

        Image newResizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        bufferedImage.getGraphics().drawImage(newResizedImage, 0, 0, null);
        return bufferedImage;
    }

    /**
     * Synchronous store.
     */
    public void store(BufferedImage originalImage, String subDir, int pictureId, int dimension) {
        storeAsync(originalImage, subDir, pictureId, dimension);
    }

    /**
     * Scale image and store it, using a thread in the background.
     */
    @Async
    public void storeAsync(BufferedImage originalImage, String subDir, int pictureId, int dimension) {
        String formatName = "jpg";
        String imageFileToWrite = Integer.toString(pictureId) + "." + formatName;

        try {
            BufferedImage resizedImage = resizeImage(originalImage, dimension);
            OutputStream outStream = storageService.getOutputHandle(subDir, imageFileToWrite);

            ImageIO.write(resizedImage, formatName, outStream);
            outStream.flush();
            outStream.close();
            resizedImage = null;
        } catch (IOException e) {
            throw new StorageFileNotFoundException("Could not store file: " + imageFileToWrite, e);
        }
    }

}
