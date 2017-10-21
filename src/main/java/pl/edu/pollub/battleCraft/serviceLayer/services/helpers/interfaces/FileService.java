package pl.edu.pollub.battleCraft.serviceLayer.services.helpers.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UnCheckedExceptions.File.FileSearchedByRelatedEntityNameNotFound;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface FileService {
    String getFileExtension(File file);

    void init();

    void store(MultipartFile file, String name, String fileType);

    void deleteAll();

    Path load(String filename);

    byte[] loadFileAsByteArray(String filename) throws IOException;

    void store(File file, String name, String fileType);

    Resource loadAsResource(String filename);

    void delete(Path path) throws IOException;

    void deleteFilesReletedWithEntities(String direcotryName,String... entitiesUniqueNames);

    Path findFileByRelatedEntityName(String entityName, String entityRelatedFilesDirectoryName) throws FileSearchedByRelatedEntityNameNotFound;

    Path getRootLocation();

    File convertMultipartFileToFile(MultipartFile file) throws IOException;
}
