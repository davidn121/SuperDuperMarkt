package com.davidnyn.superdupermarkt;

import com.davidnyn.superdupermarkt.data.model.FoodType;
import com.davidnyn.superdupermarkt.data.model.Product;
import com.davidnyn.superdupermarkt.service.web.FoodTypeService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FoodTypeUnitTest {
    private static FoodType foodTypeKaese;
    private static FoodType foodTypeWein;

    @InjectMocks
    private FoodTypeService foodTypeService;

    @BeforeAll
    public static void beforeAll()
    {
        FoodType cheese = new FoodType();
        cheese.setId(1L);
        cheese.setName("Kaese");
        cheese.setMinimumQuality(30);
        cheese.setMinimumShelfLifeInDays(50);
        cheese.setMaximumShelfLifeInDays(100);
        cheese.setDailyQualityChange(BigDecimal.valueOf(-1));
        cheese.setHasUpdatedDailyPrice(true);

        foodTypeKaese = cheese;

        FoodType wine = new FoodType();
        wine.setId(2L);
        wine.setName("Wein");
        wine.setMinimumQuality(0);
        wine.setMaximumQuality(50);
        wine.setDailyQualityChange(BigDecimal.valueOf(0.1));
        wine.setHasUpdatedDailyPrice(false);

        foodTypeWein = wine;
    }

    @DisplayName("Quality change calculation test")
    @Test
    void testQualityChangeCalculation()
    {
        // Fulfills all criteria of cheese, has to de disposed on day 2 due to low quality
        Product feta = new Product();
        feta.setLabel("Feta");
        feta.setQuality(31);
        feta.setInitialPrice(BigDecimal.valueOf(2.5));
        feta.setFoodType(foodTypeKaese);
        feta.setExpirationDate(LocalDate.now().plusDays(80));

        assertAll("Check quality change for Feta",
                () -> assertEquals(31, feta.getQuality()),
                () -> assertEquals(29, foodTypeService.calculateQualityForSpecificDay(feta, 2)),
                () -> assertEquals(21, foodTypeService.calculateQualityForSpecificDay(feta, 10))
        );


        // Fulfills all criteria of Wine, gains one quality point and reaches maximum quality on day 10
        Product merlot = new Product();
        merlot.setLabel("Merlot");
        merlot.setQuality(49);
        merlot.setInitialPrice(BigDecimal.valueOf(5.00));
        merlot.setFoodType(foodTypeWein);

        assertAll("Check quality change for Merlot",
                () -> assertEquals(49, merlot.getQuality()),
                () -> assertEquals(49, foodTypeService.calculateQualityForSpecificDay(merlot, 6)),
                () -> assertEquals(50, foodTypeService.calculateQualityForSpecificDay(merlot, 10)),
                () -> assertEquals(50, foodTypeService.calculateQualityForSpecificDay(merlot, 22), () -> "Wine can't have a quality over 50")
        );
    }

    @DisplayName("Price calculation test")
    @Test
    void testPriceCalculation()
    {
        // Fulfills all criteria of cheese, has to be disposed on day 6 due to low quality
        Product gouda = new Product();
        gouda.setLabel("Gouda");
        gouda.setQuality(35);
        gouda.setInitialPrice(BigDecimal.valueOf(4.00));
        gouda.setFoodType(foodTypeKaese);
        gouda.setExpirationDate(LocalDate.now().plusDays(55));

        assertAll("Check price calculation for Gouda",
                () -> assertEquals(BigDecimal.valueOf(4.00).setScale(2, RoundingMode.HALF_UP), foodTypeService.calculatePrice(0, gouda)),
                () -> assertEquals(BigDecimal.valueOf(4.10).setScale(2, RoundingMode.HALF_UP), foodTypeService.calculatePrice(1, gouda)),
                () -> assertEquals(BigDecimal.valueOf(7.00).setScale(2, RoundingMode.HALF_UP), foodTypeService.calculatePrice(30, gouda)),
                () -> assertEquals(BigDecimal.valueOf(5.20).setScale(2, RoundingMode.HALF_UP), foodTypeService.calculatePrice(12, gouda))
        );

        // Fulfills all criteria of Wine
        Product riesling = new Product();
        riesling.setLabel("Riesling");
        riesling.setQuality(22);
        riesling.setInitialPrice(BigDecimal.valueOf(6.00));
        riesling.setFoodType(foodTypeWein);

        // Price depends on initial quality when it was stored, because wine doesn't have updated daily prices
        assertAll("Check price calculation for Riesling",
                () -> assertEquals(BigDecimal.valueOf(8.20).setScale(2, RoundingMode.HALF_UP), foodTypeService.calculatePrice(0, riesling)),
                () -> assertEquals(BigDecimal.valueOf(8.20).setScale(2, RoundingMode.HALF_UP), foodTypeService.calculatePrice(1, riesling)),
                () -> assertEquals(BigDecimal.valueOf(8.20).setScale(2, RoundingMode.HALF_UP), foodTypeService.calculatePrice(33, riesling))

        );
    }

    @DisplayName("To be disposed calculation test")
    @Test
    void testToBeDisposedCalculation()
    {
        // Fulfills all criteria of cheese, has to de disposed on day 2 due to low quality
        Product feta = new Product();
        feta.setLabel("Feta");
        feta.setQuality(31);
        feta.setInitialPrice(BigDecimal.valueOf(2.5));
        feta.setFoodType(foodTypeKaese);
        feta.setExpirationDate(LocalDate.now().plusDays(80));

        assertAll("Check to be disposed calculation for Feta",
                // quality too low
                () -> assertTrue(foodTypeService.checkIfProductIsToBeDisposedForSpecificDay(29, feta, 1)),
                // in 82 days is after expiration date
                () -> assertTrue(foodTypeService.checkIfProductIsToBeDisposedForSpecificDay(32, feta, 82)),
                () -> assertFalse(foodTypeService.checkIfProductIsToBeDisposedForSpecificDay(35, feta, 7))
            );

        // quality too high
        Product malbec = new Product();
        malbec.setLabel("Malbec");
        malbec.setQuality(55);
        malbec.setInitialPrice(BigDecimal.valueOf(5.00));
        malbec.setFoodType(foodTypeWein);

        assertAll("Check to be disposed calculation for Feta",
                // quality too low
                () -> assertFalse(foodTypeService.checkIfProductIsToBeDisposedForSpecificDay(29, malbec, 1)),
                // in 82 days is after expiration date
                () -> assertFalse(foodTypeService.checkIfProductIsToBeDisposedForSpecificDay(1, malbec, 82)),
                () -> assertTrue(foodTypeService.checkIfProductIsToBeDisposedForSpecificDay(-1, malbec, 7))
        );
    }
}
