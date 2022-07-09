package com.ardaslegends.repository;

import com.ardaslegends.data.domain.Faction;
import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.domain.RPChar;
import com.ardaslegends.data.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PlayerRepositoryTest {

    @Autowired
    PlayerRepository repository;

    @BeforeEach
    void setup() {
        Player p1 = Player.builder().ign("mirak").discordID("MiraksId").faction(Faction.builder().name("Gondor").build()).uuid("MiraksUUID").build();
        Player p2 = Player.builder().ign("vernon").discordID("vernonId").faction(Faction.builder().name("Mordor").build()).uuid("vernonUUID").build();
        Player p3 = Player.builder().ign("luk").discordID("luksId").faction(Faction.builder().name("Arnor").build()).uuid("luksUUID").build();
        Player p4 = Player.builder().ign("aned").discordID("anedsId").faction(Faction.builder().name("Rivendell").build()).uuid("anedsUUID").build();
        RPChar rpChar = new RPChar("Sauron", "s","s", true,null, null);
        Player p5 = Player.builder().ign("anotherOne").discordID("anotherOnesId").faction(Faction.builder().name("SecretNewFac").build()).uuid("anotherOneUUID").rpChar(rpChar).build();;


        repository.saveAll(List.of(p1,p2,p3,p4,p5));
    }

    @Test
    void ensureFindByDiscordIdWorks() {

        // Assign
        String discordId = "MiraksId";

        // Act
        var query = repository.findByDiscordID(discordId);

        // Assert
        assertThat(query.isPresent()).isTrue();
        assertThat(query.get().getDiscordID()).isEqualTo(discordId);


    }

    @Test
    void ensureFindByIgnWorks() {
        // Assign
        String ign = "mirak";

        // Act
        var result = repository.findPlayerByIgn(ign);

        // Assert
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getDiscordID()).isEqualTo("MiraksId");
    }

    @Test
    void ensureFindByRPCharWorks() {
        // Assign

        RPChar rpChar2 = new RPChar("Sauron", "e", "e",true, null,null);

        // Act
        var result = repository.findPlayerByRpChar(rpChar2.getName());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getRpChar().getGear()).isNotEqualTo(rpChar2.getGear());

    }

    @Test
    void ensureFindByRPCharReturnsEmptyOptionalWhenNothingFound() {
        // Assign

        RPChar rpChar2 = new RPChar("SomebodyThatIUsedToKnoooow", "e", "e", true, null,null);

        // Act
        var result = repository.findPlayerByRpChar(rpChar2.getName());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isTrue();

    }
}
