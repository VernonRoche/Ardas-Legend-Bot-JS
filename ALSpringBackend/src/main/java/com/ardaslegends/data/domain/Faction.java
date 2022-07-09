package com.ardaslegends.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "factions")
public final class Faction extends AbstractDomainEntity {

    @Id
    private String name; //unique, name of the faction

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Player leader; //the player who leads this faction

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "faction")
    private List<Army> armies; //all current armies of this faction
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "faction")
    @JsonIgnore
    private List<Player> players; //all current players of this faction
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "claimedBy")
    private Set<Region> regions; //all regions this faction claims
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "ownedBy")
    private List<ClaimBuild> claimBuilds; //all claimbuilds of this faction

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "faction_allies",
            joinColumns = { @JoinColumn(name = "faction", foreignKey = @ForeignKey(name = "fk_faction"))},
            inverseJoinColumns = { @JoinColumn(name = "ally_faction", foreignKey = @ForeignKey(name = "fk_ally_faction")) })
    private List<Faction> allies; //allies of this faction
    private String colorcode; //the faction's colorcode, used for painting the map

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Region homeRegion; //Homeregion of the faction

    @Length(max = 512)
    private String factionBuffDescr; //The description of this faction's buff

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faction faction = (Faction) o;
        return name.equals(faction.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
