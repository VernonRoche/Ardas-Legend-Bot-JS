package com.ardaslegends.data.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

@Entity
@Table(name = "movements")
public final class Movement extends AbstractDomainEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "army_name")
    private Army army; //Is null when it's a Rp Char movement

    private Boolean isCharMovement; //Should be true when army = null

    @Embedded
    private Path path;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Boolean isAccepted;
    private Boolean isCurrentlyActive;

}
