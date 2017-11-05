package pl.edu.pollub.battleCraft.serviceLayer.services.resources.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserAccountResourcesService {

    byte[] getUserAvatar(String name) throws IOException;

    void deleteUsersAccountsAvatars(String... usersToDeleteUniqueNames) throws IOException;

    void renameUserAvatar(String previousName, String newName);

    void saveUserAvatar(String username, MultipartFile file) throws IOException;

}
