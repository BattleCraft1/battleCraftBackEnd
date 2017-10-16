package pl.edu.pollub.battleCraft.serviceLayer.services.resources.implementations;

import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.serviceLayer.services.helpers.implementations.FileServiceImpl;
import pl.edu.pollub.battleCraft.serviceLayer.services.helpers.interfaces.ImageService;
import pl.edu.pollub.battleCraft.serviceLayer.services.resources.interfaces.UserAccountResourcesService;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Service
public class UserAccountResourcesServiceImpl implements UserAccountResourcesService {

    private final String DEFAULT_USER_AVATARS_DIRECTORY_NAME = "usersAvatars";

    private final int DEFAULT_USER_AVATAR_SIZE_FOR_WEB = 40;

    private final int DEFAULT_USER_AVATAR_SIZE_FOR_MOBILE = 70;

    private final String DEFAULT_USER_AVATAR_FILE_PATH = "usersAvatars/default.jpg";

    private final Dimension DEFAULT_USER_AVATAR_DIMENSION_FOR_WEB =
            new Dimension(this.DEFAULT_USER_AVATAR_SIZE_FOR_WEB,this.DEFAULT_USER_AVATAR_SIZE_FOR_WEB);

    private final Dimension DEFAULT_USER_AVATAR_DIMENSION_FOR_MOBILE =
            new Dimension(this.DEFAULT_USER_AVATAR_SIZE_FOR_MOBILE,this.DEFAULT_USER_AVATAR_SIZE_FOR_MOBILE);

    private final FileServiceImpl fileService;

    private final ImageService imageService;

    public UserAccountResourcesServiceImpl(FileServiceImpl fileService, ImageService imageService) {
        this.fileService = fileService;
        this.imageService = imageService;
    }


    @Override
    public byte[] getUserAvatarWeb(String name)  throws IOException {
        return this.getUserAvatar(name,this.DEFAULT_USER_AVATAR_DIMENSION_FOR_WEB);
    }

    @Override
    public byte[] getUserAvatarMobile(String name)  throws IOException {
        return this.getUserAvatar(name,this.DEFAULT_USER_AVATAR_DIMENSION_FOR_MOBILE);
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

}
