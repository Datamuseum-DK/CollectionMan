package dk.datamuseum.mobilereg.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import dk.datamuseum.mobilereg.model.StoredFile;

public interface PictureService {

    String PICTUREORIGINAL = "pictureoriginal";
    String PICTUREMEDIUM = "picturemedium";
    String PICTURELOW = "picturelow";
    String PICTURETINY = "tiny";
    String[] subDirs = { PICTURELOW, PICTUREMEDIUM, PICTUREORIGINAL, PICTURETINY };
    int[] maxDims = { 150, 640, 2048, 40 };


    /**
     * Store an image in 4 sizes.
     */
    void store(MultipartFile file, int pictureId);

    /**
     * Get a stored file by subdirectory and filename within that subdirectory.
     *
     * @param subDir - section of the data storage.
     * @param path - name of file.
     */
    StoredFile getByPath(String subDir, String path);

    /**
     * Delete all uploaded pictures. Only for test purposes.
     */
    void deleteAll();

    /**
     * Delete a picture by filename from all sub directories.
     *
     * @param filename - plain filename without '/' or '\\'.
     */
    void delete(String filename);

}
