package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.MovementRepository;
import com.ardaslegends.data.repository.PlayerRepository;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.dto.player.DiscordIdDto;
import com.ardaslegends.data.service.dto.player.rpchar.MoveRpCharDto;
import com.ardaslegends.data.service.exceptions.ServiceException;
import com.ardaslegends.data.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor

@Slf4j
@Service
@Transactional(readOnly = true)
public class MovementService extends AbstractService<Movement, MovementRepository>{

    private final MovementRepository movementRepository;
    private final RegionRepository regionRepository;

    private final PlayerRepository playerRepository;
    private final Pathfinder pathfinder;

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
            log.warn("No player found with executorDiscordId [{}]", dto.discordId());
            throw new IllegalArgumentException("No player found with executorDiscordId [%s]".formatted(dto.discordId()));
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
        List<Movement> playerMovements = secureFindList(player, movementRepository::findMovementsByPlayer);
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
            log.warn("No player found with executorDiscordId [{}]", dto.discordId());
            throw new IllegalArgumentException("No player found with executorDiscordId [%s]".formatted(dto.discordId()));
        }
        Player player = fetchedPlayer.get();
        RPChar rpChar = player.getRpChar();

        log.debug("Checking if the Player has a RP Char");
        if(rpChar == null) {
            log.warn("Player {} has no RP Char!", player);
            throw ServiceException.noRpChar();
        }

        log.debug("Searching for movements of this player");
        List<Movement> allMovements = secureFindList(player, movementRepository::findMovementsByPlayer);
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
