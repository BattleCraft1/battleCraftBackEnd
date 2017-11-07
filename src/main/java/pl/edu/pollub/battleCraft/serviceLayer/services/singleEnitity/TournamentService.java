package pl.edu.pollub.battleCraft.serviceLayer.services.singleEnitity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import pl.edu.pollub.battleCraft.dataLayer.domain.Address.Address;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.builder.TournamentCreator;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.builder.TournamentEditor;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.*;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.EntityValidation.EntityValidationException;
import pl.edu.pollub.battleCraft.serviceLayer.services.validators.TournamentValidator;
import pl.edu.pollub.battleCraft.serviceLayer.toResponseDTOsMappers.TournamentToResponseDTOMapper;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTORequest.Tournament.TournamentRequestDTO;
import pl.edu.pollub.battleCraft.webLayer.DTO.DTOResponse.Tournament.TournamentResponseDTO;

import java.util.Optional;

@Service
public class TournamentService {
    private final TournamentRepository tournamentRepository;

    private final TournamentValidator tournamentValidator;

    private final TournamentCreator tournamentCreator;

    private final TournamentEditor tournamentEditor;

    private final TournamentToResponseDTOMapper tournamentToResponseDTOMapper;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository, TournamentValidator tournamentValidator, TournamentCreator tournamentCreator, TournamentEditor tournamentEditor, TournamentToResponseDTOMapper tournamentToResponseDTOMapper) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentValidator = tournamentValidator;
        this.tournamentCreator = tournamentCreator;
        this.tournamentEditor = tournamentEditor;
        this.tournamentToResponseDTOMapper = tournamentToResponseDTOMapper;
    }

    @Transactional(rollbackFor = {EntityValidationException.class,EntityNotFoundException.class})
    public TournamentResponseDTO organizeTournament(TournamentRequestDTO tournamentWebDTO, BindingResult bindingResult) throws EntityValidationException {

        tournamentValidator.checkIfTournamentExist(tournamentWebDTO,bindingResult);
        tournamentValidator.validate(tournamentWebDTO,bindingResult);
        Game tournamentGame = tournamentValidator.getValidatedGame(tournamentWebDTO,bindingResult);
        Organizer[] organizers = tournamentValidator.getValidatedOrganizers(tournamentWebDTO,bindingResult);
        Player[] participants = tournamentValidator.getValidatedParticipants(tournamentWebDTO,bindingResult);

        tournamentValidator.finishValidation(bindingResult);

        Tournament organizedTournament = tournamentCreator
                .startOrganizeTournament(
                        tournamentWebDTO.getName(),
                        tournamentWebDTO.getTablesCount(),
                        tournamentWebDTO.getPlayersOnTableCount(),
                        tournamentWebDTO.getToursCount())
                .with(organizers)
                .in(new Address(
                        tournamentWebDTO.getProvince(),
                        tournamentWebDTO.getCity(),
                        tournamentWebDTO.getStreet(),
                        tournamentWebDTO.getZipCode(),
                        tournamentWebDTO.getDescription()))
                .withGame(tournamentGame)
                .startAt(tournamentWebDTO.getDateOfStart())
                .endingIn(tournamentWebDTO.getDateOfEnd())
                .inviteParticipants(participants)
                .finishOrganize();

        return tournamentToResponseDTOMapper.map(this.tournamentRepository.save(organizedTournament));
    }

    @Transactional(rollbackFor = {EntityValidationException.class,EntityNotFoundException.class})
    public TournamentResponseDTO editTournament(TournamentRequestDTO tournamentWebDTO, BindingResult bindingResult){
        //TO DO: check if this organizer is organizer of this tournament

        Tournament tournamentToEdit = tournamentValidator.getValidatedTournamentToEdit(tournamentWebDTO, bindingResult);

        tournamentValidator.checkIfTournamentToEditExist(tournamentWebDTO,bindingResult);
        tournamentValidator.validate(tournamentWebDTO,bindingResult);
        Game tournamentGame = tournamentValidator.getValidatedGame(tournamentWebDTO,bindingResult);
        Organizer[] organizers = tournamentValidator.getValidatedOrganizers(tournamentWebDTO,bindingResult);
        Player[] participants = tournamentValidator.getValidatedParticipants(tournamentWebDTO,bindingResult);

        tournamentValidator.finishValidation(bindingResult);

        tournamentEditor.editOrganizedTournament(
                        tournamentToEdit,
                        tournamentWebDTO.getNameChange(),
                        tournamentWebDTO.getTablesCount(),
                        tournamentWebDTO.getPlayersOnTableCount(),
                        tournamentWebDTO.getToursCount())
                .editOrganizers(organizers)
                .changeAddress(
                        tournamentWebDTO.getProvince(),
                        tournamentWebDTO.getCity(),
                        tournamentWebDTO.getStreet(),
                        tournamentWebDTO.getZipCode(),
                        tournamentWebDTO.getDescription())
                .withGame(tournamentGame)
                .startAt(tournamentWebDTO.getDateOfStart())
                .endingIn(tournamentWebDTO.getDateOfEnd())
                .editParticipants(participants)
                .finishEditing();

        return tournamentToResponseDTOMapper.map(this.tournamentRepository.save(tournamentToEdit));
    }

    public TournamentResponseDTO getTournament(String tournamentUniqueName) {
        Tournament tournamentToShow = Optional.ofNullable(tournamentRepository.findTournamentToEditByUniqueName(tournamentUniqueName))
                .orElseThrow(() -> new EntityNotFoundException(Tournament.class,tournamentUniqueName));

        return tournamentToResponseDTOMapper.map(tournamentToShow);
    }
}
