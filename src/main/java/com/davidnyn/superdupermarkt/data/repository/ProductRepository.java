package com.davidnyn.superdupermarkt.data.repository;

import com.davidnyn.superdupermarkt.data.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
