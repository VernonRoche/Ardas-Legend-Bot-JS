package com.ardaslegends.service.utils;


import com.ardaslegends.data.domain.Player;
import com.ardaslegends.data.service.utils.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class ServiceUtilsTest {

    // Check nulls

    @Test
    void ensureCheckNullsWorks() {
        log.debug("Testing if checkNull works properly!");

        //Assign
        log.trace("Initializing Player object");
        Player player = Player.builder().ign("Luk").discordID("1234").uuid(null).build();

        //Act / Assert
        log.debug("Asserting that checkNulls() throws NullPointerException");
        var exception = assertThrows(NullPointerException.class,
                () -> ServiceUtils.checkNulls(player, List.of("ign", "discordID", "uuid")));

        log.debug("Assert that uuid was null");
        assertThat(exception.getMessage()).isEqualTo("uuid must not be null!");
    }


    @Test
    void ensureCheckAllNullsWorks() {
        log.debug("Testing if checkAllNulls works properly!");

        //Assign
        log.trace("Initializing Player object");
        Player player = Player.builder().id(2L).ign("Luk").discordID("1234").uuid(null).build();

        //Act / Assert
        log.debug("Asserting that checkAllNulls() throws NullPointerException");
        var exception = assertThrows(NullPointerException.class,
                () -> ServiceUtils.checkAllNulls(player));

        log.debug("Assert that uuid was null");
        assertThat(exception.getMessage()).isEqualTo("uuid must not be null!");
    }

    @Test
    void ensureCheckBlanksWorks() {
        log.debug("Testing if checkBlanks works properly!");

        //Assign
        log.trace("Initializing Player object");
        Player player = Player.builder().id(2L).ign("Luk").discordID("").uuid(null).build();

        //Act / Assert
        log.debug("Asserting that checkBlanks() throws IllegalArgumentException");
        var exception = assertThrows(IllegalArgumentException.class,
                () -> ServiceUtils.checkBlanks(player, List.of("ign", "discordID")));

        log.debug("Assert that discordID was blank");
        assertThat(exception.getMessage()).isEqualTo("discordID must not be blank!");
    }

    @Test
    void ensureCheckAllBlanksWorks() {
        log.debug("Testing if checkAllBlanks works properly!");

        //Assign
        log.trace("Initializing Player object");
        Player player = Player.builder().id(2L).ign("Luk").discordID("").uuid("hello").build();

        //Act / Assert
        log.debug("Asserting that checkAllBlanks() throws IllegalArgumentException");
        var exception = assertThrows(IllegalArgumentException.class,
                () -> ServiceUtils.checkAllBlanks(player));

        log.debug("Assert that discordID was blank");
        assertThat(exception.getMessage()).isEqualTo("discordID must not be blank!");
    }
}
