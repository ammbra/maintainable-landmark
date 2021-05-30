package org.acme.experiment.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@NoArgsConstructor
@Getter@Setter
@Entity
public class Location extends PanacheEntityBase {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String name;
    private String address;
    private String city;
    private String country;
    private Integer rating;
    private Double price;
    private Integer discount;
    @Transient
    private String message;
}
