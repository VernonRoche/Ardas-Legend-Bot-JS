package com.ardaslegends.data.service.exceptions.army;

import com.ardaslegends.data.service.exceptions.ServiceException;

public class ArmyServiceException extends ServiceException{

    private static final String NO_ARMY_WITH_NAME = "No army with the name '%s' found!";
    private static final String PLAYER_NOT_FACTION_LEADER = "You are not the faction leader or a lord of '%s' and therefore cannot (un)bind other players!";

    public static ArmyServiceException noArmyWithName(String armyName) { return new ArmyServiceException(NO_ARMY_WITH_NAME.formatted(armyName)); }

    public static ArmyServiceException notFactionLeader(String playerName, String factionName) { return new ArmyServiceException(PLAYER_NOT_FACTION_LEADER.formatted(playerName, factionName)); }

    protected ArmyServiceException(String message, Throwable rootCause) {
        super(message, rootCause);
    }

    protected ArmyServiceException(String message) {
        super(message);
    }
}
