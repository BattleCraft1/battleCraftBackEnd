package pl.edu.pollub.battleCraft.serviceLayer.services.resources.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserAccountResourcesService {

    byte[] getUserAvatarWeb(String userId) throws IOException;

    byte[] getUserAvatarMobile(String userId) throws IOException;

    void deleteUsersAccountsAvatars(String... usersToDeleteUniqueNames) throws IOException;

    void saveUserAvatar(String username, MultipartFile file) throws IOException;

    byte[] getUserBigAvatar(String name) throws IOException;
}
