package com.ardaslegends.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.ArmyRepository;
import com.ardaslegends.data.repository.ClaimBuildRepository;
import com.ardaslegends.data.repository.FactionRepository;
import com.ardaslegends.data.repository.MovementRepository;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.ClaimBuildService;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.UnitTypeService;
import com.ardaslegends.data.service.dto.army.BindArmyDto;
import com.ardaslegends.data.service.dto.army.CreateArmyDto;
import com.ardaslegends.data.service.dto.unit.UnitTypeDto;
import com.ardaslegends.data.service.exceptions.army.ArmyServiceException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.internal.ServiceDependencyException;
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
    private MovementRepository mockMovementRepository;
    private FactionRepository mockFactionRepository;
    private PlayerService mockPlayerService;
    private UnitTypeService mockUnitTypeService;
    private ClaimBuildRepository claimBuildRepository;

    @BeforeEach
    void setup() {
        mockArmyRepository = mock(ArmyRepository.class);
        mockMovementRepository = mock(MovementRepository.class);
        mockFactionRepository = mock(FactionRepository.class);
        mockPlayerService = mock(PlayerService.class);
        mockUnitTypeService = mock(UnitTypeService.class);
        claimBuildRepository = mock(ClaimBuildRepository.class);
        armyService = new ArmyService(mockArmyRepository, mockMovementRepository,mockPlayerService, mockFactionRepository, mockUnitTypeService, claimBuildRepository);
    }

    // Create Army
    @Test
    void ensureCreateArmyWorksProperly() {
        log.debug("Testing if createArmy works properly");

        log.trace("Initializing data");
        CreateArmyDto dto = new CreateArmyDto("Kek", "Kek", ArmyType.ARMY, "Kek", new UnitTypeDto[]{new UnitTypeDto("Kek", 11)});
        ClaimBuild claimBuild = new ClaimBuild();
        ClaimBuildType type = ClaimBuildType.TOWN;
        claimBuild.setType(type);


        when(mockArmyRepository.findById(dto.name())).thenReturn(Optional.empty());
        when(mockFactionRepository.findById(dto.faction())).thenReturn(Optional.of(new Faction()));
        when(mockUnitTypeService.getUnitTypeByName(any())).thenReturn(new UnitType("Kek", 1.0));
        when(claimBuildRepository.findById(dto.claimBuildName())).thenReturn(Optional.of(claimBuild));
        when(mockArmyRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        log.debug("Calling createArmy()");
        var result = armyService.createArmy(dto);

        assertThat(result.getFreeTokens()).isEqualTo(30-11);
        log.info("Test passed: CreateArmy works properly with correct values");
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
        log.info("Test passed: IAE when no Faction could be found");
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

        log.info("Test passed: IAE when no ClaimBuild could be found");
    }
    @Test
    void ensureCreateArmyThrowsServiceExceptionWhenClaimBuildHasReachedMaxArmies() {
        log.debug("Testing if createArmy correctly throws SE when max armies is already reached");

        log.trace("Initializing data");
        CreateArmyDto dto = new CreateArmyDto("Kek", "Kek", ArmyType.ARMY, "Kek", new UnitTypeDto[]{new UnitTypeDto("Kek", 10)});
        ClaimBuild claimBuild = new ClaimBuild();
        ClaimBuildType type = ClaimBuildType.HAMLET;
        claimBuild.setType(type);

        when(mockArmyRepository.findById(dto.name())).thenReturn(Optional.empty());
        when(mockFactionRepository.findById(dto.faction())).thenReturn(Optional.of(new Faction()));
        when(mockUnitTypeService.getUnitTypeByName(any())).thenReturn(new UnitType("Kek", 1.0));
        when(claimBuildRepository.findById(dto.claimBuildName())).thenReturn(Optional.of(claimBuild));

        log.debug("Expecting SE on call");
        log.debug("Calling createArmy()");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.createArmy(dto));

        log.info("Test passed: SE on max armies from ClaimBuild");
    }
    @Test
    void ensureCreateArmyThrowsServiceExceptionWhenUnitsExceedAvailableTokens() {
        log.debug("Testing if createArmy correctly throws SE when units exceed available tokens");

        log.trace("Initializing data");
        CreateArmyDto dto = new CreateArmyDto("Kek", "Kek", ArmyType.ARMY, "Kek", new UnitTypeDto[]{new UnitTypeDto("Kek", 11)});
        ClaimBuild claimBuild = new ClaimBuild();
        ClaimBuildType type = ClaimBuildType.TOWN;
        claimBuild.setType(type);


        when(mockArmyRepository.findById(dto.name())).thenReturn(Optional.empty());
        when(mockFactionRepository.findById(dto.faction())).thenReturn(Optional.of(new Faction()));
        when(mockUnitTypeService.getUnitTypeByName(any())).thenReturn(new UnitType("Kek", 3.0));
        when(claimBuildRepository.findById(dto.claimBuildName())).thenReturn(Optional.of(claimBuild));

        log.debug("Expecting SE on call");
        log.debug("Calling createArmy()");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.createArmy(dto));

        log.info("Test passed: SE on exceeding token count");
    }
    @Test
    void ensureBindWorksWhenBindingSelf() {
        log.debug("Testing if army binding works properly!");

        //Assign
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
    @Test
    void ensureBindWorksWhenBindingOtherPlayer() {
        log.debug("Testing if army binding works properly on others!");

        //Assign
        log.trace("Initializing data");
        Faction faction = Faction.builder().name("Gondor").build();
        Region region = Region.builder().id("90").build();
        RPChar rpChar = RPChar.builder().name("Belegorn").currentRegion(region).build();
        Player executor = Player.builder().ign("Lüktrönic").discordID("1").faction(faction).rpChar(rpChar).build();
        Player target = Player.builder().ign("aned").discordID("2").faction(faction).rpChar(rpChar).build();
        Army army = Army.builder().name("Gondorian Army").currentRegion(region).armyType(ArmyType.ARMY).faction(faction).build();

        faction.setLeader(executor);

        BindArmyDto dto = new BindArmyDto("1", "2", "Gondorian Army");

        when(mockPlayerService.getPlayerByDiscordId("1")).thenReturn(executor);
        when(mockPlayerService.getPlayerByDiscordId(dto.targetDiscordId())).thenReturn(target);
        when(mockArmyRepository.findArmyByName("Gondorian Army")).thenReturn(Optional.of(army));
        when(mockArmyRepository.save(army)).thenReturn(army);

        log.debug("Calling bind()");
        armyService.bind(dto);

        assertThat(army.getBoundTo()).isEqualTo(target);
        log.info("Test passed: army binding works properly on other players as faction leader!");
    }
    @Test
    void ensureBindArmyThrowsServiceExceptionWhenNormalPlayerTriesToBindOtherPlayers() {
        log.debug("Testing if SE is thrown when normal player tries to bind other players");

        log.trace("Initializing data");
        BindArmyDto dto = new BindArmyDto("Luktronic", "Anedhel", "Slayers of Orcs");
        Faction gondor = Faction.builder().name("Gondor").build();
        Player luk = Player.builder().discordID(dto.executorDiscordId()).faction(gondor).build();
        Player aned = Player.builder().discordID(dto.targetDiscordId()).faction(gondor).build();

        when(mockPlayerService.getPlayerByDiscordId(dto.executorDiscordId())).thenReturn(luk);

        log.debug("Calling bind()");
        log.trace("Expecting ServiceException");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.bind(dto));
    }
    @Test
    void ensureBindArmyThrowsServiceExceptionWhenTargetArmyNotFound() {
        log.debug("Testing if SE is thrown when target army does not exist");

        log.trace("Initializing data");
        BindArmyDto dto = new BindArmyDto("Luktronic", "Luktronic", "Slayers of Orcs");
        Faction gondor = Faction.builder().name("Gondor").build();
        Player luk = Player.builder().discordID(dto.executorDiscordId()).faction(gondor).build();
        Player aned = Player.builder().discordID(dto.targetDiscordId()).faction(gondor).build();

        when(mockPlayerService.getPlayerByDiscordId(dto.executorDiscordId())).thenReturn(luk);
        when(mockArmyRepository.findArmyByName(dto.armyName())).thenReturn(Optional.empty());

        log.debug("Calling bind()");
        log.trace("Expecting ServiceException");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.bind(dto));

        log.info("Test passed: bind() correctly throws SE when no Army has been found");
    }
    @Test
    void ensureBindArmyThrowsServiceExceptionWhenTargetArmyHasDifferentFaction() {
        log.debug("Testing if SE is thrown when target army has a different faction to the target player");

        log.trace("Initializing data");
        BindArmyDto dto = new BindArmyDto("Luktronic", "Luktronic", "Slayers of Orcs");
        Faction gondor = Faction.builder().name("Gondor").build();
        Faction mordor = Faction.builder().name("Mordor").build();
        Player luk = Player.builder().discordID(dto.executorDiscordId()).faction(gondor).build();
        Player aned = Player.builder().discordID(dto.targetDiscordId()).faction(gondor).build();
        Army army = Army.builder().name(dto.armyName()).armyType(ArmyType.ARMY).faction(mordor).build();

        when(mockPlayerService.getPlayerByDiscordId(dto.executorDiscordId())).thenReturn(luk);
        when(mockArmyRepository.findArmyByName(dto.armyName())).thenReturn(Optional.of(army));

        log.debug("Calling bind()");
        log.trace("Expecting ServiceException");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.bind(dto));

        log.info("Test passed: bind() correctly throws SE when Army is from a different faction");
    }
    @Test
    void ensureBindArmyThrowsServiceExceptionWhenTargetArmyIsInADifferentRegion() {
        log.debug("Testing if SE is thrown when target army is in a different region to the player");

        log.trace("Initializing data");
        BindArmyDto dto = new BindArmyDto("Luktronic", "Luktronic", "Slayers of Orcs");
        Faction gondor = Faction.builder().name("Gondor").build();
        Region region1 = Region.builder().id("90").build();
        Region region2 = Region.builder().id("91").build();
        RPChar rpchar = RPChar.builder().name("Belegorn").currentRegion(region1).build();
        Player luk = Player.builder().discordID(dto.executorDiscordId()).faction(gondor).rpChar(rpchar).build();
        Army army = Army.builder().name(dto.armyName()).armyType(ArmyType.ARMY).faction(gondor).currentRegion(region2).build();

        when(mockPlayerService.getPlayerByDiscordId(dto.executorDiscordId())).thenReturn(luk);
        when(mockArmyRepository.findArmyByName(dto.armyName())).thenReturn(Optional.of(army));

        log.debug("Calling bind()");
        log.trace("Expecting ServiceException");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.bind(dto));

        log.info("Test passed: bind() correctly throws SE when Army is in a different region");
    }

    @Test
    void ensureBindArmyThrowsServiceExceptionWhenTargetArmyIsBoundToAPlayer() {
        log.debug("Testing if SE is thrown when target army is already bound to a player");

        log.trace("Initializing data");
        BindArmyDto dto = new BindArmyDto("Luktronic", "Luktronic", "Slayers of Orcs");
        Faction gondor = Faction.builder().name("Gondor").build();
        Region region = Region.builder().id("90").build();
        RPChar rpchar = RPChar.builder().name("Belegorn").currentRegion(region).build();
        Player luk = Player.builder().discordID(dto.executorDiscordId()).faction(gondor).rpChar(rpchar).build();
        Player aned = Player.builder().discordID("1235").faction(gondor).rpChar(rpchar).build();
        Army army = Army.builder().name(dto.armyName()).armyType(ArmyType.ARMY).faction(gondor).currentRegion(region).boundTo(aned).build();

        when(mockPlayerService.getPlayerByDiscordId(dto.executorDiscordId())).thenReturn(luk);
        when(mockArmyRepository.findArmyByName(dto.armyName())).thenReturn(Optional.of(army));

        log.debug("Calling bind()");
        log.trace("Expecting ServiceException");
        var result = assertThrows(ArmyServiceException.class, () -> armyService.bind(dto));

        log.info("Test passed: bind() correctly throws SE when Army is already bound to another player");
    }

}
