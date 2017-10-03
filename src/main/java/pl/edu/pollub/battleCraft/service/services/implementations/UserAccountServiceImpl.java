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

    private final int DEFAULT_USER_AVATAR_SIZE = 30;

    private final String DEFAULT_USER_AVATARS_DIRECTORY_NAME = "usersAvatars";

    private final String DEFAULT_USER_AVATAR_FILE_PATH = DEFAULT_USER_AVATARS_DIRECTORY_NAME+"/default.jpg";

    private final Dimension DEFAULT_USER_AVATAR_DIMENSION =
            new Dimension(this.DEFAULT_USER_AVATAR_SIZE,this.DEFAULT_USER_AVATAR_SIZE);

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
    public byte[] getUserAvatar(String name) throws IOException {
        File userAvatarFile;
        BufferedImage userAvatar;
        try{
            userAvatarFile = fileService.findFileByRelatedEntityName(name,DEFAULT_USER_AVATARS_DIRECTORY_NAME).toFile();
            userAvatar =imageService.resizeImageFromFile(userAvatarFile, DEFAULT_USER_AVATAR_DIMENSION);

            return imageService.convertBufferedImageToByteArray(userAvatar);

        }
        catch (Exception exception) {
            userAvatarFile = fileService.load(DEFAULT_USER_AVATAR_FILE_PATH).toFile();
            userAvatar =imageService.resizeImageFromFile(userAvatarFile, DEFAULT_USER_AVATAR_DIMENSION);

            return imageService.convertBufferedImageToByteArray(userAvatar);
        }
    }
}
