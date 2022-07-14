package com.ardaslegends.data.service.exceptions.army;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.service.exceptions.ServiceException;

public class ArmyServiceException extends ServiceException{

    private static final String NO_ARMY_WITH_NAME = "No army with the name '%s' found!";
    private static final String PLAYER_NOT_FACTION_LEADER = "You are not the faction leader or a lord of '%s' and therefore cannot (un)bind other players!";
    private static final String ONLY_FACTION_LEADER_CAN_BIND_WANDERER = "Only the faction leader or a lord can bind wanderers to armies!";
    private static final String NOT_IN_SAME_REGION = "Army/Company '%s' is not in the same region as character '%s'!";
    private static final String NOT_SAME_FACTION = "You are not in the same faction as army/company '%s'! Your faction: '%s' - army's/company's faction: '%s'!";
    private static final String ALREADY_BOUND = "The army/company '%s' is already bound to the player '%s'!";
    private static final String TOO_HIGH_TOKEN_COUNT = "The army's token count exceeds the maximum of 30, it currently is '%s";
    private static final String CANNOT_MOVE_ARMY_DUE_TO_ALREADY_MOVING = "Cannot move army '%s' because it is already in a movement! Cancel its movement to submit a new one";
    private static final String MAX_ARMY_OR_COMPANIES_CREATED = "The claimbuild '%s' is already at maximum armies/companies created: %s";
    private static final String CANNOT_MOVE_ARMY_DUE_TO_PLAYER_AND_ARMY_BEING_IN_DIFFERENT_FACTIONS = "Army '%s' could not be moved since it is in a different faction!";
    private static final String NO_PERMISSION_TO_MOVE_ARMY = "You do not have the permission to move armies that you are not bound to!";
    private static final String CANNOT_MOVE_ARMY_ALREADY_IN_REGION = "Army '%s' is already in the desired region of '%s'";
    public static ArmyServiceException noArmyWithName(String armyName) { return new ArmyServiceException(NO_ARMY_WITH_NAME.formatted(armyName)); }

    public static ArmyServiceException notFactionLeader(String factionName) { return new ArmyServiceException(PLAYER_NOT_FACTION_LEADER.formatted(factionName)); }

    public static ArmyServiceException notInSameRegion(String armyName, String charName) { return new ArmyServiceException(NOT_IN_SAME_REGION.formatted(armyName, charName)); }
    public static ArmyServiceException notSameFaction(String armyName, String playerFactionName, String armyFactionName) { return new ArmyServiceException(NOT_SAME_FACTION.formatted(armyName, playerFactionName, armyFactionName)); }
    public static ArmyServiceException alreadyBound(String armyName, String playerName) { return new ArmyServiceException(ALREADY_BOUND.formatted(armyName, playerName)); }
    public static ArmyServiceException tooHighTokenCount(int tokenCount) {return new ArmyServiceException(TOO_HIGH_TOKEN_COUNT.formatted(tokenCount)); };
    public static ArmyServiceException maxArmyOrCompany(String claimbuild, String units) {return new ArmyServiceException(MAX_ARMY_OR_COMPANIES_CREATED.formatted(claimbuild, units)); }
    public static ArmyServiceException onlyLeaderCanBindWanderer() {return new ArmyServiceException(ONLY_FACTION_LEADER_CAN_BIND_WANDERER);}
    public static ArmyServiceException cannotMoveArmyDueToArmyBeingInMovement(String armyName) {return new ArmyServiceException(CANNOT_MOVE_ARMY_DUE_TO_ALREADY_MOVING.formatted(armyName));}
    public static ArmyServiceException cannotMoveArmyDueToPlayerAndArmyBeingInDifferentFactions(String armyName) {return new ArmyServiceException(CANNOT_MOVE_ARMY_DUE_TO_PLAYER_AND_ARMY_BEING_IN_DIFFERENT_FACTIONS.formatted(armyName)); }
    public static ArmyServiceException notAllowedToMoveArmiesThatAreNotBoundToYou() {return new ArmyServiceException(NO_PERMISSION_TO_MOVE_ARMY);}
    public static ArmyServiceException cannotMoveArmyAlreadyInRegion(String armyName, String region) {return new ArmyServiceException(CANNOT_MOVE_ARMY_ALREADY_IN_REGION.formatted(armyName,region));}
    protected ArmyServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected ArmyServiceException(String message) {
        super(message);
    }
}
