package com.ardaslegends.data.presentation.api;

import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.domain.RPChar;
import com.ardaslegends.data.presentation.AbstractRestController;
import com.ardaslegends.data.presentation.exceptions.BadArgumentException;
import com.ardaslegends.data.presentation.exceptions.InternalServerException;
import com.ardaslegends.data.service.FactionService;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.dto.player.*;
import com.ardaslegends.data.service.dto.player.rpchar.CreateRPCharDto;
import com.ardaslegends.data.service.dto.player.rpchar.UpdateRpCharDto;
import com.ardaslegends.data.service.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RequiredArgsConstructor

@Slf4j
@RestController
@RequestMapping(PlayerRestController.BASE_URL)
public class PlayerRestController extends AbstractRestController {

    public final static String BASE_URL = "/api/player";

    public static final String PATH_CREATE_PLAYER = "/create";
    public static final String PATH_CREATE_RPCHAR = "/create/rpchar";

    public static final String PATH_UPDATE_FACTION = "/update/faction";
    public static final String PATH_UPDATE_IGN = "/update/ign";
    public static final String PATH_UPDATE_DISCORDID = "/update/discordid";
    public static final String PATH_UPDATE_RPCHAR_NAME = "/update/rpchar/name";
    public static final String PATH_UPDATE_RPCHAR_TITLE = "/update/rpchar/title";
    public static final String PATH_UPDATE_RPCHAR_GEAR = "/update/rpchar/gear";
    public static final String PATH_UPDATE_RPCHAR_PVP = "/update/rpchar/pvp";


    public static final String PATH_DELETE_PLAYER = "/delete";
    public static final String PATH_DELETE_RPCHAR = "/delete/rpchar";

    public static final String PATH_GET_BY_IGN = "/getByIgn/{ign}";
    public static final String PATH_GET_BY_DISCORD_ID = "/getByDiscId/{discId}";

    private final PlayerService playerService;
    private final FactionService factionService;

    @GetMapping(PATH_GET_BY_IGN)
    public HttpEntity<Player> getByIgn(@PathVariable String ign) {
        log.debug("Incoming getByIgn Request. Ign: {}", ign);

        log.debug("Calling PlayerService.getPlayerByIgn, Ign: {}", ign);
        Player playerFound = wrappedServiceExecution(ign, playerService::getPlayerByIgn);

        log.info("Successfully fetched player ({}) by ign ({})", playerFound, playerFound.getIgn());
        return ResponseEntity.ok(playerFound);
    }

    @GetMapping(PATH_GET_BY_DISCORD_ID)
    public HttpEntity<Player> getByDiscordId(@PathVariable String discId) {
        log.debug("Incoming getByDiscordId Request. DiscordId: {}", discId);

        log.debug("Calling PlayerService.getPlayerByDiscordId, DiscordId: {}", discId);
        Player playerFound = wrappedServiceExecution(discId, playerService::getPlayerByDiscordId);

        log.info("Successfully fetched player ({}) by DiscordId ({})", playerFound, playerFound.getDiscordID());
        return ResponseEntity.ok(playerFound);
    }

    @PostMapping(PATH_CREATE_PLAYER)
    public HttpEntity<Player> createPlayer(@RequestBody CreatePlayerDto createPlayerDto) {
        log.debug("Incoming createPlayer Request. Data [{}]", createPlayerDto);

        log.debug("Calling PlayerService.createPlayer. Data {}" ,createPlayerDto);
        Player createdPlayer = wrappedServiceExecution(createPlayerDto, playerService::createPlayer);

        URI self = UriComponentsBuilder.fromPath(BASE_URL + PATH_GET_BY_IGN)
                .uriVariables(Map.of("ign", createdPlayer.getIgn()))
                .build().toUri();
        log.debug("URI built. Data {}, URI {}", createPlayerDto, self.toString());

        log.info("Sending HttpResponse with successfully created Player {}", createdPlayer);
        return ResponseEntity.created(self).body(createdPlayer);
    }

    @PostMapping(PATH_CREATE_RPCHAR)
    public HttpEntity<RPChar> createRpChar(@RequestBody CreateRPCharDto createRPCharDto) {
        log.debug("Incoming createRpChar Request. Data [{}]", createRPCharDto);

        log.debug("Calling PlayerService.createRoleplayCharacter. Data [{}]", createRPCharDto);
        RPChar createdRpChar = wrappedServiceExecution(createRPCharDto, playerService::createRoleplayCharacter);

        log.info("Sending HttpResponse with successfully created Player {}", createdRpChar);
        return ResponseEntity.ok(createdRpChar);
    }


    @PatchMapping(PATH_UPDATE_FACTION)
    public HttpEntity<Player> updatePlayerFaction(@RequestBody UpdatePlayerFactionDto updatePlayerFactionDto) {
        log.debug("Incoming updatePlayerFaction Request. Data {}", updatePlayerFactionDto);

        log.trace("Trying to update the player's faction");
        Player player = wrappedServiceExecution(updatePlayerFactionDto, playerService::updatePlayerFaction);
        log.debug("Successfully updated faction without encountering any errors");

        log.trace("Building URI for player...");
        URI self = UriComponentsBuilder.fromPath(BASE_URL + PATH_GET_BY_IGN)
                .uriVariables(Map.of("ign", player.getIgn()))
                .build().toUri();
        log.debug("URI built. Data {}, URI {}", updatePlayerFactionDto, self.toString());

        log.debug("Updating player done!");
        log.info("Sending HttpResponse with successfully updated Player {}", player);
        return ResponseEntity.ok(player);
    }

    @PatchMapping(PATH_UPDATE_IGN)
    public HttpEntity<Player> updatePlayerIgn(@RequestBody UpdatePlayerIgnDto dto) {

        log.debug("Incoming updatePlayerIgn Request: Data [{}]", dto);

        log.trace("Trying to update the player's ingame name");
        Player player = wrappedServiceExecution(dto, playerService::updateIgn);
        log.debug("Successfully updated faction without encountering any errors");

        log.info("Sending HttpResponse with successfully updated Player {}", player);
        return ResponseEntity.ok(player);
    }

    @PatchMapping(PATH_UPDATE_DISCORDID)
    public HttpEntity<Player> updatePlayerDiscordId(@RequestBody UpdateDiscordIdDto dto) {
        log.debug("Incoming updateDiscordId Request: Data [{}]", dto);

        log.trace("Trying to update the player's executorDiscordId");
        Player player = wrappedServiceExecution(dto, playerService::updateDiscordId);
        log.debug("Successfully updated executorDiscordId without encountering any errors");

        log.info("Sending HttpResponse with successfully updated Player {}", player);
        return ResponseEntity.ok(player);
    }

    @PatchMapping(PATH_UPDATE_RPCHAR_NAME)
    public HttpEntity<RPChar> updateCharacterName(@RequestBody UpdateRpCharDto dto) {

        log.debug("Incoming updateCharacterName Request: Data [{}]", dto);

        log.trace("Executing playerService.updateCharacterName");
        RPChar rpChar = wrappedServiceExecution(dto, playerService::updateCharacterName);
        log.debug("Successfully updated character name without encountering any errors");

        log.info("Sending HttpResponse with successfully updated RPChar [{}]", rpChar);
        return ResponseEntity.ok(rpChar);
    }

    @PatchMapping(PATH_UPDATE_RPCHAR_TITLE)
    public HttpEntity<RPChar> updateCharacterTitle(@RequestBody UpdateRpCharDto dto) {

        log.debug("Incoming updateCharacterTitle Request: Data [{}]", dto);

        log.trace("Executing playerService.updateCharacterTitle");
        RPChar rpChar = wrappedServiceExecution(dto, playerService::updateCharacterTitle);
        log.debug("Successfully updated character title without encountering any errors");

        log.info("Sending HttpResponse with successfully updated RPChar [{}]", rpChar);
        return ResponseEntity.ok(rpChar);
    }

    @PatchMapping(PATH_UPDATE_RPCHAR_GEAR)
    public HttpEntity<RPChar> updateCharacterGear(@RequestBody UpdateRpCharDto dto) {

        log.debug("Incoming updateCharacterGear Request: Data [{}]", dto);

        log.trace("Executing playerService.updateCharacterGear");
        RPChar rpChar = wrappedServiceExecution(dto, playerService::updateCharacterGear);
        log.debug("Successfully updated character Gear without encountering any errors");

        log.info("Sending HttpResponse with successfully updated RPChar [{}]", rpChar);
        return ResponseEntity.ok(rpChar);
    }

    @PatchMapping(PATH_UPDATE_RPCHAR_PVP)
    public HttpEntity<RPChar> updateCharacterPvP(@RequestBody UpdateRpCharDto dto) {

        log.debug("Incoming updateCharacterPvP Request: Data [{}]", dto);

        log.trace("Executing playerService.updateCharacterPvP");
        RPChar rpChar = wrappedServiceExecution(dto, playerService::updateCharacterPvp);
        log.debug("Successfully updated character PvP without encountering any errors");

        log.info("Sending HttpResponse with successfully updated RPChar [{}]", rpChar);
        return ResponseEntity.ok(rpChar);
    }
    @DeleteMapping(PATH_DELETE_PLAYER)
    public HttpEntity<Player> deletePlayer(@RequestBody DiscordIdDto dto) {

        log.debug("Incoming deletePlayer Request: Data [{}]", dto);

        log.trace("Executing playerService.deletePlayer");
        Player player = wrappedServiceExecution(dto, playerService::deletePlayer);
        log.debug("Successfully deleted player, [{}]", player);

        log.info("Sending HttpResponse with successfully deleted Player [{}]", player);
        return ResponseEntity.ok(player);
    }

    @DeleteMapping(PATH_DELETE_RPCHAR)
    public HttpEntity<RPChar> deleteRpChar(@RequestBody DiscordIdDto dto) {

        log.debug("Incoming deleteRpChar Request: Data [{}]", dto);

        log.trace("Executing playerService.deleteRpChar");
        RPChar rpChar = wrappedServiceExecution(dto,playerService::deleteRpChar);
        log.debug("Successfully deleted rpchar, [{}]", rpChar);

        log.info("Sending HttpResponse with successfully deleted RpChar [{}]", rpChar);
        return ResponseEntity.ok(rpChar);
    }
}
