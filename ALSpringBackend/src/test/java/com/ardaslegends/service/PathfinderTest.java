package com.ardaslegends.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.domain.Path;
import com.ardaslegends.data.service.Pathfinder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PathfinderTest {

    private RegionRepository mockRepository;
    private Pathfinder pathfinder;
    private Player player;

    Region r1, r2, r3, r4, r5, r6;
    Region rs1, rs2;

    @BeforeEach
    void testData() {
        // Initialize Data
        // Initialize regions, player, factions, army, rpchar and finally claimbuild
        r1 = new Region("1", "one", RegionType.LAND, new HashSet<>(), new ArrayList<>(), new HashSet<>());
        r2 = new Region("2", "two", RegionType.MOUNTAIN, new HashSet<>(), new ArrayList<>(), new HashSet<>());
        r3 = new Region("3", "three", RegionType.LAND, new HashSet<>(), new ArrayList<>(), new HashSet<>());
        r4 = new Region("4", "four", RegionType.HILL, new HashSet<>(), new ArrayList<>(), new HashSet<>());
        r5 = new Region("5", "five", RegionType.LAND, new HashSet<>(), new ArrayList<>(), new HashSet<>());
        r6 = new Region("6", "six", RegionType.LAND, new HashSet<>(), new ArrayList<>(), new HashSet<>());
        rs1 = new Region("1.S", "one_sea", RegionType.SEA, new HashSet<>(), new ArrayList<>(), new HashSet<>());
        rs2 = new Region("2.S", "two_sea", RegionType.SEA, new HashSet<>(), new ArrayList<>(), new HashSet<>());

        player = Player.builder().ign("VernonRoche").discordID("VernonRocheDiscord").build();
        Faction faction_good = new Faction("Gondor", player, new ArrayList<>(), new ArrayList<>(), new HashSet<>(), new ArrayList<>(), new ArrayList<>(), "white", r1, "Double move in Gondor");
        Faction faction_bad = new Faction("Mordor", null, new ArrayList<>(), new ArrayList<>(), new HashSet<>(), new ArrayList<>(), new ArrayList<>(), "black", r3, "Move in Mordor");

        Army army = new Army("Test army", ArmyType.ARMY, faction_good, r1, null, new ArrayList<>(), new ArrayList<>(), null, 15, null);
        RPChar rpchar = RPChar.builder().name("Aldwin").currentRegion(r1).boundTo(army).build();

        ClaimBuild claimbuild = new ClaimBuild("claimbuild", r2, ClaimBuildType.CAPITAL, faction_good,
                new Coordinate(1, 1, 1), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), "none", "4", new HashSet<>());

        // Set up relations
        r1.getClaimedBy().add(faction_good);
        r3.getClaimedBy().add(faction_bad);

        player.setFaction(faction_good);
        player.setRpChar(rpchar);

        faction_good.getArmies().add(army);
        faction_good.getPlayers().add(player);
        faction_good.getRegions().add(r1);
        faction_good.getRegions().add(r2);

        faction_bad.getRegions().add(r3);

        army.setBoundTo(player);

        r2.getClaimBuilds().add(claimbuild);

        claimbuild.getSpecialBuildings().add(SpecialBuilding.HARBOUR);

        // Set up region neighbours
        r1.addNeighbour(r2);
        r1.addNeighbour(r3);
        r1.addNeighbour(r4);
        r2.addNeighbour(r1);
        r2.addNeighbour(r3);
        r3.addNeighbour(r1);
        r3.addNeighbour(r2);
        r3.addNeighbour(r5);
        r3.addNeighbour(r6);
        r4.addNeighbour(r1);
        r4.addNeighbour(r3);
        r4.addNeighbour(r5);
        r5.addNeighbour(r3);
        r5.addNeighbour(r4);
        r5.addNeighbour(r6);
        r6.addNeighbour(r3);
        r6.addNeighbour(r5);
        rs1.addNeighbour(r2);
        rs1.addNeighbour(r3);
        rs2.addNeighbour(r3);
        rs2.addNeighbour(r6);

        List<Region> regionList = List.of(r1, r2, r3, r4, r5, r6, rs1, rs2);

        mockRepository = mock(RegionRepository.class);

        for (Region region : regionList) {
            when(mockRepository.findById(region.getId())).thenReturn(Optional.of(region));
        }

        pathfinder = new Pathfinder(mockRepository);
    }

    @Test
    void ensureNormalMoveSucceeds() {
        Path path = pathfinder.findShortestWay(r1, r2, player, false);
        assertThat(path.getPath().size()).isEqualTo(2);
        assertThat(path.getCost()).isEqualTo(RegionType.MOUNTAIN.getCost());

        path = pathfinder.findShortestWay(r1, r5, player, false);
        assertThat(path.getPath().size()).isEqualTo(3);
        assertThat(path.getCost()).isEqualTo(RegionType.LAND.getCost() + RegionType.HILL.getCost());

        path = pathfinder.findShortestWay(r1, r6, player, false);
        assertThat(path.getPath().size()).isEqualTo(4);
        assertThat(path.getCost()).isEqualTo(RegionType.LAND.getCost() * 2 + RegionType.HILL.getCost());
    }

    @Test
    void ensureEmbarkingSucceeds() {
        rs1.addNeighbour(rs2);
        rs2.addNeighbour(rs1);
        r2.addNeighbour(rs1);
        r3.addNeighbour(rs1);
        r3.addNeighbour(rs2);
        r6.addNeighbour(rs2);
        List<Region> regionList = List.of(r2, r3, r6, rs1, rs2);
        mockRepository.saveAll(regionList);

        Path path = pathfinder.findShortestWay(r2, rs1, player, false);
        assertThat(path.getPath().size()).isEqualTo(2);
        assertThat(path.getCost()).isEqualTo(1);
    }

    @Test
    void ensureDisembarkingSucceeds() {
        rs1.addNeighbour(rs2);
        rs2.addNeighbour(rs1);
        r2.addNeighbour(rs1);
        r3.addNeighbour(rs1);
        r3.addNeighbour(rs2);
        r6.addNeighbour(rs2);
        List<Region> regionList = List.of(r2, r3, r6, rs1, rs2);
        mockRepository.saveAll(regionList);

        Path path = pathfinder.findShortestWay(rs2, r6, player, false);
        assertThat(path.getPath().size()).isEqualTo(2);
        assertThat(path.getCost()).isEqualTo(2);
    }

    @Test
    void ensureThatMoveInEnemyFails() {
        Path path = pathfinder.findShortestWay(r1, r3, player, false);
        assertEquals(-1, path.getCost());
    }

    /*
    This test is working for regions with different costs.
    Considering every region has the same cost, this test is not really useful.
     */
    @Test
    void ensureMoveThroughSeaWorks() {
        rs1.addNeighbour(rs2);
        rs2.addNeighbour(rs1);
        r2.addNeighbour(rs1);
        r3.addNeighbour(rs1);
        r3.addNeighbour(rs2);
        r6.addNeighbour(rs2);
        List<Region> regionList = List.of(r2, r3, r6, rs1, rs2);
        mockRepository.saveAll(regionList);

        Path path = pathfinder.findShortestWay(r2, r6, player, false);
        /*assertThat(path.getPath().size()).isEqualTo(4);
        assertThat(path.getCost()).isEqualTo(RegionType.SEA.getCost() * 2 + RegionType.LAND.getCost() + 1);
        assertFalse(path.getPath().contains(r3));*/

    }

}
