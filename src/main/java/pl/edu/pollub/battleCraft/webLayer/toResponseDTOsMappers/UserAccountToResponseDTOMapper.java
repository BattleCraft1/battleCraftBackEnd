package pl.edu.pollub.battleCraft.webLayer.toResponseDTOsMappers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.dataLayer.domain.ParticipantsGroup.ParticipantsGroup;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.Tournament.enums.TournamentType;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.Participation.Participation;
import pl.edu.pollub.battleCraft.dataLayer.domain.Play.Play;
import pl.edu.pollub.battleCraft.dataLayer.domain.Participation.nullObjectPattern.NullParticipation;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.serviceLayer.services.security.AuthorityRecognizer;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.UserAccount.UserAccountResponseDTO;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserAccountToResponseDTOMapper {

    private final AuthorityRecognizer authorityRecognizer;

    @Autowired
    public UserAccountToResponseDTOMapper(AuthorityRecognizer authorityRecognizer) {
        this.authorityRecognizer = authorityRecognizer;
    }

    public UserAccountResponseDTO map(String oldUserName,UserAccount userAccount){
        UserAccountResponseDTO userAccountResponseDTO = new UserAccountResponseDTO();
        userAccountResponseDTO.setName(userAccount.getName());

        userAccountResponseDTO.setNewToken(authorityRecognizer.createNewTokenIfUserChangeUsername(oldUserName,userAccount));

        userAccountResponseDTO.setCanCurrentUserEdit(authorityRecognizer.getCurrentUserNameFromContext().equals(oldUserName)
                || authorityRecognizer.getCurrentUserRoleFromContext().equals("ROLE_ADMIN"));
        userAccountResponseDTO.setNameChange(userAccount.getName());
        userAccountResponseDTO.setEmail(userAccount.getEmail());
        userAccountResponseDTO.setFirstname(userAccount.getFirstname());
        userAccountResponseDTO.setLastname(userAccount.getLastname());
        userAccountResponseDTO.setPhoneNumber(Optional.ofNullable(userAccount.getPhoneNumber()).orElse(""));
        userAccountResponseDTO.setProvince(userAccount.getAddressOwnership().getAddress().getProvince().name());
        userAccountResponseDTO.setCity(userAccount.getAddressOwnership().getAddress().getCity());
        userAccountResponseDTO.setStreet(userAccount.getAddressOwnership().getAddress().getStreet());
        userAccountResponseDTO.setZipCode(userAccount.getAddressOwnership().getAddress().getZipCode());
        userAccountResponseDTO.setDescription(Optional.ofNullable(userAccount.getAddressOwnership().getAddress().getDescription()).orElse(""));
        userAccountResponseDTO.setStatus(userAccount.getStatus().name());

        if(userAccount instanceof Player){
            Player player = (Player) userAccount;
            userAccountResponseDTO.setBanned(player.isBanned());
            userAccountResponseDTO.setPoints(player.getBattles().stream().mapToInt(Play::getPoints).sum());
            userAccountResponseDTO.setNumberOfBattles(player.getBattles().size());
            this.createParticipation(userAccountResponseDTO,player);
        }

        if(userAccount instanceof Organizer){
            Organizer organizer = (Organizer) userAccount;
            userAccountResponseDTO.setCreatedGames(organizer.getCreatedGames().stream().map(Game::getName).collect(Collectors.toList()));
            this.createOrganizations(userAccountResponseDTO,organizer);
        }

        return userAccountResponseDTO;
    }

    private void createParticipation(UserAccountResponseDTO userAccountResponseDTO, Player player){
        player.getParticipation().forEach(fistPlayerParticipation -> {
            Tournament tournament = fistPlayerParticipation.getParticipatedTournament();

            if(tournament.getTournamentType() == TournamentType.GROUP){
                Participation secondPlayerParticipation = tournament.getParticipation().stream()
                        .filter(participation -> ParticipantsGroup.checkIfParticipantsAreInTheSameGroup(fistPlayerParticipation,participation)
                                                  && !participation.getPlayer().equals(player))
                        .findFirst().orElse(new NullParticipation());

                userAccountResponseDTO.addParticipatedGroupTournament(tournament,fistPlayerParticipation,secondPlayerParticipation);
            }
            else{
                userAccountResponseDTO.addParticipatedDuelTournament(tournament,fistPlayerParticipation);
            }});
    }

    private void createOrganizations(UserAccountResponseDTO userAccountResponseDTO, Organizer organizer){
        organizer.getOrganizations().forEach(userAccountResponseDTO::addOrganizedTournament);
    }
}
