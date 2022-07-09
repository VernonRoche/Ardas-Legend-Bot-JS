package com.ardaslegends.data.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.exceptions.ServiceException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Builder

@Service
@Slf4j
public class Pathfinder {
  private final RegionRepository _regionRepository;

  /**
   * Find the shortest path
   * Return an object which contains the path and sum of weights
   */

  public Path findShortestWay(
          Region startRegion,
          Region endRegion,
          Player player,
          boolean isCharacterMove
  ){
    log.info("Finding Path for player '{}': from '{}' to '{}' isRpChar: {}",player.getIgn(), startRegion.getId(), endRegion.getId(), isCharacterMove);
    log.debug("Movement is army move: {}", (!isCharacterMove));

    log.debug("Initializing data for Pathfinding...");
    //smallest weights between startRegion and all the other nodes
    Map<Region, Integer> smallestWeights = new HashMap<>();
    //for convenience, mark distance from startRegion to itself as 0
    smallestWeights.put(startRegion, 0);

    //implicit graph of all nodes and previous node in ideal paths
    Map<Region, Region> prevNodes = new HashMap<>();

    //use queue for breadth first search
    //for convenience, we'll use an array, but linked list would be preferred
    ArrayList<Region> nodesToVisitQueue= new ArrayList<>();

    //record visited nodes with a set. The string is the toString of a HexID
    Set<String> visitedNodes = new HashSet<>();
    visitedNodes.add(startRegion.getId());

    Region currentNode = startRegion;
    //loop through nodes
    log.debug("Starting the loop...");
    while (currentNode != endRegion)  {
      //get the shortest path so far from start to currentNode
      final int dist = smallestWeights.get(currentNode);

      log.debug("Checking region {}", currentNode.getId());

      //iterate over current child's nodes and process
      Set<Region> neighbourRegions = currentNode.getNeighboringRegions();
      log.trace("Iterating over Region {}'s neighbours", currentNode.getId());
      for (Region neighbourRegion : neighbourRegions) {

        log.trace("Checking if neighbor has been visited yet...");
        //add node to queue if not already visited
        if (
                !visitedNodes.contains(neighbourRegion.getId()) &&
                        !nodesToVisitQueue.contains(neighbourRegion)
        ) {
          log.debug("Region {} hasn't been visited yet - adding to queue", neighbourRegion.getId());
          nodesToVisitQueue.add(neighbourRegion);
        }

        log.debug("Calculating the cost for Region {}", neighbourRegion.getId());

        // CALCULATE COST
        int thisDist = 0;

        // Check if we can move to that region as an army
        if (!isCharacterMove) {
          log.debug("Checking if army can move through Region {}", neighbourRegion.getId());

          boolean isNotClaimedByFaction = !neighbourRegion.getClaimedBy().contains(player.getFaction());
          log.trace("Region {} is claimed by Faction: {}", neighbourRegion.getId(), !isNotClaimedByFaction);

          boolean isNotClaimedByAlly = player.getFaction().getAllies().stream().noneMatch(faction ->
                  neighbourRegion.getClaimedBy().contains(faction));
          log.trace("Region {} is claimed by ally: {}", neighbourRegion.getId(), !isNotClaimedByAlly);

          boolean isNotUnclaimed = !neighbourRegion.getClaimedBy().isEmpty();
          log.trace("Region {} is unclaimed: {}", neighbourRegion.getId(), !isNotUnclaimed);

          if (isNotClaimedByAlly && isNotClaimedByFaction && isNotUnclaimed) {
            log.debug("Army cannot move through Region {} - it is not unclaimed or claimed by the player's faction or its allies!", neighbourRegion.getId());
            thisDist = 1000;
          }
        }

        log.debug("Checking if dis-/embarking...");

        // Check if we are embarking or disembarking
        if (currentNode.getRegionType() != RegionType.SEA && neighbourRegion.getRegionType() == RegionType.SEA) { //Checks if the current Region is land and has a Sea Region as neighbor
          log.trace("Current region is Land and neighbors a Sea region - continue check for harbour");

          boolean canEmbark = false;
          log.trace("Checking Region's claimbuilds for harbour");
          for (ClaimBuild claimbuild : currentNode.getClaimBuilds()) {
            if (claimbuild.getSpecialBuildings().contains(SpecialBuilding.HARBOUR)) {
              log.debug("Found Harbour in current Region ({}) - can embark", neighbourRegion.getId());
              thisDist += dist + 1;
              canEmbark = true;
              break;
            }
          }
          // It's a code smell I know. Had to find a quick fix.
          // Should be part of the next code review.
          if (!canEmbark) {
            log.debug("No Harbour found in current Region ({}) - cannot embark", neighbourRegion.getId());
            thisDist = 1000;
          }
        } else if (currentNode.getRegionType() == RegionType.SEA && neighbourRegion.getRegionType() != RegionType.SEA) { //Checks if current region is Sea and neighbor is land
          log.debug("Current region is Sea region and neighbors land region - can disembark");
          thisDist += dist + neighbourRegion.getCost() + 1;
        } else {
          log.debug("Current region is land region of type {}", currentNode.getRegionType().name());
          thisDist += dist + neighbourRegion.getCost();
        }

        log.debug("Calculated Cost for this Region -> {}", thisDist);

        log.debug("Checking if there is a shorter path already");
        //if we already have a distance to neighbourRegion, compare with this distance
        if (prevNodes.containsKey(neighbourRegion)) {
          log.trace("Found another path - checking which is shorter...");
          //get the recorded smallest distance
          final int altDist = smallestWeights.get(neighbourRegion);

          //if this distance is better, update the smallest distance + prev node
          if (thisDist < altDist) {
            log.debug("Current path is shorter, setting as new shortest Path (old: {} - new: {})", altDist, thisDist);
            prevNodes.put(neighbourRegion, currentNode);
            smallestWeights.put(neighbourRegion, thisDist);
          }
        } else {
          //if there is no distance recoded yet, add now
          log.debug("No path found - setting as new shortest Path (cost: {})",thisDist);
          prevNodes.put(neighbourRegion, currentNode);
          smallestWeights.put(neighbourRegion, thisDist);
        }
      }

      //mark that we've visited this node
      log.trace("Setting current node as visited");
      visitedNodes.add(currentNode.getId());

      //exit if done
      //pull the next node to visit, if any
      log.trace("Getting the next node to visit and removing current one from queue");
      nodesToVisitQueue.remove(currentNode);
      currentNode = nodesToVisitQueue.stream().min(Comparator.comparingInt(smallestWeights::get)).get();

      if (currentNode == null) {
        log.warn("No Region to visit!");
        throw ServiceException.pathfinderNoRegions(startRegion, endRegion);
      }
    }

    //get the shortest path into an array
    ArrayList<String> path = new ArrayList<>();

    log.trace("Building the Path");
    currentNode = endRegion;
    while (!Objects.equals(currentNode.getId(), startRegion.getId())) {
      path.add(currentNode.getId());
      currentNode = prevNodes.get(currentNode);
    }
    path.add(startRegion.getId());

    log.trace("Reversing the path so it starts with the start region");
    //reverse the path so it starts with startRegion
    Collections.reverse(path);
    log.trace("Getting the cost of the path");
    int cost = smallestWeights.get(endRegion);
    if (isCharacterMove) {
      log.trace("Halving the cost since the movement is a RpChar move");
      cost = (int) Math.ceil(cost / 2.0);
    }
    if (cost >= 1000) {
      cost = -1;
    }
    log.debug("Final cost is {}", cost);

    log.info("Finished finding shortest path from {} to {}", startRegion.getId(), endRegion.getId());
    log.info("Cost: {} - Path: {}", cost, String.join(" -> ", path));
    return new Path(cost, path);
  }
}