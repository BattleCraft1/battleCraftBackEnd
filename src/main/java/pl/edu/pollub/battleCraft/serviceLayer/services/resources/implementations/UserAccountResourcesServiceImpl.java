package pl.edu.pollub.battleCraft.serviceLayer.services.resources.implementations;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.UserAccountRepository;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.UserAvatar.InvalidUserAvatarExtension;
import pl.edu.pollub.battleCraft.serviceLayer.services.helpers.implementations.FileServiceImpl;
import pl.edu.pollub.battleCraft.serviceLayer.services.helpers.interfaces.ImageService;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.interfaces.UserAccountResourcesService;

import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
public class UserAccountResourcesServiceImpl implements UserAccountResourcesService {

    private final String DEFAULT_USER_AVATARS_DIRECTORY_NAME = "usersAvatars";

    private final int DEFAULT_USER_AVATAR_SIZE = 170;

    private final Dimension DEFAULT_USER_AVATAR_DIMENSION =
            new Dimension(this.DEFAULT_USER_AVATAR_SIZE,this.DEFAULT_USER_AVATAR_SIZE);

    private final String DEFAULT_USER_AVATAR_FILE_PATH = "usersAvatars/default.png";

    private final FileServiceImpl fileService;

    private final ImageService imageService;

    private final UserAccountRepository userAccountRepository;

    public UserAccountResourcesServiceImpl(FileServiceImpl fileService, ImageService imageService, UserAccountRepository userAccountRepository) {
        this.fileService = fileService;
        this.imageService = imageService;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public byte[] getUserAvatar(String name) throws IOException {
        try{
            return fileService.loadFileRelatedEntityNameAsByteArray(name,DEFAULT_USER_AVATARS_DIRECTORY_NAME);

        }
        catch (Exception exception) {
            return fileService.loadFileAsByteArray(DEFAULT_USER_AVATAR_FILE_PATH);
        }
    }

    @Override
    public void deleteUsersAccountsAvatars(String... usersToDeleteUniqueNames) throws IOException{
        fileService.deleteFilesRelatedWithEntities(DEFAULT_USER_AVATARS_DIRECTORY_NAME,usersToDeleteUniqueNames);
    }

    @Override
    public void saveUserAvatar(@NotNull @NotBlank String username,@NotNull @NotBlank MultipartFile file) throws IOException {
        String name = Optional.ofNullable(userAccountRepository.checkIfUserExist(username))
                .orElseThrow(() -> new EntityNotFoundException(UserAccount.class,username));

        fileService.deleteFilesRelatedWithEntities(DEFAULT_USER_AVATARS_DIRECTORY_NAME,name);

        String extension = file.getOriginalFilename().split("\\.")[1];

        if(!extension.equals("bmp") && !extension.equals("gif") && !extension.equals("jpg") && !extension.equals("jpeg") && !extension.equals("png"))
            throw new InvalidUserAvatarExtension(extension);

        BufferedImage userAvatar =imageService.resizeImageFromFile(
                fileService.convertMultipartFileToFile(file),
                DEFAULT_USER_AVATAR_DIMENSION);

        File userAvatarFile = new File(username);
        ImageIO.write(userAvatar, extension, userAvatarFile);

        fileService.store(userAvatarFile,new StringBuilder(DEFAULT_USER_AVATARS_DIRECTORY_NAME).append("/").append(username).toString(),extension);
    }

}
