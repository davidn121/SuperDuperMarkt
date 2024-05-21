package com.davidnyn.superdupermarkt.data.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "internalBuilder")
public class ProductCreationDto
{
    private String label;
    private Integer quality;
    private String foodType;
    private BigDecimal initialPrice;
    private LocalDate expirationDate;

    public static ProductCreationDtoBuilder builder(String label, Integer quality, BigDecimal initialPrice, String foodType)
    {
        return internalBuilder().label(label).quality(quality).initialPrice(initialPrice).foodType(foodType);
    }
}
