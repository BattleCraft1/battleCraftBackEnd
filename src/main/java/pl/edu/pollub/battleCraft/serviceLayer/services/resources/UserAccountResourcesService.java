package pl.edu.pollub.battleCraft.serviceLayer.services.resources;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.UserAccountRepository;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ObjectNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.File.UserAvatar.InvalidUserAvatarExtension;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ThisObjectIsBannedException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security.YouAreNotOwnerOfThisObjectException;
import pl.edu.pollub.battleCraft.serviceLayer.services.file.FileService;
import pl.edu.pollub.battleCraft.serviceLayer.services.image.ImageService;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;

import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
public class UserAccountResourcesService {

    private final String DEFAULT_USER_AVATARS_DIRECTORY_NAME = "usersAvatars";

    private final int DEFAULT_USER_AVATAR_SIZE = 170;

    private final Dimension DEFAULT_USER_AVATAR_DIMENSION =
            new Dimension(this.DEFAULT_USER_AVATAR_SIZE,this.DEFAULT_USER_AVATAR_SIZE);

    private final String DEFAULT_USER_AVATAR_FILE_PATH = "usersAvatars/default.png";

    private final FileService fileService;

    private final ImageService imageService;

    private final UserAccountRepository userAccountRepository;

    private final AuthorityRecognizer authorityRecognizer;

    public UserAccountResourcesService(FileService fileService, ImageService imageService, UserAccountRepository userAccountRepository, AuthorityRecognizer authorityRecognizer) {
        this.fileService = fileService;
        this.imageService = imageService;
        this.userAccountRepository = userAccountRepository;
        this.authorityRecognizer = authorityRecognizer;
    }

    public byte[] getUserAvatar(String name) throws IOException {
        try{
            return fileService.loadFileRelatedEntityNameAsByteArray(name,DEFAULT_USER_AVATARS_DIRECTORY_NAME);

        }
        catch (Exception exception) {
            return fileService.loadFileAsByteArray(DEFAULT_USER_AVATAR_FILE_PATH);
        }
    }

    public void deleteUsersAccountsAvatars(String... usersToDeleteUniqueNames) throws IOException{
        fileService.deleteFilesRelatedWithEntities(DEFAULT_USER_AVATARS_DIRECTORY_NAME,usersToDeleteUniqueNames);
    }

    public void renameUserAvatar(String previousName,String newName) {
        fileService.renameRelatedWithEntityFile(DEFAULT_USER_AVATARS_DIRECTORY_NAME,previousName,newName);
    }

    public void saveUserAvatar(@NotNull @NotBlank String username,@NotNull @NotBlank MultipartFile file) throws IOException {
        UserAccount user = Optional.ofNullable(userAccountRepository.checkIfUserExist(username))
                .orElseThrow(() -> new ObjectNotFoundException(UserAccount.class,username));

        if(user instanceof Player){
            if(((Player) user).isBanned())
                throw new ThisObjectIsBannedException(Player.class,user.getName());
        }

        authorityRecognizer.checkIfCurrentUserIsOwnerOfAvatar(user);

        fileService.deleteFilesRelatedWithEntities(DEFAULT_USER_AVATARS_DIRECTORY_NAME,user.getName());

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
