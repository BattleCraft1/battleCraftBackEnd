package pl.edu.pollub.battleCraft.serviceLayer.services.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pollub.battleCraft.config.storage.StorageProperties;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.File.FileSearchedByRelatedEntityNameNotFound;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.File.StorageException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.File.StorageFileNotFoundException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Service
public class FileService {

    private final Path rootLocation;

    @Autowired
    public FileService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    public Path getRootLocation(){
        return rootLocation;
    }

    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    public void store(MultipartFile file, String name, String fileType) {
        try {
            if (file.isEmpty()) {
                throw new StorageException(new StringBuilder("Failed to store empty file ").append(file.getOriginalFilename()).toString());
            }
            if (file.getSize() / 1048576 > 6) {//6MB
                throw new StorageException(new StringBuilder("Failed to store file ").append(file.getOriginalFilename()).append(". File have size larger than 6MB ").toString());
            }

            Files.copy(file.getInputStream(), this.rootLocation.resolve(new StringBuilder(name).append(".").append(fileType).toString()));

        } catch (IOException e) {
            throw new StorageException(new StringBuilder("Failed to store file ").append(file.getOriginalFilename()).toString(), e);
        }
    }

    public void store(File file, String name, String fileType) {
        try {
            if (file.length() == 0) {
                throw new StorageException(new StringBuilder("Failed to store empty file ").append(file.getName()).toString());
            }
            if (file.length() / 1048576 > 6) {//6MB
                throw new StorageException(new StringBuilder("Failed to store file ").append(file.getName()).append(". File have size larger than 6MB ").toString());
            }
            Path filePath = this.rootLocation.resolve(new StringBuilder(name).append(".").append(fileType).toString());

            Files.copy(new FileInputStream(file),filePath);

        } catch (IOException e) {
            e.printStackTrace();
            throw new StorageException(new StringBuilder("Failed to store file ").append(file.getName()).toString(), e);
        }
    }

    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException(new StringBuilder("Could not read file: ").append(filename).toString());

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException(new StringBuilder("Could not read file: ").append(filename).toString(), e);
        }
    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());

    }

    public void delete(Path path) throws IOException {
        if (Files.exists(path))
            Files.delete(path);
        else
            throw new StorageFileNotFoundException(new StringBuilder("file of path: ").append(path.getFileName()).append(" not exist").toString());
    }

    public void deleteFilesRelatedWithEntities(String directoryName, List<String> entitiesUniqueNames){
        for(String entitiesUniqueName:entitiesUniqueNames){
            try {
                Path relatedFile = this.findFileByRelatedEntityName(entitiesUniqueName,directoryName);
                if(relatedFile.toFile().exists())
                this.delete(relatedFile);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void renameRelatedWithEntityFile(String directoryName, String previousName, String newName){
        try {
            Path relatedFilePath = this.findFileByRelatedEntityName(previousName,directoryName);
            File relatedFile = relatedFilePath.toFile();
            if(relatedFile.exists()){
                Path targetPath = Paths.get(new StringBuilder(this.getRootLocation().toString())
                        .append("/").append(directoryName).append("/").append(newName).append(".")
                        .append(relatedFilePath.toString().split("\\.")[1])
                        .toString());
                Files.move(relatedFilePath, targetPath);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public byte[] loadFileAsByteArray(String filename) throws IOException {
        return Files.readAllBytes(this.load(filename));
    }

    public byte[] loadFileRelatedEntityNameAsByteArray(String entityName,String directoryName) throws IOException, FileSearchedByRelatedEntityNameNotFound {
        return Files.readAllBytes(this.findFileByRelatedEntityName(entityName,directoryName));
    }

    private Path findFileByRelatedEntityName(String entityName, String entityRelatedFilesDirectoryName)
            throws FileSearchedByRelatedEntityNameNotFound {
        String entityRelatedFilesDirectoryPath =
                new StringBuilder(this.getRootLocation().toString())
                        .append("/")
                        .append(entityRelatedFilesDirectoryName)
                        .append("/").toString();
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

    public String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    public File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
