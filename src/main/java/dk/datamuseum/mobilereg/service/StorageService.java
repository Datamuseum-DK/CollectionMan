package dk.datamuseum.mobilereg.service;

import java.io.OutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface for a storage service.
 * Implementations can then be based om files, Minio, etc.
 */
public interface StorageService {

    /**
     * Create subsections.
     * This is to be used by the clients of the service to coexist with other uses.
     *
     * @param section - name of the section. May not contain '/'
     */
    void init(String section);

    /**
     * Store a file in a section.
     *
     * @param section - name of the section. May not contain '/'
     */
    void store(MultipartFile file, String section);

    /**
     * Open a file as a stream.
     *
     * @param section - name of the section. May not contain '/'
     * @param filename - file name.
     * @return file as an output stream.
     */
    OutputStream getOutputHandle(String section, String filename);

    /**
     * Get path to a filename.
     *
     * @param section - name of the section. May not contain '/'
     * @param filename - file name.
     * @return file as an output stream.
     */
    Path load(String section, String filename);

    /**
     * Get file as a resource.
     *
     * @param section - name of the section. May not contain '/'
     * @param filename - file name.
     * @return file as a resource.
     */
    Resource loadAsResource(String section, String filename);

    /**
     * Delete all files in a section.
     *
     * @param section - name of the section. May not contain '/'
     */
    void deleteAll(String section);

    /**
     * Delete a file from storage.
     *
     * @param section - name of the section.
     * @param filename - filename in the section.
     */
    void delete(String section, String filename);
}
