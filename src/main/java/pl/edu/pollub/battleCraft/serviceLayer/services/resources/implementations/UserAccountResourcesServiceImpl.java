package pl.edu.pollub.battleCraft.serviceLayer.services.resources.implementations;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.entities.User.subClasses.players.Player;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.PlayerRepository;
import pl.edu.pollub.battleCraft.dataLayer.repositories.interfaces.UserAccountRepository;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.UserAvatar.InvalidUserAvatarExtension;
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

    private final int DEFAULT_USER_AVATAR_SIZE_FOR_WEB = 80;

    private final int DEFAULT_BIG_USER_AVATAR_SIZE = 170;

    private final int DEFAULT_USER_AVATAR_SIZE_FOR_MOBILE = 70;

    private final String DEFAULT_USER_AVATAR_FILE_PATH = "usersAvatars/default.jpg";

    private final Dimension DEFAULT_USER_AVATAR_DIMENSION_FOR_WEB =
            new Dimension(this.DEFAULT_USER_AVATAR_SIZE_FOR_WEB,this.DEFAULT_USER_AVATAR_SIZE_FOR_WEB);

    private final Dimension DEFAULT_BIG_USER_AVATAR_DIMENSION =
            new Dimension(this.DEFAULT_BIG_USER_AVATAR_SIZE,this.DEFAULT_BIG_USER_AVATAR_SIZE);

    private final Dimension DEFAULT_USER_AVATAR_DIMENSION_FOR_MOBILE =
            new Dimension(this.DEFAULT_USER_AVATAR_SIZE_FOR_MOBILE,this.DEFAULT_USER_AVATAR_SIZE_FOR_MOBILE);

    private final FileServiceImpl fileService;

    private final ImageService imageService;

    private final PlayerRepository playerRepository;

    public UserAccountResourcesServiceImpl(FileServiceImpl fileService, ImageService imageService, PlayerRepository playerRepository) {
        this.fileService = fileService;
        this.imageService = imageService;
        this.playerRepository = playerRepository;
    }


    @Override
    public byte[] getUserAvatarWeb(String name)  throws IOException {
        return this.getUserAvatar(name,this.DEFAULT_USER_AVATAR_DIMENSION_FOR_WEB);
    }

    @Override
    public byte[] getUserAvatarMobile(String name)  throws IOException {
        return this.getUserAvatar(name,this.DEFAULT_USER_AVATAR_DIMENSION_FOR_MOBILE);
    }

    @Override
    public byte[] getUserBigAvatar(String name)  throws IOException {
        return this.getUserAvatar(name,this.DEFAULT_BIG_USER_AVATAR_DIMENSION);
    }

    private byte[] getUserAvatar(String name, Dimension dimension) throws IOException {
        File userAvatarFile;
        BufferedImage userAvatar;
        String fileExtension;
        try{
            userAvatarFile = fileService.findFileByRelatedEntityName(name,DEFAULT_USER_AVATARS_DIRECTORY_NAME).toFile();
            userAvatar =imageService.resizeImageFromFile(userAvatarFile, dimension);
            fileExtension = fileService.getFileExtension(userAvatarFile);
            return imageService.convertBufferedImageToByteArray(userAvatar,fileExtension);

        }
        catch (Exception exception) {
            userAvatarFile = fileService.load(DEFAULT_USER_AVATAR_FILE_PATH).toFile();
            userAvatar =imageService.resizeImageFromFile(userAvatarFile, dimension);
            fileExtension = fileService.getFileExtension(userAvatarFile);
            return imageService.convertBufferedImageToByteArray(userAvatar,fileExtension);
        }
    }

    @Override
    public void deleteUsersAccountsAvatars(String... usersToDeleteUniqueNames) throws IOException{
        fileService.deleteFilesReletedWithEntities(DEFAULT_USER_AVATARS_DIRECTORY_NAME,usersToDeleteUniqueNames);
    }

    @Override
    public void saveUserAvatar(@NotNull @NotBlank String username,@NotNull @NotBlank MultipartFile file) throws IOException {
        String name = Optional.ofNullable(playerRepository.checkIfPlayerExist(username))
                .orElseThrow(() -> new EntityNotFoundException(UserAccount.class,username));
        if(username.equals("default"))
            throw new EntityNotFoundException(UserAccount.class,username);

        String extension = file.getOriginalFilename().split("\\.")[1];

        if(!extension.equals("bmp") && !extension.equals("gif") && !extension.equals("jpg") && !extension.equals("jpeg") && !extension.equals("png"))
            throw new InvalidUserAvatarExtension(extension);

        BufferedImage userAvatar =imageService.resizeImageFromFile(
                fileService.convertMultipartFileToFile(file),
                DEFAULT_BIG_USER_AVATAR_DIMENSION);

        File userAvatarFile = new File(username);
        ImageIO.write(userAvatar, extension, userAvatarFile);

        fileService.store(userAvatarFile,new StringBuilder(DEFAULT_USER_AVATARS_DIRECTORY_NAME).append("/").append(username).toString(),extension);
    }

}
