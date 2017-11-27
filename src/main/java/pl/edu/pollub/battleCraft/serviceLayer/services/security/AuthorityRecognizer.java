package pl.edu.pollub.battleCraft.serviceLayer.services.security;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.GameRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.jpaRepositories.TournamentRepository;
import pl.edu.pollub.battleCraft.dataLayer.dao.pageOfEntity.search.criteria.SearchCriteria;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.Tournament;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.Tournament.enums.TournamentStatus;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.UserAccount;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Organizer.Organizer;
import pl.edu.pollub.battleCraft.dataLayer.domain.AddressOwner.User.subClasses.Player.Player;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.Game;
import pl.edu.pollub.battleCraft.dataLayer.domain.Game.enums.GameStatus;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ThisObjectIsBannedException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.ObjectStatus.ThisTournamentIsAlreadyStarted;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security.AnyRoleNotFoundException;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.Security.YouAreNotOwnerOfThisObjectException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AuthorityRecognizer {

    @PersistenceContext
    private EntityManager entityManager;

    private Authentication currentAuthentication;

    private final TournamentRepository tournamentRepository;

    private final GameRepository gameRepository;

    @Autowired
    public AuthorityRecognizer(TournamentRepository tournamentRepository, GameRepository gameRepository){
        this.tournamentRepository = tournamentRepository;
        this.gameRepository = gameRepository;
        currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
    }

    public String getCurrentUserRoleFromContext(){
        return currentAuthentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).findFirst().orElse("GUEST");
    }

    public String getCurrentUserNameFromContext(){
        return currentAuthentication.getName();
    }

    public String getCurrentUserRoleFromUserDetails(UserDetails userDetails){
        return userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .findFirst().orElseThrow(AnyRoleNotFoundException::new);
    }

    public String getCurrentUserRoleFromUserDetails(User userDetails){
        return userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .findFirst().orElseThrow(AnyRoleNotFoundException::new);
    }

    public void modifySearchCriteriaForCurrentUserRole(List<SearchCriteria> searchCriteria){
        String role = this.getCurrentUserRoleFromContext();
        if(!role.equals("ROLE_ADMIN")){
            searchCriteria.add(
                    new SearchCriteria(
                            Collections.singletonList("banned"),
                            ":",
                            Collections.singletonList(false)
                    )
            );
        }
    }

    public void modifySearchCriteriaForCurrentPlayer(List<SearchCriteria> searchCriteria){
        String username = this.getCurrentUserNameFromContext();

        searchCriteria.add(
                new SearchCriteria(
                        Collections.singletonList("participated by"),
                        ":",
                        Collections.singletonList(username)
                )
        );
    }

    public void modifySearchCriteriaForCurrentOrganizer(List<SearchCriteria> searchCriteria){
        String username = this.getCurrentUserNameFromContext();

        searchCriteria.add(
                new SearchCriteria(
                        Collections.singletonList("organized by"),
                        ":",
                        Collections.singletonList(username)
                )
        );
    }

    public List<String> getGamesNamesForCurrentUser(){
        String role = this.getCurrentUserRoleFromContext();
        if(!role.equals("ROLE_ADMIN")){
            return gameRepository.getAllAcceptedGamesNames();
        }
        return gameRepository.getAllGamesNames();
    }

    public void checkIfUserIsOrganizerOfTournament(Tournament tournament){
        String username = this.getCurrentUserNameFromContext();
        tournament.getOrganizations().stream()
                .map(organization -> organization.getOrganizer().getName())
                .filter(organizerName -> organizerName.equals(username))
                .findFirst().orElseThrow(() -> new YouAreNotOwnerOfThisObjectException(Tournament.class,tournament.getName()));
    }

    public void checkIfUserIsAdminOrOrganizerOfTournament(Tournament tournament){
        if(this.getCurrentUserRoleFromContext().equals("ROLE_ADMIN")) return;
        String username = this.getCurrentUserNameFromContext();
        tournament.getOrganizations().stream()
                .map(organization -> organization.getOrganizer().getName())
                .filter(organizerName -> organizerName.equals(username))
                .findFirst().orElseThrow(() -> new YouAreNotOwnerOfThisObjectException(Tournament.class,tournament.getName()));
    }

    public List<String> checkIfCurrentUserCanDeleteTournaments(String... tournamentsToDeleteUniqueNames){
        String role = this.getCurrentUserRoleFromContext();
        if(!role.equals("ROLE_ADMIN")){
            return this.checkIfUserIsOwnerOfTournaments(tournamentsToDeleteUniqueNames);
        }
        else {
            return tournamentRepository.selectTournamentsToDeleteUniqueNames(tournamentsToDeleteUniqueNames);
        }
    }

    private List<String> checkIfUserIsOwnerOfTournaments(String... tournamentsToCheck){
        String organizerName = this.getCurrentUserNameFromContext();
        Session hibernateSession = (Session) entityManager.getDelegate();
        Criteria criteria = hibernateSession.createCriteria(Organizer.class, "tournamentOwner");
        criteria.setProjection(Projections.distinct(Projections.property("name")));
        criteria.createAlias("organizations","organizations");
        criteria.createAlias("organizations.organizedTournament","organizedTournament");
        criteria.setProjection(Projections.groupProperty("name"));
        criteria.setProjection(Projections.groupProperty("organizedTournament.name"));
        criteria.add(Restrictions.in("organizedTournament.name",tournamentsToCheck));
        criteria.add(Restrictions.eq("name",organizerName));
        List<String> result = criteria.list();
        return result;
    }

    public void checkIfCurrentUserIsCreatorOfGame(Game game){
        String role = this.getCurrentUserRoleFromContext();

        if(!role.equals("ROLE_ADMIN")){
            if(game.isBanned() || game.getStatus()==GameStatus.ACCEPTED || !game.getCreator().getName().equals(this.getCurrentUserNameFromContext()))
                throw new YouAreNotOwnerOfThisObjectException(Game.class,game.getName());
        }
    }


    public void checkIfGameWithEditedRulesNeedReAcceptation(Game game){
        String role = this.getCurrentUserRoleFromContext();

        if(!role.equals("ROLE_ADMIN")){
            game.setStatus(GameStatus.NEW);
            this.gameRepository.save(game);
        }
    }

    public void checkIfCurrentUserIsOwnerOfAvatar(UserAccount user){
        String role = this.getCurrentUserRoleFromContext();

        if(!role.equals("ROLE_ADMIN")){
            if(!user.getName().equals(this.getCurrentUserNameFromContext()))
                throw new YouAreNotOwnerOfThisObjectException("Account",user.getName());
        }

    }

    public void checkIfCurrentUserIsOrganizerOfTournament(Tournament tournament){
        String role = this.getCurrentUserRoleFromContext();

        if(tournament.getStatus()!= TournamentStatus.ACCEPTED && tournament.getStatus()!=TournamentStatus.NEW)
            throw new ThisTournamentIsAlreadyStarted();

        if(!role.equals("ROLE_ADMIN")){
            if(tournament.isBanned())
                throw new ThisObjectIsBannedException(Tournament.class,tournament.getName());
            List<String> organizersNames = tournament.getOrganizations().stream()
                    .map(organization -> organization.getOrganizer().getName())
                    .collect(Collectors.toList());
            if(!organizersNames.contains(this.getCurrentUserNameFromContext()))
                throw new YouAreNotOwnerOfThisObjectException(Tournament.class,tournament.getName());
        }
    }

    public void checkIfEditedTournamentNeedReAcceptation(Tournament tournament){
        String role = this.getCurrentUserRoleFromContext();

        if(!role.equals("ROLE_ADMIN")){
            tournament.setStatus(TournamentStatus.NEW);
        }
    }

    public void checkIfCurrentUserIsOwnerOfAccount(UserAccount user){
        String role = this.getCurrentUserRoleFromContext();

        if(!role.equals("ROLE_ADMIN")){
            if(user instanceof Player){
                if(((Player)user).isBanned())
                    throw new ThisObjectIsBannedException(Player.class,user.getName());
            }

            if(!user.getName().equals(this.getCurrentUserNameFromContext()))
                throw new YouAreNotOwnerOfThisObjectException("Account",user.getName());
        }
    }
}
