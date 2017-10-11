package pl.edu.pollub.battleCraft.serviceLayer.services.resources.interfaces;

import java.io.IOException;

public interface UserAccountResourcesService {

    byte[] getUserAvatarWeb(String userId) throws IOException;

    byte[] getUserAvatarMobile(String userId) throws IOException;

    void deleteUsersAccountsAvatars(String... usersToDeleteUniqueNames) throws IOException;
}
