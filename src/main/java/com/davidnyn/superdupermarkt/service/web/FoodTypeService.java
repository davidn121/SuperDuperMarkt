package com.davidnyn.superdupermarkt.service.web;

import com.davidnyn.superdupermarkt.data.model.FoodType;
import com.davidnyn.superdupermarkt.data.model.Product;
import com.davidnyn.superdupermarkt.data.repository.FoodTypeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
public class FoodTypeService extends EntityCrudService<FoodType, FoodTypeRepository>
{

    public int calculateQualityForSpecificDay(Product product, int day)
    {
        FoodType foodType = product.getFoodType();

        int calculatedQuality = BigDecimal.valueOf(product.getQuality())
                .add(foodType.getDailyQualityChange().multiply(BigDecimal.valueOf(day)))
                .setScale(0, RoundingMode.DOWN).intValue();

        return foodType.getMaximumQuality() != null ? Math.min(calculatedQuality, foodType.getMaximumQuality()) : calculatedQuality;
    }

    public BigDecimal calculatePrice(int calculatedQuality, Product product)
    {
        FoodType foodType = product.getFoodType();

        BigDecimal relevantQuality = !foodType.getHasUpdatedDailyPrice() ? BigDecimal.valueOf(product.getQuality()) : BigDecimal.valueOf(calculatedQuality);

        return product.getInitialPrice().add(BigDecimal.valueOf(0.10).multiply(relevantQuality)).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean checkIfProductIsToBeDisposedForSpecificDay(int calculatedQuality, Product product, int day) {
        FoodType foodType = product.getFoodType();
        LocalDate relevantDay = LocalDate.now().plusDays(day);

        // If there is no expiration date it cant be expired
        boolean isDateAfterExpired = product.getExpirationDate() != null && (relevantDay.isEqual(product.getExpirationDate()) || relevantDay.isAfter(product.getExpirationDate()));

        return isDateAfterExpired || calculatedQuality < foodType.getMinimumQuality();
    }

}
