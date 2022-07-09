package com.ardaslegends.data.domain;

import lombok.*;

import javax.persistence.*;


/***
 * Association Table for the Claimbuild 1:n - n:1 ProductionSite Association
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "production_claimbuild")
public final class ProductionClaimbuild extends AbstractDomainEntity {

    @EmbeddedId
    private ProductionClaimbuildId id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @MapsId("productionSiteId")
    @JoinColumn(name = "production_site_id", foreignKey = @ForeignKey(name = "fk_production_site_id"))
    private ProductionSite productionSite;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @MapsId("claimbuildId")
    @JoinColumn(name = "claimbuild_id", foreignKey = @ForeignKey(name = "fk_claimbuild_id"))
    private ClaimBuild claimbuild;

    private Long count;

}
