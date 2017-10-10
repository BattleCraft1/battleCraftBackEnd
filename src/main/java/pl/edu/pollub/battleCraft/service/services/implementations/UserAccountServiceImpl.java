package pl.edu.pollub.battleCraft.service.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.edu.pollub.battleCraft.data.entities.User.subClasses.enums.UserType;
import pl.edu.pollub.battleCraft.data.repositories.extensions.interfaces.ExtendedUserAccountRepository;
import pl.edu.pollub.battleCraft.data.repositories.helpers.searchSpecyficators.SearchCriteria;
import pl.edu.pollub.battleCraft.service.services.helpers.file.implementations.FileServiceImpl;
import pl.edu.pollub.battleCraft.service.services.helpers.image.interfaces.ImageService;
import pl.edu.pollub.battleCraft.service.services.interfaces.UserAccountService;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class UserAccountServiceImpl implements UserAccountService {
    private final ExtendedUserAccountRepository userAccountRepository;

    private final FileServiceImpl fileService;

    private final ImageService imageService;

    private final int DEFAULT_USER_AVATAR_SIZE_FOR_WEB = 30;

    private final int DEFAULT_USER_AVATAR_SIZE_FOR_MOBILE = 70;

    private final String DEFAULT_USER_AVATARS_DIRECTORY_NAME = "usersAvatars";

    private final String DEFAULT_USER_AVATAR_FILE_PATH = "usersAvatars/default.jpg";

    private final Dimension DEFAULT_USER_AVATAR_DIMENSION_FOR_WEB =
            new Dimension(this.DEFAULT_USER_AVATAR_SIZE_FOR_WEB,this.DEFAULT_USER_AVATAR_SIZE_FOR_WEB);

    private final Dimension DEFAULT_USER_AVATAR_DIMENSION_FOR_MOBILE =
            new Dimension(this.DEFAULT_USER_AVATAR_SIZE_FOR_MOBILE,this.DEFAULT_USER_AVATAR_SIZE_FOR_MOBILE);

    @Autowired
    public UserAccountServiceImpl(ExtendedUserAccountRepository userAccountRepository,
                                  FileServiceImpl fileService,
                                  ImageService imageService) {
        this.userAccountRepository = userAccountRepository;
        this.fileService = fileService;
        this.imageService = imageService;
    }

    @Override
    public Page getPageOfUserAccounts(Pageable requestedPage,
                                      List<SearchCriteria> searchCriteria) {
        return userAccountRepository.getPageOfUserAccounts(searchCriteria, requestedPage);
    }

    @Override
    public void banUsersAccounts(String... usersToBanUniqueNames) {
        userAccountRepository.banUsersAccounts(usersToBanUniqueNames);
    }

    @Override
    public void unlockUsersAccounts(String... usersToUnlockUniqueNames) {
        userAccountRepository.unlockUsersAccounts(usersToUnlockUniqueNames);
    }

    @Override
    public void deleteUsersAccounts(String... usersToDeleteUniqueNames) throws IOException {
        userAccountRepository.deleteUsersAccounts(usersToDeleteUniqueNames);
        fileService.deleteFilesReletedWithEntities(DEFAULT_USER_AVATARS_DIRECTORY_NAME,usersToDeleteUniqueNames);
    }

    @Override
    public void acceptUsersAccounts(String... usersToAcceptUniqueNames) {
        userAccountRepository.acceptUsersAccounts(usersToAcceptUniqueNames);
    }

    @Override
    public void cancelAcceptUsersAccounts(String... usersToCancelAcceptUniqueNames) {
        userAccountRepository.cancelAcceptUsersAccounts(usersToCancelAcceptUniqueNames);
    }

    @Override
    public void advancePlayersToOrganizer(String... playersToAdvanceToOrganizersUniqueNames) {
        userAccountRepository.advancePlayersToOrganizer(playersToAdvanceToOrganizersUniqueNames);
    }

    @Override
    public void degradeOrganizerToPlayers(String... organizerToDegradeToPlayersUniqueNames) {
        userAccountRepository.degradeOrganizerToPlayers(organizerToDegradeToPlayersUniqueNames);
    }

    @Override
    public List<String> getAllUserTypes() {
        return UserType.getNames();
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
            exception.printStackTrace();
            userAvatarFile = fileService.load(DEFAULT_USER_AVATAR_FILE_PATH).toFile();
            userAvatar =imageService.resizeImageFromFile(userAvatarFile, dimension);
            fileExtension = fileService.getFileExtension(userAvatarFile);
            return imageService.convertBufferedImageToByteArray(userAvatar,fileExtension);
        }
    }
}
