package com.ardaslegends.data.service.exceptions;
import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.domain.RPChar;
import com.ardaslegends.data.domain.Region;
import org.springframework.web.client.RestClientException;
import javax.persistence.PersistenceException;
import javax.validation.constraints.NotNull;

public class ServiceException extends RuntimeException {

    private static final String NO_REGIONS_TO_VISIT = "Encountered Error in pathfinder, no more regions to visit! Start region [%s], End region [%s]";

    // Create
    private static final String CANNOT_CREATE_DUE_TO_DATABASE_PROBLEMS_WITH_NULL_ENTITY = "Cannot create entity due to database problems!";
    private static final String CANNOT_CREATE_ENTITY_DUE_TO_DATABASE_PROBLEMS = "Cannot create entity of type %s (%s) due to database problems!";
    public static final String CANNOT_CREATE_ENTITY_DUE_TO_IT_ALREADY_EXISTING = "Cannot create entity of type %s (%s) due to it already existing!";

    // Read
    public static final String CANNOT_READ_ENTITY_DUE_TO_DATABASE_PROBLEMS = "Cannot read entity of type %s (%s) due to database problems!";
    public static final String CANNOT_READ_ENTITY_DUE_TO_DATABASE_PROBLEMS_NULL_ENTITY = "Cannot read entity of due to database problems!";
    public static final String CANNOT_READ_ENTITY_BECAUSE_OF_NO_EXISTING_RECORD = "No record of type %s found with %s (%s)!";

    public static final String CANNOT_READ_ENTITY_DUE_TO_EXTERNAL_MOJANG_API_ERROR = "External Mojang Api Error, message: %s";

    // Update

    private static final String CANNOT_SAVE_DUE_TO_DATABASE_PROBLEMS_WITH_NULL_ENTITY = "Cannot save entity due to database problems!";

    private static final String CANNOT_SAVE_ENTITY_DUE_TO_DATABASE_PROBLEMS = "Cannot save entity of type %s (%s) due to database problems!";

    // Delete
    private static final String CANNOT_DELETE_DUE_TO_DATABASE_PROBLEMS_WITH_NULL_ENTITY = "Cannot delete entity due to database problems!";
    private static final String CANNOT_DELETE_ENTITY_DUE_TO_DATABASE_PROBLEMS = "Cannot delete entity of type %s (%s) due to database problems!";

    // Utils

    private static final String NO_SUCH_FIELD = "No field with the name '%s'!";
    private static final String ILLEGAL_ACCESS_FIELD = "Illegal access on field '%s'!";

    private static final String PASSED_FUNCTION_NULL = "Passed function on secureFind method is null!";
    private static final String SECURE_FIND_FAILED_BECAUSE_OF_DATABASE_PROBLEM = "Cannot find entity due to database problem! Identifier [%s]";
    private static final String SECURE_FIND_FAILED_BECAUSE_OF_DATABASE_PROBLEM_NULL_IDENTIFIER = "Cannot find entity due to database problem! Identifier was null!";

    //Movements

    private static final String CANNOT_MOVE_RPCHAR_DUE_TO_ALREADY_IN_REGION = "The Character '%s' is already in region %s!";

    private static final String CANNOT_MOVE_RPCHAR_DUE_BOUND_TO_ARMY = "Cannot move the Rp Char '%s' because it is bound to the army '%s'!";
    private static final String CANNOT_MOVE_RPCHAR_DUE_TO_ALREADY_MOVING = "Cannot move the Rp Char '%s' because it is already in another movement! Cancel the other movement first if you want to move this character.";
    private static final String NO_ACTIVE_MOVEMENT = "There are no active movements for the character '%s'!";
    private static final String MORE_THAN_ONE_ACTIVE_MOVEMENT = "Found more than one active movement for character '%s' - please contact the devs!";

    //Player

    private static final String NO_RP_CHAR = "You have no Roleplay Character!";
    private static final String CREATE_RP_CHAR_NO_FACTION = "Player %s is in no faction - cannot create Roleplay Character!";
    public static ServiceException cannotReadEntityDueToExternalMojangError(RestClientException ex) {
        String msg = CANNOT_READ_ENTITY_DUE_TO_EXTERNAL_MOJANG_API_ERROR.formatted(ex.getMessage());
        return new ServiceException(msg, ex);
    }

    public static <T> ServiceException cannotCreateEntity(T entity, PersistenceException pEx) {
        String msg = (entity == null)
                ? CANNOT_CREATE_DUE_TO_DATABASE_PROBLEMS_WITH_NULL_ENTITY
                : CANNOT_CREATE_ENTITY_DUE_TO_DATABASE_PROBLEMS.formatted(entity.getClass().getSimpleName(), entity.toString());
        return new ServiceException(msg, pEx);
    }

    public static <T> ServiceException cannotCreateEntityThatAlreadyExists(T entity) {
        String msg = CANNOT_CREATE_ENTITY_DUE_TO_IT_ALREADY_EXISTING.formatted(entity.getClass().getSimpleName(), entity.toString());
        return new ServiceException(msg);
    }

    public static <T> ServiceException cannotReadEntityDueToDatabase(T entity, PersistenceException pEx) {
        String msg = (entity == null)
                ? CANNOT_READ_ENTITY_DUE_TO_DATABASE_PROBLEMS_NULL_ENTITY
                : CANNOT_READ_ENTITY_DUE_TO_DATABASE_PROBLEMS.formatted(entity.getClass().getSimpleName(), entity.toString());
        return new ServiceException(msg, pEx);
    }

    public static <T> ServiceException cannotReadEntityDueToNotExisting(String className, String columnName, T fetchParameters) {
        String msg = CANNOT_READ_ENTITY_BECAUSE_OF_NO_EXISTING_RECORD.formatted(className, columnName, fetchParameters);
        return new ServiceException(msg);
    }

    public static <T> ServiceException cannotSaveEntity(T entity, PersistenceException pEx) {
        String msg = (entity == null)
                ? CANNOT_SAVE_DUE_TO_DATABASE_PROBLEMS_WITH_NULL_ENTITY
                : CANNOT_SAVE_ENTITY_DUE_TO_DATABASE_PROBLEMS.formatted(entity.getClass().getSimpleName(), entity.toString());
        return new ServiceException(msg, pEx);
    }

    public static ServiceException noSuchField(String fieldName, NoSuchFieldException e) {
        String msg = NO_SUCH_FIELD.formatted(fieldName);
        return new ServiceException(msg, e);
    }

    public static ServiceException illegalAccessOnField(String fieldName, IllegalAccessException e) {
        String msg = ILLEGAL_ACCESS_FIELD.formatted(fieldName);
        return new ServiceException(msg, e);
    }

    public static <T> ServiceException cannotDeleteEntity(T entity, PersistenceException pEx) {
        String msg = (entity == null)
                ? CANNOT_DELETE_DUE_TO_DATABASE_PROBLEMS_WITH_NULL_ENTITY
                : CANNOT_DELETE_ENTITY_DUE_TO_DATABASE_PROBLEMS.formatted(entity.getClass().getSimpleName(), entity.toString());
        return new ServiceException(msg, pEx);
    }

    public static ServiceException passedNullFunction() {
        return new ServiceException(PASSED_FUNCTION_NULL, null);
    }

    public static <T> ServiceException secureFindFailed(T identifier, PersistenceException pEx) {
        String msg = (identifier == null)
                ? SECURE_FIND_FAILED_BECAUSE_OF_DATABASE_PROBLEM_NULL_IDENTIFIER
                : SECURE_FIND_FAILED_BECAUSE_OF_DATABASE_PROBLEM.formatted(identifier);
        return new ServiceException(msg, pEx);
    }

    public static ServiceException pathfinderNoRegions(Region startRegion, Region endRegion) {
        String msg = NO_REGIONS_TO_VISIT.formatted(startRegion,endRegion);
        return new ServiceException(msg);
    }

    public static ServiceException noRpChar() {
        return new ServiceException(NO_RP_CHAR);
    }

    public static ServiceException createRpCharNoFaction(String playerName) {
        return new ServiceException(CREATE_RP_CHAR_NO_FACTION.formatted(playerName));
    }

    public static ServiceException cannotMoveRpCharAlreadyInRegion(@NotNull RPChar rpchar, Region region) {
        String msg = CANNOT_MOVE_RPCHAR_DUE_TO_ALREADY_IN_REGION.formatted(rpchar.getName(), region.getId());
        return new ServiceException(msg);
    }
    public static ServiceException cannotMoveRpCharBoundToArmy(@NotNull RPChar rpchar, @NotNull Army army) {
        String msg = CANNOT_MOVE_RPCHAR_DUE_BOUND_TO_ARMY.formatted(rpchar.getName(), army.getName());
        return new ServiceException(msg);
    }

    public static ServiceException cannotMoveRpCharAlreadyMoving(@NotNull RPChar rpchar) {
        String msg = CANNOT_MOVE_RPCHAR_DUE_TO_ALREADY_MOVING.formatted(rpchar.getName());
        return new ServiceException(msg);
    }

    public static ServiceException noActiveMovement(@NotNull RPChar rpchar) { return new ServiceException(NO_ACTIVE_MOVEMENT.formatted(rpchar.getName())); }

    public static ServiceException moreThanOneActiveMovement(@NotNull RPChar rpchar) { return new ServiceException(MORE_THAN_ONE_ACTIVE_MOVEMENT.formatted(rpchar.getName())); }
    private ServiceException(String message, Throwable rootCause) { super(message, rootCause);}
    private ServiceException(String message) { super(message);}

}
