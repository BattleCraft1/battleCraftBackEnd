package pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Invitation;

public class InvitationDTO {
    public String name;
    public boolean accepted;

    public InvitationDTO(String name, boolean accepted) {
        this.name = name;
        this.accepted = accepted;
    }
}
