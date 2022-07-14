package com.ardaslegends.repository;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.ArmyRepository;
import com.ardaslegends.data.repository.MovementRepository;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.Opt;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
public class MovementRepositoryTest {

    @Autowired
    MovementRepository repository;

    @Autowired
    ArmyRepository armyRepository;

    @Test
    void ensureFindByArmyAndIsCurrentlyActiveWorks() {
        log.debug("Testing if MovementRepository query: findByArmyAndIsCurrentlyActive works properly!");

        log.trace("Calling query, expecting empty Optional");
        var result = repository.findMovementByArmyAndIsCurrentlyActiveTrue(Army.builder().name("Kek").build());
        log.trace("Is Optional empty? [{}]", result.isEmpty());
        assertThat(result.isEmpty()).isTrue();

        log.trace("Saving test entity");
        Region region = Region.builder()
                .id("KekRegion")
                .build();
        Army kekArmy = Army.builder()
                .name("Kek")
                .armyType(ArmyType.ARMY)
                .faction(Faction.builder()
                        .name("KekFaction").build())
                .currentRegion(region)
                .freeTokens(20)
                .build();
        kekArmy = armyRepository.save(kekArmy);
        Movement movement = Movement.builder()
                .army(kekArmy)
                .isCurrentlyActive(true)
                .build();

        Movement movement2 = Movement.builder()
                .army(kekArmy)
                .isCurrentlyActive(false)
                .build();

        repository.save(movement2);

        log.trace("Calling query, expecting that a result is empty");
        result = repository.findMovementByArmyAndIsCurrentlyActiveTrue(Army.builder().name("Kek").build());
        log.trace("Is Optional empty? [{}]", result.isEmpty());
        assertThat(result.isEmpty()).isTrue();

        log.trace("Saving correct entity");
        repository.save(movement);

        log.trace("Calling query, expecting that a result is present");
        result = repository.findMovementByArmyAndIsCurrentlyActiveTrue(Army.builder().name("Kek").build());
        log.trace("Is Optional present? [{}]", result.isPresent());
        assertThat(result.isPresent()).isTrue();
    }
}
