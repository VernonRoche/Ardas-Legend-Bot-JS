package com.ardaslegends.data.service.dto.army;

import com.ardaslegends.data.service.dto.unit.UnitTypeDto;

public record CreateArmyDto(String name, String faction, String claimBuildName, UnitTypeDto[] units) {
}
