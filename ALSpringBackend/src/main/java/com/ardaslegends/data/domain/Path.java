package com.ardaslegends.data.domain;

import lombok.*;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@Builder

@Embeddable
public class Path {

    private Integer cost;

    @ElementCollection
    private List<String> path;

    public Path() {
       this.cost = 0;
       this.path = new ArrayList<>(1);
    }

    public String getDestination() {
        return path.get(path.size()-1);
    }
}
