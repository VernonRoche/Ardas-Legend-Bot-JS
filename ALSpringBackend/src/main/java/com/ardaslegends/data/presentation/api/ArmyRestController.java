package com.ardaslegends.data.presentation.api;

import com.ardaslegends.data.domain.Army;
import com.ardaslegends.data.presentation.AbstractRestController;
import com.ardaslegends.data.service.ArmyService;
import com.ardaslegends.data.service.dto.army.CreateArmyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(ArmyRestController.BASE_URL)
public class ArmyRestController extends AbstractRestController {
    public static final String BASE_URL = "/api/army";
    public static final String PATH_CREATE_ARMY = "/create";

    private final ArmyService armyService;

    @PostMapping(PATH_CREATE_ARMY)
    public HttpEntity<Army> createArmy(@RequestBody CreateArmyDto dto) {
        log.debug("Incoming createArmy Request: Data [{}]", dto);

        log.debug("Calling ArmyService.createArmy");
        Army createdArmy = wrappedServiceExecution(dto, armyService::createArmy);

        log.info("Successfully created Army [{}]", createdArmy.getName());
        return ResponseEntity.ok(createdArmy);
    }
}
