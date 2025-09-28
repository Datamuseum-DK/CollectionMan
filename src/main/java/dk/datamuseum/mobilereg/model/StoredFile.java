package dk.datamuseum.mobilereg.model;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

/**
 * Management and metadata for uploaded files. Contains original file name
 * lastModified date etc.
 */
public class StoredFile {

    private String subDir;
    /** Filename without directory. */
    private String filename;

    private MediaType contentType;

    private long fileSize;

    private Resource content;

    public String getSubDir() {
        return subDir;
    }

    public void setSubDir(String subDir) {
        this.subDir = subDir;
    }

    /**
     * Get filename without directory.
     */
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public MediaType getContentType() {
        return contentType;
    }

    public void setContentType(MediaType contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return fileSize;
    }

    public void setSize(final long size) {
        this.fileSize = size;
    }

    /**
     * Get content as a Resource.
     * This type is an abstraction from an underlying resource, such as a file or class path resource.
     * @see <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/io/Resource.html">Interface Resource</a>
     */
    public Resource getContentAsResource() {
        return content;
    }

    /**
     * Set content as a Resource.
     *
     * @param content - the file content.
     */
    public void setContentResource(final Resource content) {
        this.content = content;
    }
}
