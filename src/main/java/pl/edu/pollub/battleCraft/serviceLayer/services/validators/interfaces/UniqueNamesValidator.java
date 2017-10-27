package pl.edu.pollub.battleCraft.serviceLayer.services.validators.interfaces;


import java.util.List;

public interface UniqueNamesValidator {
    void validateUniqueNamesElementsToDelete(List<String> validUniqueNames, String... uniqueNamesToValidate);
    void validateUniqueNamesElementsToAccept(List<String> validUniqueNames, String... uniqueNamesToValidate);
    void validateUniqueNamesElementsToReject(List<String> validUniqueNames, String... uniqueNamesToValidate);
    void validateUniqueNamesElementsToAdvance(List<String> validUniqueNames, String... uniqueNamesToValidate);
    void validateUniqueNamesElementsToDegrade(List<String> validUniqueNames, String... uniqueNamesToValidate);
}
