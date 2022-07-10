package com.ardaslegends.data.service.exceptions.army;

import com.ardaslegends.data.service.exceptions.ServiceException;

public class ArmyServiceException extends ServiceException{

    private static final String NO_ARMY_WITH_NAME = "No army with the name '%s' found!";
    private static final String PLAYER_NOT_FACTION_LEADER = "You are not the faction leader or a lord of '%s' and therefore cannot (un)bind other players!";
    private static final String NOT_IN_SAME_REGION = "Army/Company '%s' is not in the same region as character '%s'!";
    private static final String NOT_SAME_FACTION = "You are not in the same faction as army/company '%s'! Your faction: '%s' - army's/company's faction: '%s'!";
    private static final String ALREADY_BOUND = "The army/company '%s' is already bound to the player '%s'!";

    private static final String TOO_HIGH_TOKEN_COUNT = "The army's token count exceeds the maximum of 30, it currently is '%s";

    public static ArmyServiceException noArmyWithName(String armyName) { return new ArmyServiceException(NO_ARMY_WITH_NAME.formatted(armyName)); }

    public static ArmyServiceException notFactionLeader(String factionName) { return new ArmyServiceException(PLAYER_NOT_FACTION_LEADER.formatted(factionName)); }

    public static ArmyServiceException notInSameRegion(String armyName, String charName) { return new ArmyServiceException(NOT_IN_SAME_REGION.formatted(armyName, charName)); }
    public static ArmyServiceException notSameFaction(String armyName, String playerFactionName, String armyFactionName) { return new ArmyServiceException(NOT_SAME_FACTION.formatted(armyName, playerFactionName, armyFactionName)); }
    public static ArmyServiceException alreadyBound(String armyName, String playerName) { return new ArmyServiceException(ALREADY_BOUND.formatted(armyName, playerName)); }
    public static ArmyServiceException tooHighTokenCount(int tokenCount) {return new ArmyServiceException(TOO_HIGH_TOKEN_COUNT.formatted(tokenCount)); };
    protected ArmyServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected ArmyServiceException(String message) {
        super(message);
    }
}
