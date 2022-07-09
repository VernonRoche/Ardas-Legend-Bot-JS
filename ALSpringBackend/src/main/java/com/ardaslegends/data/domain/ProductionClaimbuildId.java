package com.ardaslegends.data.domain;


import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;


/***
 * This is the Id Mapping for the Production - Claimbuild Association Table
 * Without this, JPA doesn't map the primary key correctly
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Embeddable
public final class ProductionClaimbuildId extends AbstractDomainEntity implements Serializable {

    private static final long serialVersionUID = -7659401942823299559L;

    private Long productionSiteId;

    private String claimbuildId;



}
