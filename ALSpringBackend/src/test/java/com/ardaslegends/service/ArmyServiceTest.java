package com.ardaslegends.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.ArmyRepository;
import com.ardaslegends.data.repository.ClaimBuildRepository;
import com.ardaslegends.data.repository.FactionRepository;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.ClaimBuildService;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.UnitTypeService;
import com.ardaslegends.data.service.dto.army.BindArmyDto;
import com.ardaslegends.data.service.dto.army.CreateArmyDto;
import com.ardaslegends.data.service.dto.unit.UnitTypeDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class ArmyServiceTest {

    private ArmyService armyService;

    private ArmyRepository mockArmyRepository;
    private FactionRepository mockFactionRepository;
    private PlayerService mockPlayerService;
    private UnitTypeService mockUnitTypeService;
    private ClaimBuildRepository claimBuildRepository;

    @BeforeEach
    void setup() {
        mockArmyRepository = mock(ArmyRepository.class);
        mockFactionRepository = mock(FactionRepository.class);
        mockPlayerService = mock(PlayerService.class);
        mockUnitTypeService = mock(UnitTypeService.class);
        claimBuildRepository = mock(ClaimBuildRepository.class);
        armyService = new ArmyService(mockArmyRepository, mockPlayerService, mockFactionRepository, mockUnitTypeService, claimBuildRepository);
    }

    // Create Army
    @Test
    void ensureCreateArmyWorksProperly() {

    }

    @Test
    void ensureCreateArmyThrowsIAEWhenArmyNameIsAlreadyTaken() {
        log.debug("Testing if createArmy correctly throws IAE when name is already taken");

        log.trace("Initializing data");
        CreateArmyDto dto = new CreateArmyDto("Kek", "Kek", ArmyType.ARMY, "Kek", new UnitTypeDto[]{new UnitTypeDto("Kek", 10)});

        when(mockArmyRepository.findById(dto.name())).thenReturn(Optional.of(new Army()));

        log.debug("Expecting IAE on call");
        log.debug("Calling createArmy()");
        var result = assertThrows(IllegalArgumentException.class, () -> armyService.createArmy(dto));

        assertThat(result.getMessage()).contains("already exists");
        log.info("Test passed: IAE when Army Name is taken!");
    }
    @Test
    void ensureCreateArmyThrowsIAEWhenNoValidFactionFound() {
        log.debug("Testing if createArmy correctly throws IAE when no valid faction could be found");

        log.trace("Initializing data");
        CreateArmyDto dto = new CreateArmyDto("Kek", "Kek", ArmyType.ARMY, "Kek", new UnitTypeDto[]{new UnitTypeDto("Kek", 10)});

        when(mockArmyRepository.findById(dto.name())).thenReturn(Optional.empty());
        when(mockFactionRepository.findById(dto.faction())).thenReturn(Optional.empty());

        log.debug("Expecting IAE on call");
        log.debug("Calling createArmy()");
        var result = assertThrows(IllegalArgumentException.class, () -> armyService.createArmy(dto));

        assertThat(result.getMessage()).contains("No faction found");
    }
    @Test
    void ensureCreateArmyThrowsIAEWhenNoClaimBuildWithInputNameHasBeenFound() {
        log.debug("Testing if createArmy correctly throws IAE when no claimBuild could be found");

        log.trace("Initializing data");
        CreateArmyDto dto = new CreateArmyDto("Kek", "Kek", ArmyType.ARMY, "Kek", new UnitTypeDto[]{new UnitTypeDto("Kek", 10)});

        when(mockArmyRepository.findById(dto.name())).thenReturn(Optional.empty());
        when(mockFactionRepository.findById(dto.faction())).thenReturn(Optional.of(new Faction()));
        when(mockUnitTypeService.getUnitTypeByName(any())).thenReturn(new UnitType("Kek", 1.0));
        when(claimBuildRepository.findById(dto.claimBuildName())).thenReturn(Optional.empty());

        log.debug("Expecting IAE on call");
        log.debug("Calling createArmy()");
        var result = assertThrows(IllegalArgumentException.class, () -> armyService.createArmy(dto));

        assertThat(result.getMessage()).contains("No ClaimBuild found");
    }
    @Test
    void ensureBindWorksWhenBindingSelf() {
        log.debug("Testing if army binding works properly!");

        //Assing
        log.trace("Initializing data");
        Faction faction = Faction.builder().name("Gondor").build();
        Region region = Region.builder().id("90").build();
        RPChar rpChar = RPChar.builder().name("Belegorn").currentRegion(region).build();
        Player player = Player.builder().ign("Lüktrönic").discordID("1").faction(faction).rpChar(rpChar).build();
        Army army = Army.builder().name("Gondorian Army").currentRegion(region).armyType(ArmyType.ARMY).faction(faction).build();

        BindArmyDto dto = new BindArmyDto("1", "1", "Gondorian Army");

        when(mockPlayerService.getPlayerByDiscordId("1")).thenReturn(player);
        when(mockArmyRepository.findArmyByName("Gondorian Army")).thenReturn(Optional.of(army));
        when(mockArmyRepository.save(army)).thenReturn(army);

        log.debug("Calling bind()");
        armyService.bind(dto);

        assertThat(army.getBoundTo()).isEqualTo(player);
        log.info("Test passed: army binding works properly!");
    }

    //TODO add other tests for bind()

}
