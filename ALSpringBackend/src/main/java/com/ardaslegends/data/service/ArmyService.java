package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.repository.ArmyRepository;
import com.ardaslegends.data.service.dto.army.BindArmyDto;
import com.ardaslegends.data.service.exceptions.army.ArmyServiceException;
import com.ardaslegends.data.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class ArmyService extends AbstractService<Army, ArmyRepository> {
    private final ArmyRepository armyRepository;
    private final PlayerService playerService;

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
}
