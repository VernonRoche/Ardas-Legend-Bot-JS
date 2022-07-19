package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.ArmyRepository;
import com.ardaslegends.data.repository.ClaimBuildRepository;
import com.ardaslegends.data.repository.MovementRepository;
import com.ardaslegends.data.service.dto.army.BindArmyDto;
import com.ardaslegends.data.service.dto.unit.UnitTypeDto;
import com.ardaslegends.data.service.exceptions.army.ArmyServiceException;
import com.ardaslegends.data.repository.FactionRepository;
import com.ardaslegends.data.service.dto.army.CreateArmyDto;
import com.ardaslegends.data.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class ArmyService extends AbstractService<Army, ArmyRepository> {
    private final ArmyRepository armyRepository;
    private final MovementRepository movementRepository;
    private final PlayerService playerService;
    private final FactionRepository factionRepository;
    private final UnitTypeService unitTypeService;
    private final ClaimBuildRepository claimBuildRepository;

    // TODO: Need to test
    public Army createArmy(CreateArmyDto dto) {
        log.debug("Creating army with data [{}]", dto);

        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkAllBlanks(dto);
        Arrays.stream(dto.units()).forEach(ServiceUtils::checkAllBlanks);

        log.debug("Fetching required Data");

        log.trace("Fetching if an army with name [{}] already exists.", dto.name());
        Optional<Army> fetchedArmy = secureFind(dto.name(), armyRepository::findById);

        if(fetchedArmy.isPresent()) {
            log.warn("Army with name [{}] already exists", dto.name());
            throw new IllegalArgumentException("Army with name %s already exists, please choose a different name".formatted(dto.name()));
        }

        log.trace("Fetching Faction with name [{}]", dto.faction());
        Optional<Faction> fetchedFaction = secureFind(dto.faction(), factionRepository::findById);

        if(fetchedFaction.isEmpty()) {
            log.warn("No faction found with name [{}]", dto.faction());
            throw new IllegalArgumentException("No faction found that has the name \"%s\"".formatted(dto.faction()));
        }

        log.debug("Assembling Units Map, fetching units");
        var unitTypes = Arrays.stream(dto.units())
                .collect(Collectors.toMap(
                        unitTypeDto -> unitTypeService.getUnitTypeByName(unitTypeDto.unitTypeName()),
                        UnitTypeDto::amount
                ));

        log.trace("Fetching Claimbuild with name [{}]", dto.claimBuildName());
        Optional<ClaimBuild> fetchedClaimbuild = secureFind(dto.claimBuildName(), claimBuildRepository::findById);

        if(fetchedClaimbuild.isEmpty()) {
            log.warn("No ClaimBuild found with name [{}]", dto.claimBuildName());
            // TODO: Change to ServiceException, dont know if if it should be in ArmyServiceException, CBServiceException or base SE
            throw new IllegalArgumentException("No ClaimBuild found with the name %s".formatted(dto.claimBuildName()));
        }

        ClaimBuild inputClaimBuild = fetchedClaimbuild.get();

        log.trace("Finished fetching required data");

        log.debug("Checking if ClaimBuild can create another army");

        if(inputClaimBuild.getCreatedArmies().size() >= inputClaimBuild.getType().getMaxArmies()) {
            log.warn("ClaimBuild [{}] already has max amount of armies created! Armies {}",inputClaimBuild ,inputClaimBuild.getCreatedArmies() );
            throw ArmyServiceException.maxArmyOrCompany(inputClaimBuild.toString(), inputClaimBuild.getCreatedArmies().toString());
        }

        log.debug("Checking if token count does not exceed 30");
        // Calculate the tokens that were used
        log.trace("Calculating amount of tokens used");
        int tokenCount = 0;
        List<Unit> units = new ArrayList<>();
        for (UnitType unit: unitTypes.keySet()) {
            // Get the UnitType cost and multiply it by the count of that unit, which is stored in the units Map
            tokenCount += unit.getTokenCost() * unitTypes.get(unit);
            units.add(new Unit(null, unit, null, unitTypes.get(unit),unitTypes.get(unit)));
        }

        log.debug("Calculated Token count is [{}]", tokenCount);
        if(tokenCount > 30) {
            log.warn("Token count exceeds 30 [{}]", tokenCount);
            throw ArmyServiceException.tooHighTokenCount(tokenCount);
        }

        log.trace("Assembling Army Object");
        Army army = new Army(dto.name(),
                dto.armyType(),
                fetchedFaction.get(),
                inputClaimBuild.getRegion(),
                null,
                null,
                null,
                inputClaimBuild,
                30-tokenCount,
                inputClaimBuild);

        log.trace("Adding the army to each unit");
        Army finalArmy = army;
        units.forEach(unit -> unit.setArmy(finalArmy));

        army.setUnits(units);

        log.debug("Trying to persist the army object");
        army = secureSave(army, armyRepository);

        log.info("Finished CreateArmy!");
        return army;
    }

    @Transactional(readOnly = false)
    public Army bind(BindArmyDto dto) {
        log.debug("Binding army [{}] to player with discord id [{}]", dto.armyName(), dto.targetDiscordId());

        log.trace("Validating data");
        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkAllBlanks(dto);

        log.trace("Getting the executor player's instance");
        Player executor = playerService.getPlayerByDiscordId(dto.executorDiscordId());

        /*
        Checking if the executor is the faction leader - if not, throw error that player doesn't have permission to bind
         */
        boolean isBindingSelf = dto.executorDiscordId().equals(dto.targetDiscordId()); //Says if the player is binding themselves

        log.debug("Checking if executor and target are not equal");
        if (!isBindingSelf) {
            //TODO Check for lords as well
            log.trace("Executor and target are not equal - checking if executor is faction leader");
            if (!executor.equals(executor.getFaction().getLeader())) {
                log.warn("Executor player [{}] is not faction leader of faction [{}]!", executor, executor.getFaction());
                throw ArmyServiceException.notFactionLeader(executor.getFaction().getName());
            }
        }

        /*
        Setting target player
        If not binding self then fetch the target player from DB
         */
        log.trace("Getting the target player's instance");
        Player targetPlayer = null;
        if (isBindingSelf)
            targetPlayer = executor;
        else
            targetPlayer = playerService.getPlayerByDiscordId(dto.targetDiscordId());

        log.debug("Fetching the army [{}]", dto.armyName());
        Optional<Army> fetchedArmy = armyRepository.findArmyByName(dto.armyName());

        if (fetchedArmy.isEmpty()) {
            log.warn("No army found with the name [{}]!", dto.armyName());
            throw ArmyServiceException.noArmyWithName(dto.armyName());
        }
        Army army = fetchedArmy.get();
        log.debug("Found army [{}] - type: [{}]", army.getName(), army.getArmyType().name());

        // TODO: Check for Wanderer or Allied Faction
        log.debug("Checking if army and player are in the same faction");
        if (!army.getFaction().equals(targetPlayer.getFaction())) {
            log.debug("Player and army are not in the same faction");
            log.debug("Checking if the faction is allied to the players faction");
            //TODO Check for allied faction
            log.debug("Checking if the player is a wanderer");
            if(targetPlayer.getFaction().getName().equals("Wanderer")) {
                log.debug("Target player is a wanderer - checking if executor is faction leader");
                //TODO Check for Lords as well
                if(!executor.equals(executor.getFaction().getLeader())) {
                    log.warn("Player [{}] is not faction leader of [{}] and therefore cannot bind wanderers!", executor, executor.getFaction());
                    throw ArmyServiceException.onlyLeaderCanBindWanderer();
                }
            }
            else {
                log.warn("Army [{}] and player [{}] are not in the same faction (army: [{}], player: [{}])", army.getName(), targetPlayer, army.getFaction(), targetPlayer.getFaction());
                throw ArmyServiceException.notSameFaction(army.getName(), targetPlayer.getFaction().getName(), army.getFaction().getName());
            }
        }

        log.debug("Checking if army and player are in the same region");
        if (!army.getCurrentRegion().equals(targetPlayer.getRpChar().getCurrentRegion())) {
            log.warn("Army and player are not in the same region!");
            throw ArmyServiceException.notInSameRegion(army.getName(), targetPlayer.getRpChar().getName());
        }

        log.debug("Checking if army is already bound to player");
        if (army.getBoundTo() != null) {
            log.warn("Army [{}] is already bound to another player [{}]!", army.getName(), army.getBoundTo());
            throw ArmyServiceException.alreadyBound(army.getName(), army.getBoundTo().getIgn());
        }

        log.debug("Checking if army is in an active movement");
        Optional<Movement> armyActiveMove = movementRepository.findMovementByArmyAndIsCurrentlyActiveTrue(army);
        if(armyActiveMove.isPresent()) {
            String destinationRegion =  armyActiveMove.get().getPath().getDestination();
            log.warn("Army [{}] is currently moving to region [{}] and therefore cannot be bound to player [{}]!", army.getName(), destinationRegion, targetPlayer);
            throw ArmyServiceException.cannotBindArmyIsMoving(army.getName(), destinationRegion);
        }

        log.debug("Checking if rp char is in an active movement");
        Optional<Movement> charActiveMove = movementRepository.findMovementByPlayerAndIsCurrentlyActiveTrue(targetPlayer);
        if(charActiveMove.isPresent()) {
            String destinationRegion =  charActiveMove.get().getPath().getDestination();
            log.warn("Character [{}] is currently moving to region [{}] and therefore cannot be bound to army [{}]!", targetPlayer, destinationRegion, army.getName());
            throw ArmyServiceException.cannotBindCharIsMoving(army.getName(), destinationRegion);
        }

        log.debug("Binding army [{}] to player [{}]...", army.getName(), targetPlayer);
        army.setBoundTo(targetPlayer);

        log.debug("Persisting newly changed army...");
        army = secureSave(army, armyRepository);

        log.info("Bound {} [{}] to player [{}]!", army.getArmyType().name(), army.getName(), targetPlayer);
        return army;
    }
}
