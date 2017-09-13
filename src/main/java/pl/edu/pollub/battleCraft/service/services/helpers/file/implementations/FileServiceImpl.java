package pl.edu.pollub.battleCraft.service.services.helpers.file.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pollub.battleCraft.config.StorageProperties;
import pl.edu.pollub.battleCraft.service.exceptions.UnCheckedExceptions.File.FileSearchedByRelatedEntityNameNotFound;
import pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.File.StorageException;
import pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.File.StorageFileNotFoundException;
import pl.edu.pollub.battleCraft.service.services.helpers.file.interfaces.FileService;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class FileServiceImpl implements FileService {

    private final Path rootLocation;

    @Autowired
    public FileServiceImpl(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public Path getRootLocation(){
        return rootLocation;
    }

    @Override
    public void store(MultipartFile file, String name, String fileType) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }
            if (file.getSize() / 1048576 > 20) {//20MB
                throw new StorageException("Failed to store file " + file.getOriginalFilename() + ". File have size larger than 20MB ");
            }

            Files.copy(file.getInputStream(), this.rootLocation.resolve(name + "." + fileType));


        } catch (IOException e) {
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());

    }

    @Override
    public void delete(Path path) throws IOException {
        if (Files.exists(path))
            Files.delete(path);

        throw new StorageFileNotFoundException("file of path: " + path.getFileName() + " not exist");
    }

    @Override
    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    public Path findFileByRelatedEntityName(String entityName, String entityRelatedFilesDirectoryName)
            throws FileSearchedByRelatedEntityNameNotFound {
        String entityRelatedFilesDirectoryPath =
                new StringBuilder(this.getRootLocation().toString())
                        .append("\\")
                        .append(entityRelatedFilesDirectoryName)
                        .append("\\").toString();
        File entityRelatedFilesDirectory = new File(entityRelatedFilesDirectoryPath);
        File[] files = entityRelatedFilesDirectory
                .listFiles((dir,name) -> Objects.equals(name.substring(0, name.indexOf(".")), entityName));
        if(files.length>0){
            String relatedToEntityFileName =
                    new StringBuilder(entityRelatedFilesDirectoryName)
                            .append("/")
                            .append(files[0].getName()).toString();
            return this.load(relatedToEntityFileName);
        }
        else{
            throw new FileSearchedByRelatedEntityNameNotFound(entityName);
        }
    }
}
