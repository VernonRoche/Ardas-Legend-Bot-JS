package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.Army;
<<<<<<< Updated upstream
import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.repository.ArmyRepository;
import com.ardaslegends.data.service.dto.army.BindArmyDto;
import com.ardaslegends.data.service.exceptions.army.ArmyServiceException;
=======
import com.ardaslegends.data.domain.Faction;
import com.ardaslegends.data.repository.ArmyRepository;
import com.ardaslegends.data.repository.FactionRepository;
import com.ardaslegends.data.repository.UnitTypeRepository;
import com.ardaslegends.data.service.dto.army.CreateArmyDto;
>>>>>>> Stashed changes
import com.ardaslegends.data.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

<<<<<<< Updated upstream
import java.util.Optional;
=======
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
>>>>>>> Stashed changes

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class ArmyService extends AbstractService<Army, ArmyRepository> {
    private final ArmyRepository armyRepository;
    private final PlayerService playerService;
    private final FactionRepository factionRepository;
    private final UnitTypeService unitTypeService;

    public Army createArmy(CreateArmyDto dto) {
        log.debug("Creating army with data [{}]", dto);

        ServiceUtils.checkAllNulls(dto);
        ServiceUtils.checkAllBlanks(dto);
        Arrays.stream(dto.units()).forEach(unitTypeDto -> ServiceUtils.checkAllBlanks(unitTypeDto));

        log.debug("Fetching required Data");

        log.trace("Fetching if an army with name [{}] already exists.", dto.name());
        Optional<Army> fetchedArmy = secureFind(dto.name(), armyRepository::findById);

        if(fetchedArmy.isPresent()) {
            log.warn("Army with name [{}] already exists");
            throw new IllegalArgumentException("Army with name %s already exists, please choose a different name".formatted(dto.name()));
        }

        log.trace("Fetching Faction with name [{}]", dto.faction());
        Optional<Faction> fetchedFaction = secureFind(dto.faction(), factionRepository::findById);

        if(fetchedFaction.isEmpty()) {
            log.warn("No faction found with name [{}]", dto.faction());
            throw new IllegalArgumentException("No faction found that has the name \"%s\"".formatted(dto.faction()));
        }

        log.debug("Assembling Units Map, fetching units");
        var units = Arrays.stream(dto.units())
                .collect(Collectors.toMap(
                        unitTypeDto -> unitTypeService.getUnitTypeByName(unitTypeDto.unitTypeName()),
                        unitTypeDto -> unitTypeDto.amount()
                ));

        // Not finished
        return null;
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
        if(!isBindingSelf) {
            //TODO Check for lords as well
            log.trace("Executor and target are not equal - checking if executor is faction leader");
            if(!executor.equals(executor.getFaction().getLeader())) {
                log.warn("Executor player [{}] is not faction leader of faction [{}]!", executor, executor.getFaction());
                throw ArmyServiceException.notFactionLeader(executor.getIgn(), executor.getFaction().getName());
            }
        }

        /*
        Setting target player
        If not binding self then fetch the target player from DB
         */
        log.trace("Getting the target player's instance");
        Player targetPlayer = null;
        if(isBindingSelf)
            targetPlayer = executor;
        else
            targetPlayer = playerService.getPlayerByDiscordId(dto.targetDiscordId());

        log.debug("Fetching the army [{}]", dto.armyName());
        Optional<Army> fetchedArmy = armyRepository.findArmyByName(dto.armyName());

        if(fetchedArmy.isEmpty()) {
            log.warn("No army found with the name [{}]!", dto.armyName());
            throw ArmyServiceException.noArmyWithName(dto.armyName());
        }
        Army army = fetchedArmy.get();
        log.debug("Found army [{}] - type: [{}]", army.getName(), army.getArmyType().name());

        log.debug("Binding army [{}] to player [{}]...", army.getName(), targetPlayer);
        army.setBoundTo(targetPlayer);

        log.debug("Persisting newly changed army...");
        army = secureSave(army, armyRepository);

        log.info("Bound {} [{}] to player [{}]!", army.getArmyType().name(), army.getName(), targetPlayer);
        return army;
}