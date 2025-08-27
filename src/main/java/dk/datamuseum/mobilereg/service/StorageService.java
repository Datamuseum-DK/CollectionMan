package dk.datamuseum.mobilereg.service;

import java.io.OutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    void init(String subDir);

    void store(MultipartFile file, String subDir);

    OutputStream getOutputHandle(String subDir, String fileName);

    Path load(String subDir, String filename);

    Resource loadAsResource(String subDir, String filename);

    void deleteAll(String subDir);

    /**
     * Delete a file from storage.
     *
     * @param subDir - subdirectory or partition.
     * @param filename - filename in the subdirectory.
     */
    void delete(String subDir, String filename);
}
