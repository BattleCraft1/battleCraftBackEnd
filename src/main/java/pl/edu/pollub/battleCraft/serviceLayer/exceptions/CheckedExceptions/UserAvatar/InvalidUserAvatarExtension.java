package pl.edu.pollub.battleCraft.serviceLayer.exceptions.CheckedExceptions.UserAvatar;

public class InvalidUserAvatarExtension extends RuntimeException{
    public InvalidUserAvatarExtension(String extension) {
        super(new StringBuilder("Extension: ").append(extension).append(" is not acceptable extension of user avatar. You should try with jpg, gif, bmp or png").toString());
    }
}
