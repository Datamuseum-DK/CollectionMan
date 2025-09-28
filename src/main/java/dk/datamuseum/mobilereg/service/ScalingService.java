package dk.datamuseum.mobilereg.service;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.imgscalr.Scalr;
import static org.imgscalr.Scalr.Rotation;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;

/**
 * Scaling of images.
 * Calls storage service for all storage.
 * FIXME: Not thread-safe
 */
@Slf4j
@Service
public class ScalingService {

    //@Autowired
    private StorageService storageService;

    /** Orientation of image in sourceFile. */
    private int orientation;

    private BufferedImage originalImage;

    /**
     * Constructor.
     */
    @Autowired
    public ScalingService(StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * Simple scaling of image. UNUSED.
     *
     * @param maxDim - Max size in width and height.
     * @return scaled image.
     */
    private BufferedImage resizeImage(int maxDim) {
        int width, height;
        double scaling;

        width = originalImage.getWidth();
        height = originalImage.getHeight();
        if (Math.max(width, height) > maxDim) {
            scaling = (0.0 + Math.max(width, height)) / maxDim;
            width = Double.valueOf(width / scaling).intValue();
            height = Double.valueOf(height / scaling).intValue();
        }
        log.info(String.format("New dimensions %dx%d", width, height));

        Image newResizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        bufferedImage.getGraphics().drawImage(newResizedImage, 0, 0, null);
        return bufferedImage;
    }

    /**
     * Scale image with understanding of camera orientation.
     *
     * @param maxDim - Max size in width and height.
     * @return scaled image.
     *
     * @see <a href="https://stackoverflow.com/questions/21951892/how-to-determine-and-auto-rotate-images">Stackoverflow</a>.
     * @see <a href="https://www.javadoc.io/doc/org.imgscalr/imgscalr-lib/latest/org/imgscalr/Scalr.html">Scalr</a>.
     */
    private BufferedImage scaleWithOrientation(int maxDim) {
        BufferedImage scaledImg = Scalr.resize(originalImage, maxDim);

        switch (orientation) {
        case 1: // No rotation
            break;
        case 2: // Flip X
            scaledImg = Scalr.rotate(scaledImg, Rotation.FLIP_HORZ);
            break;
        case 3: // PI rotation
            scaledImg = Scalr.rotate(scaledImg, Rotation.CW_180);
            break;
        case 4: // Flip Y
            scaledImg = Scalr.rotate(scaledImg, Rotation.FLIP_VERT);
            break;
        case 5: // - PI/2 and Flip X
            scaledImg = Scalr.rotate(scaledImg, Rotation.CW_90);
            scaledImg = Scalr.rotate(scaledImg, Rotation.FLIP_HORZ);
            break;
        case 6: // -PI/2 and -width
            scaledImg = Scalr.rotate(scaledImg, Rotation.CW_90);
            break;
        case 7: // PI/2 and Flip
            scaledImg = Scalr.rotate(scaledImg, Rotation.CW_90);
            scaledImg = Scalr.rotate(scaledImg, Rotation.FLIP_VERT);
            break;
        case 8: // PI / 2
            scaledImg = Scalr.rotate(scaledImg, Rotation.CW_270);
            break;
        default:
            break;
        }
        return scaledImg;
    }

    /**
     * Set the file to work on and determine the orientation.
     * First load the image into an image buffer for later work.
     * Then try to get the orientation.
     *
     * @param sourceFile - file from user upload.
     */
    public void setSourceFile(MultipartFile sourceFile) throws IOException {
        orientation = 1;  // Default: No rotation

        InputStream sourceStream = sourceFile.getInputStream();
        originalImage = ImageIO.read(sourceStream);
        sourceStream.close();

        try {
            sourceStream = sourceFile.getInputStream();
            Metadata metadata = ImageMetadataReader.readMetadata(sourceStream);
            sourceStream.close();
            ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            if (exifIFD0Directory == null) return;
            orientation = exifIFD0Directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        } catch (ImageProcessingException ex) {
            log.warn("Unable to process image: {}", sourceFile.getOriginalFilename());
        } catch (MetadataException ex) {
            log.debug("No EXIF information found for image: " + sourceFile.getOriginalFilename());
        }
    }

    /**
     * Synchronous store.
     * When calling storeAsync() directly inside the same class, the @Async is
     * not invoked.
     *
     * @param subDir - name of directory for that scaling.
     * @param pictureId - picture primary key (a number) from the database.
     * @param dimension - Max size in width and height.
     */
    public void store(String subDir, int pictureId, int dimension) {
        storeAsync(subDir, pictureId, dimension);
    }

    /**
     * Scale image and store it, using a thread in the background.
     *
     * @param subDir - name of directory for that scaling.
     * @param pictureId - picture primary key (a number) from the database.
     * @param dimension - Max size in width and height.
     */
    @Async
    public void storeAsync(String subDir, int pictureId, int dimension) {
        String formatName = "jpg";
        String imageFileToWrite = Integer.toString(pictureId) + "." + formatName;

        try {
            //BufferedImage resizedImage = resizeImage(dimension);
            BufferedImage resizedImage = scaleWithOrientation(dimension);
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
