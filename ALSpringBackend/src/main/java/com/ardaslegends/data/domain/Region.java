package com.ardaslegends.data.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "regions")
public final class Region extends AbstractDomainEntity {

    @Id
    private String id; //unique, the region id

    private String name; //the name of the region (prob also unique)

    @Enumerated(EnumType.STRING)
    private RegionType regionType; // type of the region

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "faction_claimed_regions",
            joinColumns = { @JoinColumn(name = "region", foreignKey = @ForeignKey(name = "fk_region"))},
            inverseJoinColumns = { @JoinColumn(name = "faction", foreignKey = @ForeignKey(name = "fk_faction")) })
    private Set<Faction> claimedBy; //the list of factions which the region is claimed by

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "ownedBy")
    private List<ClaimBuild> claimBuilds; //list of claimbuilds in this region

    @JsonIgnore
    @Setter(value = AccessLevel.PRIVATE)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "region_neighbours",
            joinColumns = { @JoinColumn(name = "region", foreignKey = @ForeignKey(name = "fk_region"))},
            inverseJoinColumns = { @JoinColumn(name = "neighbour", foreignKey = @ForeignKey(name = "fk_neighbour")) })
    private Set<Region> neighboringRegions = new HashSet<>(); //list of neighboring regions

    /**
     *
     * @param possibleNeighbour, the region that you want to add, Not-Null-Constraint
     * @throws NullPointerException, when parameter is null
     * @return false when region is already in Set, true when it succeeds
     */
    public boolean addNeighbour(@NonNull Region possibleNeighbour) {

        if(neighboringRegions.contains(possibleNeighbour))
            return false;

        neighboringRegions.add(possibleNeighbour);
        return true;

    }

    public Set<Region> getNeighboringRegions() {
        return Collections.unmodifiableSet(neighboringRegions);
    }

    public int getCost() {
        return regionType.getCost();
    }


    @Override
    public String toString() {
        return "Region{" +
                "id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Region region = (Region) o;
        return id.equals(region.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
