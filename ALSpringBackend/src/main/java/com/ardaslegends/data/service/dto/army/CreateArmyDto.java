package com.ardaslegends.data.service.dto.army;

import com.ardaslegends.data.domain.ArmyType;
import com.ardaslegends.data.service.dto.unit.UnitTypeDto;

public record CreateArmyDto(String name, String faction, ArmyType armyType, String claimBuildName, UnitTypeDto[] units) {
}
