package com.davidnyn.superdupermarkt.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product extends BaseEntity {

    @Basic(optional = false)
    @Column(name = "label", nullable = false)
    private String label;

    @Basic(optional = false)
    @Column(name = "quality", nullable = false)
    private Integer quality;

    @Basic(optional = false)
    @Column(name = "initial_price", precision = 19, scale = 2, nullable = false)
    private BigDecimal initialPrice;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @JoinColumn(name = "FoodType", nullable = false)
    @ManyToOne(optional = false, cascade = {CascadeType.MERGE})
    private FoodType foodType;

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("(Produkt: " + label + ", ");

        if (foodType != null && foodType.getName() != null)
            sb.append("Lebensmittelart: " + foodType.getName() + ", ");

        sb.append("Qualitaet: " + quality + ", ");

        if (expirationDate != null) {
            sb.append("Verfallsdatum: " + expirationDate.toString() + ", ");
        }
        else
            sb.append("Verfallsdatum: kein Verfallsdatum, ");

        sb.append("Grundpreis in Euro: " + initialPrice + ")");

        return sb.toString();
    }
}
