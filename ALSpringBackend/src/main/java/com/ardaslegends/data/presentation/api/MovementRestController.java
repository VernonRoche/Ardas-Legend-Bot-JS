package com.ardaslegends.data.presentation.api;

import com.ardaslegends.data.domain.Movement;
import com.ardaslegends.data.presentation.AbstractRestController;
import com.ardaslegends.data.service.MovementService;
import com.ardaslegends.data.service.dto.army.MoveArmyDto;
import com.ardaslegends.data.service.dto.player.DiscordIdDto;
import com.ardaslegends.data.service.dto.player.rpchar.MoveRpCharDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(MovementRestController.BASE_URL)
public class MovementRestController extends AbstractRestController {

    private final MovementService movementService;

    public static final String BASE_URL = "/api/movement";

    public static final String PATH_MOVE_CHAR = "/move-char";
    public static final String PATH_MOVE_ARMY = "/move-army";
    public static final String PATH_CANCEL_CHAR_MOVEMENT = "/cancel-char-move";

    @PostMapping(PATH_MOVE_CHAR)
    public HttpEntity<Movement> moveRoleplayCharacter(@RequestBody MoveRpCharDto dto) {

        log.debug("Incoming Post Request to create rp char movement, data [{}]", dto);
        log.trace("WrappedServiceExecution of createRpCharMovement function");
        Movement movement = wrappedServiceExecution(dto, movementService::createRpCharMovement);

        log.info("Successfully handled request for creating rpchar movement!");
        return ResponseEntity.ok(movement);
    }

    @PatchMapping(PATH_CANCEL_CHAR_MOVEMENT)
    public HttpEntity<Movement> cancelRoleplayCharacterMove(@RequestBody DiscordIdDto dto) {

        log.debug("Incoming Post Request to create rp char movement, data [{}]", dto);
        log.trace("WrappedServiceExecution of cancelRpCharMovement function");
        Movement movement = wrappedServiceExecution(dto, movementService::cancelRpCharMovement);

        log.info("Successfully handled request for creating rpchar movement!");
        return ResponseEntity.ok(movement);
    }

    @PostMapping(PATH_MOVE_ARMY)
    public HttpEntity<Movement> moveArmy(@RequestBody MoveArmyDto dto) {

        log.debug("Incoming Post Request to create army movement, data [{}]", dto);
        log.trace("WrappedServiceExecution of createArmyMovement function");
        Movement movement = wrappedServiceExecution(dto, movementService::createArmyMovement);

        log.info("Successfully handled request for creating army movement!");
        return ResponseEntity.ok(movement);
    }
}
