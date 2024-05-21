package com.davidnyn.superdupermarkt.data.mapper;

import com.davidnyn.superdupermarkt.data.dto.ProductCreationDto;
import com.davidnyn.superdupermarkt.data.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {


    public Product toEntity(ProductCreationDto dto)
    {
        Product product = new Product();

        product.setLabel(dto.getLabel());
        product.setQuality(dto.getQuality());
        product.setExpirationDate(dto.getExpirationDate());

        return product;

    }
}
