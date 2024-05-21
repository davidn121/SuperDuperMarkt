package com.davidnyn.superdupermarkt.data.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "food_types")
public class FoodType extends BaseEntity {

    @Basic(optional = false)
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "minimum_quality")
    private Integer minimumQuality;

    @Column(name = "maximum_quality")
    private Integer maximumQuality;

    @Column(name = "minimum_shelf_life_in_days")
    private Integer minimumShelfLifeInDays;

    @Column(name = "maximum_shelf_life_in_days")
    private Integer maximumShelfLifeInDays;

    // rate in which the quality of the food type increases/decreases
    @Basic(optional = false)
    @Column(name = "daily_quality_change", nullable = false)
    private BigDecimal dailyQualityChange;

    @Basic(optional = false)
    @Column(name = "has_update_daily_price", nullable = false)
    private Boolean hasUpdatedDailyPrice = true;

    @OneToMany(mappedBy = "foodType")
    private Set<Product> products = new HashSet<>();
}
