package pl.edu.pollub.battleCraft.serviceLayer.services.validators;

import org.springframework.stereotype.Component;
import pl.edu.pollub.battleCraft.serviceLayer.exceptions.UncheckedExceptions.PageOfEntities.OperationOnPageFailedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class UniqueNamesValidator{

    public void validateUniqueNamesElementsToDelete(List<String> validUniqueNames, String... uniqueNamesToValidate){
        List<String> notValidUniqueNames =
                this.validateUniqueNamesBeforeOperationOnDatabase(validUniqueNames,uniqueNamesToValidate);

        if(notValidUniqueNames.size()>0)
            throw new OperationOnPageFailedException(new StringBuilder("Elements: ").append(String.join(", ", notValidUniqueNames))
                            .append(" are not deleted because you are not owner of this elements").toString());
    }

    public void validateUniqueNamesElementsToAccept(List<String> validUniqueNames, String... uniqueNamesToValidate){
        List<String> notValidUniqueNames =
                this.validateUniqueNamesBeforeOperationOnDatabase(validUniqueNames,uniqueNamesToValidate);
        if(notValidUniqueNames.size()>0)
            throw new OperationOnPageFailedException(new StringBuilder("Elements: ").append(String.join(", ", notValidUniqueNames))
                            .append(" are not accepted because you can accept only new elements and not banned").toString());
    }

    public void validateUniqueNamesElementsToReject(List<String> validUniqueNames, String... uniqueNamesToValidate){
        List<String> notValidUniqueNames =
                this.validateUniqueNamesBeforeOperationOnDatabase(validUniqueNames,uniqueNamesToValidate);
        if(notValidUniqueNames.size()>0)
            throw new OperationOnPageFailedException(new StringBuilder("Elements: ").append(String.join(", ", notValidUniqueNames))
                            .append(" are not rejected because you can cancel acceptation only for accepted elements").toString());
    }

    public void validateUniqueNamesElementsToAdvance(List<String> validUniqueNames, String... uniqueNamesToValidate){
        List<String> notValidUniqueNames =
                this.validateUniqueNamesBeforeOperationOnDatabase(validUniqueNames,uniqueNamesToValidate);
        if(notValidUniqueNames.size()>0)
            throw new OperationOnPageFailedException(new StringBuilder("Users: ").append(String.join(", ", notValidUniqueNames))
                            .append(" are not advance to Organizer because if you want advance user to Organizer he must by a Accepted").toString());
    }

    public void validateUniqueNamesElementsToDegrade(List<String> validUniqueNames, String... uniqueNamesToValidate){
        List<String> notValidUniqueNames =
                this.validateUniqueNamesBeforeOperationOnDatabase(validUniqueNames,uniqueNamesToValidate);
        if(notValidUniqueNames.size()>0)
            throw new OperationOnPageFailedException(new StringBuilder("Users: ").append(String.join(", ", notValidUniqueNames))
                            .append(" are not degrade to Accepted because if you want degrade user to Accepted he must by a Organizer").toString());
    }

    private List<String> validateUniqueNamesBeforeOperationOnDatabase(List<String> validUniqueNames, String... uniqueNamesToValidate){
        List<String> notValidUniqueNames = new ArrayList<String>(Arrays.asList(uniqueNamesToValidate));
        notValidUniqueNames.removeAll(validUniqueNames);
        return notValidUniqueNames;
    }
}
