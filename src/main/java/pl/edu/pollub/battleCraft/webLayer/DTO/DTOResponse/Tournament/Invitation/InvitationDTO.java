package pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Tournament.Invitation;

public class InvitationDTO {
    public String invitedUserName;
    public boolean accepted;

    public InvitationDTO(String invitedUserName, boolean accepted) {
        this.invitedUserName = invitedUserName;
        this.accepted = accepted;
    }
}
