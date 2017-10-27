package pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.File.UserAvatar;

public class InvalidUserAvatarExtension extends RuntimeException{
    public InvalidUserAvatarExtension(String extension) {
        super(new StringBuilder("Extension: ").append(extension).append(" is not acceptable extension as game rules. You should try with pdf").toString());
    }
}
