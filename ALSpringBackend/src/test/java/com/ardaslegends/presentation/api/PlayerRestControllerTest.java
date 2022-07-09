package com.ardaslegends.presentation.api;

import com.ardaslegends.data.domain.Faction;
import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.domain.RPChar;
import com.ardaslegends.data.presentation.api.PlayerRestController;
import com.ardaslegends.data.presentation.exceptions.BadArgumentException;
import com.ardaslegends.data.presentation.exceptions.InternalServerException;
import com.ardaslegends.data.service.FactionService;
import com.ardaslegends.data.service.PlayerService;
import com.ardaslegends.data.service.dto.player.*;
import com.ardaslegends.data.service.dto.player.rpchar.CreateRPCharDto;
import com.ardaslegends.data.service.dto.player.rpchar.UpdateRpCharDto;
import com.ardaslegends.data.service.exceptions.ServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.persistence.PersistenceException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
public class PlayerRestControllerTest {

    MockMvc mockMvc;

    private PlayerService mockPlayerService;
    private FactionService mockFactionService;

    private PlayerRestController playerRestController;

    @BeforeEach
    void setup() {
        mockPlayerService = mock(PlayerService.class);
        mockFactionService = mock(FactionService.class);
        playerRestController = new PlayerRestController(mockPlayerService, mockFactionService);
        mockMvc = MockMvcBuilders.standaloneSetup(playerRestController).build();
    }

    // Create Method Tests

    @Test
    void ensureCreatePlayerWorksProperly() {
        // Assign
        CreatePlayerDto dto = new CreatePlayerDto("vernon", "roche", "Mordor");
        Player returnedPlayer =  Player.builder().ign(dto.ign()).discordID(dto.discordID()).build();
        when(mockPlayerService.createPlayer(dto)).thenReturn(returnedPlayer);

        // Act
        var result = playerRestController.createPlayer(dto);

        // Assert
        assertThat(result.getBody()).isEqualTo(returnedPlayer);
    }

    // ---------------------------------------------------    Create RPChar Test

    @Test
    void ensureCreateRpCharWorksProperly() throws Exception {
        // Assign
        when(mockPlayerService.createRoleplayCharacter(any())).thenReturn(new RPChar());

        CreateRPCharDto dto = new CreateRPCharDto("MiraksID", "Rando", "Rando King", "Gondolin", true);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

        String requestJson = ow.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders
                .post("http://localhost:8080/api/player/create/rpchar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    // Read Methods Test

    // by Ign
    @Test
    void ensureGetByIgnWorksProperly() {
        // Assign
        String ign = "luktronic";
        Player p = Player.builder().ign(ign).build();
        when(mockPlayerService.getPlayerByIgn(ign)).thenReturn(p);

        // Act
        var result = playerRestController.getByIgn(ign);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getBody()).isEqualTo(p);
    }

    // by DiscordId

    @Test
    void ensureGetByDiscordIdWorksProperly() {
        // Assign
        String discId = "luktronic";
        Player p = Player.builder().discordID(discId).build();
        when(mockPlayerService.getPlayerByDiscordId(discId)).thenReturn(p);

        // Act
        var result = playerRestController.getByDiscordId(discId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getBody()).isEqualTo(p);
    }

    @Test
    void ensureUpdatePlayerFactionWorks() throws Exception {
        log.debug("Testing if update Player Faction Works...");

        // Assign

        log.trace("Initializing factions");
        Faction gondor = Faction.builder().name("Gondor").build();
        Faction mordor = Faction.builder().name("Mordor").build();

        log.trace("Initializing Player");
        Player player = Player.builder().ign("mirak441").discordID("123456789").faction(gondor).build();
        Player updatedPlayer = Player.builder().ign("mirak441").discordID("123456789").faction(mordor).build();

        log.trace("Initializing UpdatePlayerDto");
        UpdatePlayerFactionDto updatePlayerFactionDto = new UpdatePlayerFactionDto(player.getDiscordID(), mordor.getName());

        log.trace("Initializing mocked methods");
        when(mockFactionService.getFactionByName("Gondor")).thenReturn(gondor);
        when(mockFactionService.getFactionByName("Mordor")).thenReturn(mordor);
        when(mockPlayerService.getPlayerByIgn(player.getIgn())).thenReturn(player);
        when(mockPlayerService.updatePlayerFaction(updatePlayerFactionDto)).thenReturn(updatedPlayer);

        log.trace("Building JSON for UpdatePlayerDto");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(updatePlayerFactionDto);

        //Act

        String url = "http://localhost:8080/api/player/update/faction";

        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
        log.debug("Post Request returned status OK");

        log.info("Test passed: updatePlayerFaction works properly when using correct values!");
    }

    // Update Ign Tests

    @Test
    void ensureUpdateIgnWorksProperly() throws Exception {
        log.debug("Testing if update ign works properly");

        // Assign

        log.trace("Initializing Player Object");
        Player returnedPlayer = Player.builder().ign("Player").build();

        log.trace("Initializing UpdateIgn Data Transfer Object");
        UpdatePlayerIgnDto dto = new UpdatePlayerIgnDto("Random", "RandomId");

        log.trace("Initializing mocked method");
        when(mockPlayerService.updateIgn(dto)).thenReturn(returnedPlayer);

        log.trace("Building JSON from UpdatePlayerIgnDto");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act

        String url = "http://localhost:8080/api/player/update/ign";

        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                .patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());

        log.debug("Patch Request returned status OK");

        log.info("Test passed: updateIgn works properly when using correct values!");
    }

    // Update DiscordId

    @Test
    void ensureUpdateDiscordIdWorksProperly() throws Exception {
        log.debug("Testing if update discordId works properly");

        // Assign

        log.trace("Initializing Player Object");
        Player returnedPlayer = Player.builder().ign("Player").build();

        log.trace("Initializing UpdateDiscordId Data Transfer Object");
        UpdateDiscordIdDto dto = new UpdateDiscordIdDto("RandomOld", "RandomNew");

        log.trace("Initializing mocked method");
        when(mockPlayerService.updateDiscordId(dto)).thenReturn(returnedPlayer);

        log.trace("Building JSON from UpdatePlayerDiscordIdDto");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act

        String url = "http://localhost:8080/api/player/update/discordid";

        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());

        log.debug("Patch Request returned status OK");

        log.info("Test passed: updateDiscordId works properly when using correct values!");
    }

    // Update Character Name

    @Test
    void ensureUpdateCharacterNameWorksProperly() throws Exception {
        log.debug("Testing if updateCharacterName works properly with correct values");

        // Assign
        log.trace("Initializing dto");
        UpdateRpCharDto dto = new UpdateRpCharDto("rando", "wee", null, null, null, null, null);

        log.trace("Initialize RpChar");
        RPChar rpChar = RPChar.builder().name(dto.charName()).build();

        log.trace("Initializing mock methods");
        when(mockPlayerService.updateCharacterName(dto)).thenReturn(rpChar);

        log.trace("Building JSON from UpdateRpCharDto");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act
        String url = "http://localhost:8080/api/player/update/rpchar/name";
        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                .patch(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());
    }

    // Update title

    @Test
    void ensureUpdateCharacterTitleWorksProperly() throws Exception {
        log.debug("Testing if updateCharacterTitle works properly with correct values");

        // Assign
        log.trace("Initializing dto");
        UpdateRpCharDto dto = new UpdateRpCharDto("rando", null,"SauronKing", null, null, null, null);

        log.trace("Initialize RpChar");
        RPChar rpChar = RPChar.builder().name(dto.charName()).build();

        log.trace("Initializing mock methods");
        when(mockPlayerService.updateCharacterTitle(dto)).thenReturn(rpChar);

        log.trace("Building JSON from UpdateRpCharDto");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act
        String url = "http://localhost:8080/api/player/update/rpchar/title";
        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    // Update Gear
    @Test
    void ensureUpdateCharacterGearWorksProperly() throws Exception {
        log.debug("Testing if updateCharacterGear works properly with correct values");

        // Assign
        log.trace("Initializing dto");
        UpdateRpCharDto dto = new UpdateRpCharDto("rando", null,"SauronKing", null, null, null, null);

        log.trace("Initialize RpChar");
        RPChar rpChar = RPChar.builder().name(dto.charName()).build();

        log.trace("Initializing mock methods");
        when(mockPlayerService.updateCharacterGear(dto)).thenReturn(rpChar);

        log.trace("Building JSON from UpdateRpCharDto");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act
        String url = "http://localhost:8080/api/player/update/rpchar/gear";
        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    // Update PvP

    @Test
    void ensureUpdateCharacterPvPWorksProperly() throws Exception {
        log.debug("Testing if updateCharacterPvp works properly with correct values");

        // Assign
        log.trace("Initializing dto");
        UpdateRpCharDto dto = new UpdateRpCharDto("rando", null,"SauronKing", null, null, null, null);

        log.trace("Initialize RpChar");
        RPChar rpChar = RPChar.builder().name(dto.charName()).build();

        log.trace("Initializing mock methods");
        when(mockPlayerService.updateCharacterGear(dto)).thenReturn(rpChar);

        log.trace("Building JSON from UpdateRpCharDto");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act
        String url = "http://localhost:8080/api/player/update/rpchar/pvp";
        log.debug("Performing Patch request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }
    // ------------------------------------------- Delete Methods

    // Delete Player

    @Test
    void ensureDeletePlayerWorksProperly() throws Exception {
        log.debug("Testing if deletePlayer works properly");

        // Assign

        log.trace("Initializing Dto");
        DiscordIdDto dto = new DiscordIdDto("RandomId");

        log.trace("Initializing player object");
        Player player = Player.builder().discordID(dto.discordId()).build();

        log.trace("Initializing mock methods");
        when(mockPlayerService.deletePlayer(dto)).thenReturn(player);

        log.trace("Building JSON from DiscordIdDto");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act

        String url = "http://localhost:8080/api/player/delete";

        log.debug("Performing Delete request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    // Delete RpChar

    @Test
    void ensureDeleteRpCharWorksProperly() throws Exception {
        log.debug("Testing if RpChar works properly");

        // Assign

        log.trace("Initializing Dto");
        DiscordIdDto dto = new DiscordIdDto("RandomId");

        log.trace("Initializing rpchar object");
        RPChar rpChar = RPChar.builder().name("RandomChar").build();

        log.trace("Initializing mock methods");
        when(mockPlayerService.deleteRpChar(dto)).thenReturn(rpChar);

        log.trace("Building JSON from DeletePlayerOrRpcharDto");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(dto);

        // Act

        String url = "http://localhost:8080/api/player/delete/rpchar";

        log.debug("Performing Delete request to {}", url);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }


}
