package com.ardaslegends.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.ArmyRepository;
import com.ardaslegends.data.repository.FactionRepository;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.UnitTypeService;
import com.ardaslegends.data.service.dto.army.BindArmyDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class ArmyServiceTest {

    private ArmyService armyService;

    private ArmyRepository mockArmyRepository;
    private FactionRepository mockFactionRepository;
    private PlayerService mockPlayerService;
    private UnitTypeService mockUnitTypeService;

    @BeforeEach
    void setup() {
        mockArmyRepository = mock(ArmyRepository.class);
        mockFactionRepository = mock(FactionRepository.class);
        mockPlayerService = mock(PlayerService.class);
        mockUnitTypeService = mock(UnitTypeService.class);
        armyService = new ArmyService(mockArmyRepository, mockPlayerService, mockFactionRepository, mockUnitTypeService);
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
