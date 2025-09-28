package dk.datamuseum.mobilereg.service;

import org.springframework.web.multipart.MultipartFile;

import dk.datamuseum.mobilereg.model.StoredFile;

/**
 * Service to store pictures and return in 4 sizes.
 * When a picture is uploaded and saved with the store() method, then the
 * picture is resized to several different sizes.
 * Then the client should request an image based on the size, so that
 * the most efficient size is returned.
 */
public interface PictureService {

    String PICTUREORIGINAL = "pictureoriginal";
    String PICTUREMEDIUM = "picturemedium";
    String PICTURELOW = "picturelow";
    String[] subDirs = { PICTURELOW, PICTUREMEDIUM, PICTUREORIGINAL };
    int[] maxDims = { 150, 640, 2048, 40 };


    /**
     * Store an image in 4 sizes.
     *
     * @param file - The file to store
     * @param pictureId the identifier in the database for the file.
     */
    void store(MultipartFile file, int pictureId);

    /**
     * Get a stored file by subdirectory and filename within that subdirectory.
     *
     * @param subDir - section of the data storage.
     * @param path - name of file.
     * @return the file with meta data.
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
