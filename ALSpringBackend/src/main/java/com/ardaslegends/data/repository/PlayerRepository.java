package com.ardaslegends.data.repository;

import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.domain.RPChar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String> {

    Optional<Player> findPlayerByIgn(String ign);

    Optional<Player> findByDiscordID(String discordId);

    @Query("FROM Player WHERE rpChar.name = ?1")
    Optional<Player> findPlayerByRpChar(String name);
}
