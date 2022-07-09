package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.PlayerRepository;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.dto.player.*;
import com.ardaslegends.data.service.dto.player.rpchar.CreateRPCharDto;
import com.ardaslegends.data.service.dto.player.rpchar.UpdateRpCharDto;
import com.ardaslegends.data.service.exceptions.ServiceException;
import com.ardaslegends.data.service.external.MojangApiService;
import com.ardaslegends.data.service.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Responsible for the CRUD-Operations for the UserEntity
 */
@RequiredArgsConstructor
@Slf4j

@Service
@Transactional(readOnly = true)
public class PlayerService extends AbstractService<Player, PlayerRepository> {

    private final PlayerRepository playerRepository;

    private final FactionService factionService;
    private final MojangApiService mojangApiService;

    @Transactional(readOnly = false)
    public Player createPlayer(CreatePlayerDto dto) {

        // Checking if input data is valid

        log.debug("Creating Player with Data: {}", dto);

        ServiceUtils.checkNulls(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toList()));

        ServiceUtils.checkBlanks(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toList()));
        // Executing queries for required data

        Faction queriedFaction = factionService.getFactionByName(dto.faction());

        UUIDConverterDto uuidConverterDto = mojangApiService.getUUIDByIgn(dto.ign());

        log.debug("Querying Player by IGN {}", dto.ign());
        var userQueriedById = secureFind(dto.ign(), playerRepository::findPlayerByIgn);
        log.debug("Result of queryById: {}", userQueriedById.orElse(null));

        if (userQueriedById.isPresent()) {
            log.warn("Player with same IGN has been found! Data {}, Player {}", dto, userQueriedById.get());
            throw ServiceException.cannotCreateEntityThatAlreadyExists(userQueriedById.get());
        }

        log.debug("Querying Player by DiscordId {}", dto.discordID());
        var userQueriedByDiscordID = secureFind(dto.discordID(), playerRepository::findByDiscordID);

        if (userQueriedByDiscordID.isPresent()) {
            log.warn("Player with same IGN or DiscordId has been found! Data {}, Player {}", dto, userQueriedByDiscordID);
            throw ServiceException.cannotCreateEntityThatAlreadyExists(userQueriedByDiscordID.get());
        }
        log.debug("No player with ign, {} and executorDiscordId, {} found in database", dto.ign(), dto.discordID());

        // Creating player and Saving into database

        log.debug("Trying to create and save entity");
        Player player = Player.builder().ign(dto.ign()).uuid(uuidConverterDto.id()).discordID(dto.discordID()).faction(queriedFaction).build();

        log.debug("Persisting player entity with ign {}, executorDiscordId {} into the database", dto.ign(), dto.discordID());
        player = secureSave(player, playerRepository);

        log.info("Created Player: {}", dto);
        return player;
    }

    @Transactional(readOnly = false)
    public RPChar createRoleplayCharacter(CreateRPCharDto dto) {

        // Checking if input data is valid

        log.debug("Creating Roleplay Character with Data {}", dto);

        ServiceUtils.checkNulls(dto, Arrays.stream(dto.getClass().getDeclaredFields()).map(field -> field.getName()).collect(Collectors.toList()));

        ServiceUtils.checkBlanks(dto, Arrays.stream(dto.getClass().getDeclaredFields()).
                filter(field -> field.getType().
                        isAssignableFrom(String.class))
                .map(field -> field.getName())
                .collect(Collectors.toList()));

        if(dto.title().length() > 25) {
            log.warn("CreateRPChar title is too long");
            throw new IllegalArgumentException("Title exceeds maximum length of 25 Characters [%s, Length %s]".formatted(dto.title(), dto.title().length()));
        }

        // Executing queries for required data

        log.debug("Fetching required Data..");

        Player actualPlayer = getPlayerByDiscordId(dto.discordId());             // Throws IllegalArgument, NullPointer or ServiceException if it does not succeed
        log.debug("Fetched Player by DiscordId [{}]", actualPlayer);

        log.debug("Checking if the player has a faction");
        if(actualPlayer.getFaction() == null) {
            log.warn("Player [{}] is not in a faction - cannot create new RpChar!", actualPlayer);
            throw ServiceException.createRpCharNoFaction(actualPlayer.getIgn());
        }

        // Checking if the player already has an RPChar
        if (actualPlayer.getRpChar() != null) {
            log.warn("Player [{}] already has an RPChar", actualPlayer);
            throw new IllegalArgumentException("Player [%s] already has an RPChar [%s], delete the old one if you want a new Character!".formatted(actualPlayer, actualPlayer.getRpChar()));
        }

        Optional<Player> fetchedPlayer = secureFind(dto.rpCharName(), playerRepository::findPlayerByRpChar);

        if (fetchedPlayer.isPresent()) {
            log.warn("Player found with same RPChar Name [{}], [{}]", actualPlayer, dto.rpCharName());
            throw new IllegalArgumentException("Roleplay Character with name [%s] already exists!".formatted(dto.rpCharName()));
        }
        log.debug("No RPChar with name [{}] found - continuing with creation", dto.rpCharName());

        // Saving RPChar to the Database

        log.debug("Creating RpChar instance");
        RPChar createdChar = new RPChar(dto.rpCharName(), dto.title(), dto.gear(), dto.pvp(), actualPlayer.getFaction().getHomeRegion(), null);

        log.debug("Trying to persist RPChar [{}]", createdChar);
        actualPlayer.setRpChar(createdChar);

        actualPlayer = secureSave(actualPlayer, playerRepository);

        log.info("Created RPChar [{}]", createdChar);
        return actualPlayer.getRpChar();
    }

    public Player getPlayerByIgn(String ign) {
        log.debug("Fetching Player with Ign: {}", ign);
        Objects.requireNonNull(ign, "IGN must not be null!");

        Optional<Player> fetchedPlayer = secureFind(ign, playerRepository::findPlayerByIgn);

        if (fetchedPlayer.isEmpty()) {
            log.warn("No player with ign {} found!", ign);
            throw ServiceException.cannotReadEntityDueToNotExisting(Player.class.getSimpleName(), "ign", ign);
        }
        log.info("Successfully fetched player: {}", fetchedPlayer.get());
        return fetchedPlayer.get();
    }

    public Player getPlayerByDiscordId(String discordId) {
        log.debug("Fetching Player with Discord ID: {}", discordId);
        Objects.requireNonNull(discordId, "DiscordId must not be null!");

        Optional<Player> fetchedPlayer = secureFind(discordId, playerRepository::findByDiscordID);

        if (fetchedPlayer.isEmpty()) {
            log.warn("No player with executorDiscordId {} found!", discordId);
            throw ServiceException.cannotReadEntityDueToNotExisting(Player.class.getSimpleName(), "executorDiscordId", discordId);
        }
        log.info("Successfully fetched player: {}", fetchedPlayer.get());
        return fetchedPlayer.get();
    }

    @Transactional(readOnly = false)
    public Player updatePlayerFaction(UpdatePlayerFactionDto dto) {
        log.debug("Updating the Faction of player '{}' to '{}'", dto.discordId(), dto.factionName());

        /*
        Validating input
         */
        log.trace("Checking if input data is present...");
        ServiceUtils.checkNulls(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toList()));

        ServiceUtils.checkBlanks(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toList()));


        /*
        Finding the player instance by the ign
         */

        Player player = null;

        /*
        I don't have to put this into a try-catch block because the getPlayerByIgn function already
        throws all the needed exceptions
         */
        log.trace("Trying to search for player with ign '{}'", dto.discordId());
        player = getPlayerByDiscordId(dto.discordId());
        log.debug("Found player '{}'", dto.discordId());

        /*
        Finding the Faction instance by faction name
         */

         /*
        I don't have to put this into a try-catch block because the getPlayerByIgn function already
        throws all the needed exceptions
         */
        log.trace("Trying to search for Faction with name '{}'", dto.factionName());
        Faction faction = factionService.getFactionByName(dto.factionName());
        log.debug("Found Faction '{}'", dto.factionName());

        //Saving the old faction so I can log it later on
        Faction oldFaction = player.getFaction();

        /*
        Updating the Faction and saving the player
         */

        log.trace("Setting faction of player '{}' to '{}'", player.getIgn(), faction);
        player.setFaction(faction);

        log.debug("Saving the updated Player to database...");
        player = secureSave(player, playerRepository);

        log.info("Updated faction of player '{}' - From '{}' to '{}'", player.getIgn(), oldFaction.getName(), player.getFaction().getName());
        return player;
    }

    @Transactional(readOnly = false)
    public Player updateIgn(UpdatePlayerIgnDto dto) {
        log.debug("Updating ign of player [{}]", dto);

        // Checking if data is valid

        ServiceUtils.checkNulls(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toList()));

        ServiceUtils.checkBlanks(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toList()));


        // Get the player who issued the command
        Player playerToUpdate = getPlayerByDiscordId(dto.discordId());

        // Check if somebody already has this IGN
        Optional<Player> playerByIgn = secureFind(dto.ign(), playerRepository::findPlayerByIgn);

        if(playerByIgn.isPresent()) {
            log.warn("Player with ign [{}] already existing!", dto.ign());
            throw new IllegalArgumentException("A player with the IGN [%s] [%s] already exists".formatted(dto.ign(), playerByIgn.get()));
        }
        log.debug("No player with ign [{}] existing yet - continuing", dto.ign());

        UUIDConverterDto uuidConverterDto = mojangApiService.getUUIDByIgn(dto.ign());
        log.debug("uuId to ign returned [{}]", dto);

        //Save old IGN for later logging
        String oldIgn = playerToUpdate.getIgn();

        // Mutate player Object
        playerToUpdate.setIgn(dto.ign());
        playerToUpdate.setUuid(uuidConverterDto.id());

        playerToUpdate = secureSave(playerToUpdate, playerRepository);

        log.info("Successfully updated IGN of player '{}' from '{}' to '{}'!", playerToUpdate.getDiscordID(), oldIgn, playerToUpdate.getIgn());
        return playerToUpdate;
    }

    @Transactional(readOnly = false)
    public Player updateDiscordId(UpdateDiscordIdDto dto) {
        log.debug("Updating discord id, data [{}]", dto);

        // Checking if data is valid

        ServiceUtils.checkNulls(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toList()));

        ServiceUtils.checkBlanks(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toList()));

        // Get the player entity which is to be updated
        log.debug("Fetching old player");
        Player playerToUpdate = getPlayerByDiscordId(dto.oldDiscordId());

        // Check if a player is already registered with the new account
        log.debug("Fetching player with new to DiscordId to see if a player already exists");
        Optional<Player> fetchedPlayer = secureFind(dto.newDiscordId(), playerRepository::findByDiscordID);

        if(fetchedPlayer.isPresent()) {
            log.warn("Player found that is registered with the same executorDiscordId [{}] [{}]", dto.newDiscordId(), fetchedPlayer.get());
            throw new IllegalArgumentException("A Player with the new executorDiscordId already exists! [%s]".formatted(fetchedPlayer.get()));
        }

        log.debug("Update DiscordId of player");
        playerToUpdate.setDiscordID(dto.newDiscordId());

        log.debug("Trying to persist Player [{}]", playerToUpdate);
        playerToUpdate = secureSave(playerToUpdate, playerRepository);

        log.info("Successfully updated DiscordId of player '{}' from '{}' to '{}'!", playerToUpdate.getDiscordID(), dto.oldDiscordId(), playerToUpdate.getDiscordID());
        return playerToUpdate;
    }

    @Transactional(readOnly = false)
    public RPChar updateCharacterName(UpdateRpCharDto dto) {
        log.debug("Updating character name, data [{}]", dto);

        // Checking if data is valid

        ServiceUtils.checkNulls(dto, List.of("executorDiscordId", "charName"));

        ServiceUtils.checkBlanks(dto, List.of("executorDiscordId", "charName"));

        // Get the player entity which is to be updated
        log.debug("Fetching player");
        Player playerToUpdate = getPlayerByDiscordId(dto.discordId());

        // Check if player actually has an rpchar
        if (playerToUpdate.getRpChar() == null) {
            log.warn("No Rpchar found at player [{}]", playerToUpdate);
            throw new IllegalArgumentException("You do not have an RPChar and therefore cannot update its name!");
        }
        // Check if a player has already taken the name
        log.debug("Fetching player with new RpChar name to see if that name is already taken");
        Optional<Player> fetchedPlayer = secureFind(dto.charName(), playerRepository::findPlayerByRpChar);

        if(fetchedPlayer.isPresent()) {
            log.warn("Player found that has an Rpchar with the same name [{}] [{}]", dto.charName(), fetchedPlayer.get());
            throw new IllegalArgumentException("RpChar Name is already taken!".formatted(fetchedPlayer.get()));
        }

        log.debug("Update RpChar Name");
        playerToUpdate.getRpChar().setName(dto.charName());

        log.debug("Trying to persist player [{}]", playerToUpdate);
        playerToUpdate = secureSave(playerToUpdate, playerRepository);

        log.info("Successfully updated Rp Character Name of player [{}] to [{}]!", playerToUpdate, playerToUpdate.getRpChar().getName());
        return playerToUpdate.getRpChar();
    }

    @Transactional(readOnly = false)
    public RPChar updateCharacterTitle(UpdateRpCharDto dto) {
        log.debug("Updating character title to '{}' for player's ({}) rpchar '{}'", dto.title(), dto.discordId(), dto.charName());

        // Checking if data is valid
        log.debug("Validating DTO data");
        ServiceUtils.checkNulls(dto, List.of("executorDiscordId", "title"));
        ServiceUtils.checkBlanks(dto, List.of("executorDiscordId", "title"));

        if(dto.title().length() > 25) {
            log.warn("updateCharacterTitle - title is too long");
            throw new IllegalArgumentException("Title exceeds maximum length of 25 Characters [%s, Length %s]".formatted(dto.title(), dto.title().length()));
        }

        // Get the player entity which is to be updated
        log.debug("Fetching player");
        Player playerToUpdate = getPlayerByDiscordId(dto.discordId());

        // Check if player actually has an rpchar
        if (playerToUpdate.getRpChar() == null) {
            log.warn("No Rpchar found at player [{}]", playerToUpdate);
            throw new IllegalArgumentException("You do not have an RPChar and therefore cannot update its title!");
        }

        log.debug("Update RpChar Title");
        playerToUpdate.getRpChar().setTitle(dto.title());

        log.debug("Trying to persist player [{}]", playerToUpdate);
        playerToUpdate = secureSave(playerToUpdate, playerRepository);

        log.info("Successfully updated Rp Character title of player [{}] to [{}]!", playerToUpdate, playerToUpdate.getRpChar().getTitle());
        return playerToUpdate.getRpChar();
    }

    @Transactional(readOnly = false)
    public RPChar updateCharacterGear(UpdateRpCharDto dto) {
        log.debug("Updating character gear to '{}' for player's ({}) rpchar '{}'", dto.gear(), dto.discordId(), dto.charName());

        // Checking if data is valid
        log.debug("Validating DTO data");
        ServiceUtils.checkNulls(dto, List.of("executorDiscordId", "gear"));
        ServiceUtils.checkBlanks(dto, List.of("executorDiscordId", "gear"));

        // Get the player entity which is to be updated
        log.debug("Fetching player");
        Player playerToUpdate = getPlayerByDiscordId(dto.discordId());

        // Check if player actually has an rpchar
        if (playerToUpdate.getRpChar() == null) {
            log.warn("No Rpchar found at player [{}]", playerToUpdate);
            throw new IllegalArgumentException("You do not have an RPChar and therefore cannot update its gear!");
        }

        //Update the gear
        log.debug("Update RpChar Gear");
        playerToUpdate.getRpChar().setGear(dto.gear());

        log.debug("Trying to persist player [{}]", playerToUpdate);
        playerToUpdate = secureSave(playerToUpdate, playerRepository);

        log.info("Successfully updated Rp Character gear of player [{}] to [{}]!", playerToUpdate, playerToUpdate.getRpChar().getGear());
        return playerToUpdate.getRpChar();
    }


    @Transactional(readOnly = false)
    public RPChar updateCharacterPvp(UpdateRpCharDto dto) {
        log.debug("Updating character's pvp status to '{}' for player's ({}) rpchar '{}'", dto.pvp(), dto.discordId(), dto.charName());

        // Checking if data is valid
        log.debug("Validating DTO data");
        ServiceUtils.checkNulls(dto, List.of("executorDiscordId", "pvp"));
        ServiceUtils.checkBlanks(dto, List.of("executorDiscordId", "pvp"));

        // Get the player entity which is to be updated
        log.debug("Fetching player");
        Player playerToUpdate = getPlayerByDiscordId(dto.discordId());

        // Check if player actually has an rpchar
        if (playerToUpdate.getRpChar() == null) {
            log.warn("No Rpchar found at player [{}]", playerToUpdate);
            throw new IllegalArgumentException("You do not have an RPChar and therefore cannot update its pvp status!");
        }

        //Update the pvp status
        log.debug("Update RpChar PvP Status");
        playerToUpdate.getRpChar().setPvp(dto.pvp());

        log.debug("Trying to persist player [{}]", playerToUpdate);
        playerToUpdate = secureSave(playerToUpdate, playerRepository);

        log.info("Successfully updated Rp Character PvP Status of player [{}] to [{}]!", playerToUpdate, playerToUpdate.getRpChar().getPvp());
        return playerToUpdate.getRpChar();
    }

    @Transactional(readOnly = false)
    public Player deletePlayer(DiscordIdDto dto) {
        log.debug("Deleting player with data [{}]", dto);

        // Validating Data

        ServiceUtils.checkNulls(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toList()));

        ServiceUtils.checkBlanks(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toList()));

        // Get player who issued the command
        Player player = getPlayerByDiscordId(dto.discordId());
        log.debug("Found player which is to be deleted [{}]", player);

        log.debug("Trying to delete player [{}]", player);
        secureDelete(player, playerRepository);

        log.info("Succesfully deleted player [{}]", player);
        return player;
    }

    @Transactional(readOnly = false)
    public RPChar deleteRpChar(DiscordIdDto dto) {
        log.debug("Deleting Rpchar with data [{}]", dto);

        // Validating Data

        ServiceUtils.checkNulls(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toList()));

        ServiceUtils.checkBlanks(dto, Arrays.stream(dto.getClass().getDeclaredFields())
                .map(field -> field.getName())
                .collect(Collectors.toList()));

        // Get player who issued the command
        Player player = getPlayerByDiscordId(dto.discordId());
        log.debug("Found player which issued the command [{}]", player);

        if(player.getRpChar() == null) {
            log.warn("The player has no RpChar to delete!");
            throw new IllegalArgumentException("No roleplay character found!");
        }
        log.debug("Deleting RpChar [{}] from player [{}]", player.getRpChar(), player);
        RPChar deletedRpChar = player.getRpChar();
        player.setRpChar(null);
        log.debug("Trying to save player with deleted RpChar [{}]", player);
        player = secureSave(player, playerRepository);

        log.info("Succesfully deleted rpchar [{}]", deletedRpChar);
        return deletedRpChar;
    }


}
