package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.ArmyRepository;
import com.ardaslegends.data.repository.MovementRepository;
import com.ardaslegends.data.repository.PlayerRepository;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.dto.army.CreateArmyDto;
import com.ardaslegends.data.service.dto.army.MoveArmyDto;
import com.ardaslegends.data.service.dto.player.DiscordIdDto;
import com.ardaslegends.data.service.dto.player.rpchar.MoveRpCharDto;
import com.ardaslegends.data.service.exceptions.ServiceException;
import com.ardaslegends.data.service.exceptions.army.ArmyServiceException;
import com.ardaslegends.data.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor

@Slf4j
@Service
@Transactional(readOnly = true)
public class MovementService extends AbstractService<Movement, MovementRepository>{

    private final MovementRepository movementRepository;
    private final RegionRepository regionRepository;
    private final ArmyRepository armyRepository;
    private final PlayerRepository playerRepository;
    private final Pathfinder pathfinder;

    // TODO: Check if time is frozen -> if yes, cancel request
    // TODO: Check if army is in a battle -> if yes, cancel request
    // TODO: Check if army is healing -> if yes, ask to stop healing
    @Transactional(readOnly = false)
    public Movement createArmyMovement(MoveArmyDto dto) {
        log.debug("Trying to move Army [{}] executed by [{}] to Region [{}]", dto.armyName(), dto.executorDiscordId(), dto.toRegion());

        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkAllBlanks(dto);

        log.debug("Fetching required data");

        log.trace("Fetching player");
        Optional<Player> fetchedPlayer = secureFind(dto.executorDiscordId(), playerRepository::findByDiscordID);

        if(fetchedPlayer.isEmpty()) {
            // TODO: Change to ServiceException
            log.warn("Player with discId [{}] was not found", dto.executorDiscordId());
            throw new IllegalArgumentException("You are not registered! Please register your account");
        }
        Player player = fetchedPlayer.get();

        log.trace("Fetching army entity");
        Optional<Army> fetchedArmy = secureFind(dto.armyName(), armyRepository::findArmyByName);

        if(fetchedArmy.isEmpty()) {
            // TODO: Change to ServiceException
            log.warn("Army with name [{}] was not found", dto.armyName());
            throw new IllegalArgumentException("There was no army with the name '%s' found in the database".formatted(dto.armyName()));
        }
        Army army = fetchedArmy.get();

        log.trace("Fetching region entity");
        Optional<Region> fetchedRegion = secureFind(dto.toRegion(), regionRepository::findById);

        if(fetchedRegion.isEmpty()) {
            log.warn("Desired region [{}] does not exist in the database", dto.toRegion());
            throw ServiceException.regionDoesNotExist(dto.toRegion());
        }
        Region region = fetchedRegion.get();

        log.debug("Checking if army is already in the desired region");
        if(dto.toRegion().equals(army.getCurrentRegion())) {
            log.warn("Army is already in desired region [{}], no movement required");
            throw ArmyServiceException.cannotMoveArmyAlreadyInRegion(army.toString(),dto.toRegion());
        }

        log.debug("Checking if army is currently performing a movement");
        if(secureFind(army, movementRepository::findMovementByArmyAndIsCurrentlyActiveTrue).isPresent()) {
            log.warn("Army [{}] is currently performing a movement", dto.armyName());
            throw ArmyServiceException.cannotMoveArmyDueToArmyBeingInMovement(army.getName());
        }

        log.debug("Checking if executor is allowed to perform the movement");
        boolean isAllowed = false;

        log.trace("Checking if the executor is bound to the army");
        if(Objects.equals(player, army.getBoundTo())) {
            log.trace("Executor is bound to the army, allowed to move it!");
            isAllowed = true;
        }

        log.trace("Checking if the army is in the same faction");
        if(!isAllowed && !player.getFaction().equals(army.getFaction())) {
            log.warn("CreateArmyMovement: Movement denied, army and player are not in the same faction");
            throw ArmyServiceException.cannotMoveArmyDueToPlayerAndArmyBeingInDifferentFactions(army.getName());
        }

        log.trace("Checking if the player is the faction leader");
        if(!isAllowed && player.equals(army.getFaction().getLeader())) {
            log.trace("Executor is the leader of the armies faction, allowed to move it!");
            isAllowed = true;
        }

        // TODO: Check Lordship -> once system is implemented, lords may also be allowed to move armies
        // log.trace("Checking if the player is a Lord and has permission to move armies");

        if(!isAllowed) {
            log.warn("Player [{}] in Faction [{}] does not have permission to move armies", player.getIgn(), player.getFaction());
            throw ArmyServiceException.notAllowedToMoveArmiesThatAreNotBoundToYou();
        }

        log.debug("Player [{}] is allowed to move army [{}], executing pathfinder", player, army);
        Path path = pathfinder.findShortestWay(army.getCurrentRegion(),region,player, false);

        var currentTime = LocalDateTime.now();
        log.debug("Creating movement object");
        Movement movement = Movement.builder()
                .army(army)
                .player(army.getBoundTo())
                .isCharMovement(false)
                .isAccepted(false)
                .isCurrentlyActive(true)
                .startTime(currentTime)
                .endTime(currentTime.plusDays(path.getCost()))
                .path(path)
                .build();

        log.debug("Saving Movement to database");
        movement = secureSave(movement, movementRepository);

        log.info("Successfully saved movement [{}]", movement);
        return movement;
    }

    // TODO: Check if player is healing
    @Transactional(readOnly = false)
    public Movement createRpCharMovement(MoveRpCharDto dto) {
        log.debug("Moving RpChar of player {} to Region {}", dto.discordId(), dto.toRegion());

        //Validating data

        log.trace("Validating Data");
        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkAllBlanks(dto);

        log.trace("Getting the player");
        Optional<Player> fetchedPlayer = secureFind(dto.discordId(), playerRepository::findByDiscordID);

        if(fetchedPlayer.isEmpty()) {
            log.warn("No player found with discordId [{}]", dto.discordId());
            throw new IllegalArgumentException("No player found with discordId [%s]".formatted(dto.discordId()));
        }
        Player player = fetchedPlayer.get();
        log.trace("Setting the RPChar");
        RPChar rpChar = player.getRpChar();

        log.debug("Checking if the Player has a RP Char");
        if(rpChar == null) {
            log.warn("Player {} has no RP Char!", player);
            throw ServiceException.noRpChar();
        }

        log.debug("Checking if destination is the current region");
        if(dto.toRegion().equals(rpChar.getCurrentRegion().getId())) {
            log.warn("Character is already in region {}!", dto.toRegion());
            throw ServiceException.cannotMoveRpCharAlreadyInRegion(rpChar, rpChar.getCurrentRegion());
        }

        log.debug("Checking if rpChar is bound to army");
        if(rpChar.getBoundTo() != null) {
            log.warn("RpChar is currently bound to army!");
            throw ServiceException.cannotMoveRpCharBoundToArmy(rpChar, rpChar.getBoundTo());
        }

        log.debug("Checking if rpChar is already in a movement");
        List<Movement> playerMovements = secureFind(player, movementRepository::findMovementsByPlayer);
        if(playerMovements.stream().anyMatch(Movement::getIsCurrentlyActive)) { //Checking if there are any active movements
            log.warn("Player {} is already involved in a movement!", player);
            throw ServiceException.cannotMoveRpCharAlreadyMoving(player.getRpChar());
        }

        //Setting up Region Data

        log.trace("Find the region the player is moving to");
        Optional<Region> fetchedToRegion = secureFind(dto.toRegion(), regionRepository::findById);

        if(fetchedToRegion.isEmpty()) {
            log.warn("User inputed to Region does not exist [{}]", dto.toRegion());
            throw new IllegalArgumentException("The region %s does not exist!".formatted(dto.toRegion()));
        }

        Region toRegion = fetchedToRegion.get();

        log.trace("Getting the RPChar's current region");
        Region fromRegion = rpChar.getCurrentRegion();

        log.debug("Calling the pathfinder to find the fastest way from '{}' -> '{}'", fromRegion.getId(), toRegion.getId());
        Path shortestPath = pathfinder.findShortestWay(fromRegion, toRegion, player, true);

        log.trace("Getting the current time");
        LocalDateTime currentTime = LocalDateTime.now();

        log.trace("Building the movement object");
        Movement movement = Movement.builder().player(player).path(shortestPath).startTime(currentTime).endTime(currentTime.plusDays(shortestPath.getCost())).isCharMovement(true).isAccepted(false).isCurrentlyActive(true).build();

        log.trace("Saving the new movement");
        movement = secureSave(movement, movementRepository);

        log.info("Successfully created new Movement for the RPChar '{}' of Player '{}'", rpChar.getName(), player);
        return movement;
    }

    @Transactional(readOnly = false)
    public Movement cancelRpCharMovement(DiscordIdDto dto) {
        log.debug("Cancelling the rp char movement of player {}", dto.discordId());

        //validating data
        log.trace("Validating Data");
        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkAllBlanks(dto);

        log.trace("Getting the player");
        Optional<Player> fetchedPlayer = secureFind(dto.discordId(), playerRepository::findByDiscordID);

        if(fetchedPlayer.isEmpty()) {
            log.warn("No player found with discordId [{}]", dto.discordId());
            throw new IllegalArgumentException("No player found with discordId [%s]".formatted(dto.discordId()));
        }
        Player player = fetchedPlayer.get();
        RPChar rpChar = player.getRpChar();

        log.debug("Checking if the Player has a RP Char");
        if(rpChar == null) {
            log.warn("Player {} has no RP Char!", player);
            throw ServiceException.noRpChar();
        }

        log.debug("Searching for movements of this player");
        List<Movement> allMovements = secureFind(player, movementRepository::findMovementsByPlayer);
        log.debug("Found {} movements for player {}", allMovements.size(), player.getIgn());

        log.debug("Looking for active movements");
        List<Movement> activeMovements = allMovements.stream().filter(Movement::getIsCurrentlyActive).toList();

        log.debug("Checking if there is an active movement that can be cancelled");
        if(activeMovements.size() == 0) {
            log.warn("No active movements for player {}", player.getIgn());
            throw ServiceException.noActiveMovement(rpChar);
        }

        //THIS SHOULD NEVER HAPPEN - CHECKING JUST IN CASE
        log.debug("Checking if there are more than 1 active movements");
        if(activeMovements.size() > 1) {
            log.warn("Found more than one active movement for player {} - cancelling process", player.getIgn());
            throw ServiceException.moreThanOneActiveMovement(rpChar);
        }

        log.debug("Setting movement to inactive");
        Movement movement = activeMovements.get(0);
        movement.setIsCurrentlyActive(false);

        log.debug("Persisting movement");
        movement = secureSave(movement, movementRepository);

        return movement;
    }


}
