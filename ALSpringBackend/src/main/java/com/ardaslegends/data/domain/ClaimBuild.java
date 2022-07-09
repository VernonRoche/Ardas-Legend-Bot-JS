package com.ardaslegends.data.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "claimbuilds")
public final class ClaimBuild extends AbstractDomainEntity {
    @Id
    private String name; //unique, name of the claimbuild

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "region", foreignKey = @ForeignKey(name = "fk_region"))
    @NotNull(message = "Claimbuild: Region must not be null")
    private Region region; //the region the claimbuild is in

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Claimbuild: ClaimbuildType must not be null")
    private ClaimBuildType type; //Type of claimbuild, e.g. HAMLET

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "owned_by", foreignKey = @ForeignKey(name = "fk_owned_by"))
    @NotNull(message = "Claimbuild: ownedBy must not be null")
    private Faction ownedBy; //faction which owns this CB

    @Embedded
    @NotNull(message = "Claimbuild: Coordinate must not be null")
    private Coordinate coordinates; //coordinate locations

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "stationedAt")
    private List<Army> stationedArmies; //armies which are stationed in this CB

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "originalClaimbuild")
    private List<Army> createdArmies; //armies which were created from this CB. Usually only 1 army, but capitals can create 2

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "originalClaimbuild")
    private List<Army> createdTradingCompanies; //TCs which were created from this CB. Seperated from armies so you can search for them more easily.

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, mappedBy = "claimbuild")
    private List<ProductionClaimbuild> productionSites; //the production sites in this cb

    @ElementCollection(targetClass = SpecialBuilding.class)
    @CollectionTable(name = "claimbuild_special_buildings",
            joinColumns = @JoinColumn(name = "claimbuild_id", foreignKey = @ForeignKey(name = "fk_claimbuild_id")))
    @Column(name = "special_buildings")
    @Enumerated(EnumType.STRING)
    private List<SpecialBuilding> specialBuildings; //special buildings in this cb, e.g. House of Healing

    private String traders; //traders in this CB. e.g. Dwarven Smith. Only relevant for staff so they know which traders need to be added

    private String numberOfHouses; //houses in this CB, e.g. 4 large 12 small. Only relevant for staff

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "claimbuild_builders",
            joinColumns = { @JoinColumn(name = "claimbuild_id", foreignKey = @ForeignKey(name = "fk_claimbuild_id"))},
            inverseJoinColumns = { @JoinColumn(name = "player_id", foreignKey = @ForeignKey(name = "fk_player_id")) })
    private Set<Player> builtBy; //the player who built the CB


}
