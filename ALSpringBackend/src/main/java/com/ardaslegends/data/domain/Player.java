package com.ardaslegends.data.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "players")
public final class Player extends AbstractDomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull(message = "Player: IGN must not be null")
    private String ign; //unique, ingame name of the player

    @Column(unique = true)
    @NotNull(message = "Player: UUID must not be null")
    private String uuid;

    @Column(name = "discord_id", unique = true)
    @NotNull(message = "Player: DiscordID must not be null")
    private String discordID; //unique, the ID of the player's discord account

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "faction", foreignKey = @ForeignKey(name = "fk_faction"))
    @NotNull(message = "Player: Faction must not be null")
    private Faction faction; //the faction this character belongs to

    @Embedded
    private RPChar rpChar; //the player's rp character

    @Override
    public String toString() {
        return ign;
    }
}
