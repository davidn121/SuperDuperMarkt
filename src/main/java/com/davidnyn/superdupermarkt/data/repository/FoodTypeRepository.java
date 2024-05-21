package com.davidnyn.superdupermarkt.data.repository;

import com.davidnyn.superdupermarkt.data.model.FoodType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodTypeRepository extends JpaRepository<FoodType, Long> {
}
