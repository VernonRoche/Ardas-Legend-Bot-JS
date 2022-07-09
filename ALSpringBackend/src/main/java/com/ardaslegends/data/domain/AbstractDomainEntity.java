package com.ardaslegends.data.domain;

/***
 * Every Domain Entity relevant to our database implements this interface
 * so that we can restrict certain codeblocks to using only DomainEntities.
 * Should only be used for @Entity Classes and NOT @Embeddables
 */
public abstract sealed class AbstractDomainEntity permits Army, ClaimBuild, Faction, Player, ProductionClaimbuild, ProductionClaimbuildId, ProductionSite, Region, Unit, UnitType, RPChar, Movement {
}
