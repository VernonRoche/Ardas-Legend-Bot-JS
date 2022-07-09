package com.ardaslegends.data.domain;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Embeddable
public final class RPChar extends AbstractDomainEntity {

    @Column(unique = true)
    private String name;

    @Length(max = 25, message = "Title too long!")
    private String title;

    private String gear;

    private Boolean pvp;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "current_region", foreignKey = @ForeignKey(name = "fk_current_region"))
    @NotNull(message = "RpChar: currentRegion must not be null")
    private Region currentRegion; //the region the character is currently in

    @OneToOne
    @JoinColumn(name = "bound_to", foreignKey = @ForeignKey(name = "fk_bound_to"))
    private Army boundTo; //the army that is bound to this character

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RPChar rpChar = (RPChar) o;
        return name.equals(rpChar.name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
