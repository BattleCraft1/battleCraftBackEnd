package pl.edu.pollub.battleCraft.service.exceptions.CheckedExceptions.TournamentOrganization;

public class TooManyInvitedParticipants extends RuntimeException{
    public TooManyInvitedParticipants(int maxPlayers, int participantsCount){
        super(new StringBuilder("You cannot invite ")
                .append(participantsCount)
                .append(" because players limit in your tournament is set on ")
                .append(maxPlayers)
                .toString());
    }
}
