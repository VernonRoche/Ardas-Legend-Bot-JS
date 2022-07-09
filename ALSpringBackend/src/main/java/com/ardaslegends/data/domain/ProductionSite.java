package com.ardaslegends.data.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "production_sites")
public final class ProductionSite extends AbstractDomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProductionSiteType type; //unique, type of production site, e.g. FARM

    @Column(name = "produced_resource")
    private String producedResource; //the resource this production site produces

    @Column(name = "amount_produced")
    private Integer amountProduced; //the amount

}
