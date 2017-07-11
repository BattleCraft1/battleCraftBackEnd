package pl.edu.pollub.battleCraft.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileService {
    void init();
    void store(MultipartFile file, String name, String fileType);
    void deleteAll();
    Path load(String filename);
    Resource loadAsResource(String filename);
    void delete(Path path) throws IOException;
}
