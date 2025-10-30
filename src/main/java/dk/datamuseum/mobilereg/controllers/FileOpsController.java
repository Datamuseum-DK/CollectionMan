package dk.datamuseum.mobilereg.controllers;

import dk.datamuseum.mobilereg.model.StoredFile;
import dk.datamuseum.mobilereg.service.PictureService;
import java.io.IOException;
import java.time.Instant;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * File operations - upload, download, delete.
 * @see <a href="http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/multipart/MultipartFile.html">Spring MultipartFile</a>.
 */
@Slf4j
@Controller
public class FileOpsController {

    private final PictureService pictureService;

    /**
     * Constructor.
     */
    public FileOpsController(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    /**
     * Download a file.
     *
     * @param subDir - e.g. "original", "medium", etc.
     * @param pathName - The name of the file.
     * FIXME: Set image type based on suffix.
     */
    @RequestMapping(value = "/media/{directory}/{file_name}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadFile(
        @PathVariable("directory") String subDir,
        @PathVariable("file_name") String pathName) throws NotFoundException {

        log.debug("Download: {}/{}", subDir, pathName);

        StoredFile pictureRec = pictureService.getByPath(subDir, pathName);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + pictureRec.getFilename());

        Resource resource = pictureRec.getContentAsResource();
        long lastModified = 0;
        long contentLength = 0;
        try {
            lastModified = resource.lastModified();
        } catch (IOException ex) {
            lastModified = Instant.now().getEpochSecond();
        }
        try {
            contentLength = resource.contentLength();
        } catch (IOException ex) {
            throw new NotFoundException(0);
        }
        return ResponseEntity.ok()
            .headers(headers)
            .lastModified(lastModified)
            .contentLength(contentLength)
            .contentType(pictureRec.getContentType())
            .body(resource);
    }

}
