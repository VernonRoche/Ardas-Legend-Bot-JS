package com.ardaslegends.data.domain;

public enum RegionType {

    // The number next to the Enum represensts the cost/duration to move into such a region

    SEA(1),
    LAND(1),
    HILL(2),
    ICE(2),
    DESERT(3),
    FOREST(3),
    SWAMP(4),
    JUNGLE(5),
    MOUNTAIN(6);

    private final int cost;
    private RegionType(int cost){
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }
}
