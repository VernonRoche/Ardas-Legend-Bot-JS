package com.ardaslegends.data.domain;

public enum ClaimBuildType {
    HAMLET(0,0,0,0),
    VILLAGE(0,0,0,0),
    TOWN(1,1,1,1),
    CAPITAL(2,1,2,1),
    KEEP(0,0,0,0),
    CASTLE(1,0,1,0),
    STRONGHOLD(1,0,1,0);

    private final int maxArmies;
    private final int freeArmies;
    private final int maxTradingCompanies;
    private final int freeTradingCompanies;
    private ClaimBuildType(int maxArmies, int maxTradingCompanies, int freeArmies, int freeTradingCompanies) {
        this.maxArmies = maxArmies;
        this.maxTradingCompanies = maxTradingCompanies;
        this.freeArmies = freeArmies;
        this.freeTradingCompanies = freeTradingCompanies;
    }

    public int getMaxArmies() {return maxArmies; }
    public int getMaxTradingCompanies() {return maxTradingCompanies; }
    public int getFreeArmies() {return freeArmies; }
    public int getFreeTradingCompanies() {return freeTradingCompanies; }
}
