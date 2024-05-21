package com.davidnyn.superdupermarkt;

import com.davidnyn.superdupermarkt.application.exceptions.EntityValidationException;
import com.davidnyn.superdupermarkt.data.model.FoodType;
import com.davidnyn.superdupermarkt.data.model.Product;
import com.davidnyn.superdupermarkt.service.web.ProductService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ProductValidationExceptionUnitTest {
    private static FoodType foodTypeKaese;
    private static FoodType foodTypeWein;

    @InjectMocks
    private ProductService productService;

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

    @DisplayName("Test of valid products")
    @Test
    void testValidProducts()
    {
        Product gouda = new Product();
        gouda.setLabel("Gouda");
        gouda.setQuality(35);
        gouda.setInitialPrice(BigDecimal.valueOf(4.00));
        gouda.setFoodType(foodTypeKaese);
        gouda.setExpirationDate(LocalDate.now().plusDays(55));

        assertDoesNotThrow(() -> productService.validateEntity(gouda));

        // Fulfills all criteria of Wine, gains one quality point and reaches maximum quality on day 10
        Product merlot = new Product();
        merlot.setLabel("Merlot");
        merlot.setQuality(49);
        merlot.setInitialPrice(BigDecimal.valueOf(5.00));
        merlot.setFoodType(foodTypeWein);

        assertDoesNotThrow(() -> productService.validateEntity(merlot));
    }

    @DisplayName("Test of quality validations")
    @Test
    void testQualityValidation()
    {
        // Quality too low
        Product brie = new Product();
        brie.setLabel("Brie");
        brie.setQuality(25);
        brie.setInitialPrice(BigDecimal.valueOf(3.00));
        brie.setFoodType(foodTypeKaese);
        brie.setExpirationDate(LocalDate.now().plusDays(80));

        Throwable brieException = assertThrows(EntityValidationException.class, () -> productService.validateEntity(brie));
        assertEquals("Brie: Die Qualität des Produktes ist unter das Minimum dieser Lebensmittelart!", brieException.getMessage());


        // quality too low
        Product champagne = new Product();
        champagne.setLabel("Champagne");
        champagne.setQuality(-3);
        champagne.setInitialPrice(BigDecimal.valueOf(4.00));
        champagne.setFoodType(foodTypeWein);

        Throwable champagneException = assertThrows(EntityValidationException.class, () -> productService.validateEntity(champagne));
        assertEquals("Champagne: Die Qualität des Produktes ist unter das Minimum dieser Lebensmittelart!", champagneException.getMessage());


        // quality too high
        Product malbec = new Product();
        malbec.setLabel("Malbec");
        malbec.setQuality(55);
        malbec.setInitialPrice(BigDecimal.valueOf(5.00));
        malbec.setFoodType(foodTypeWein);

        Throwable malbecException = assertThrows(EntityValidationException.class, () -> productService.validateEntity(malbec));
        assertEquals("Malbec: Die Qualität des Produktes ist über das Maximum dieser Lebensmittelart!", malbecException.getMessage());
    }

    @DisplayName("Test of expiration date validations")
    @Test
    void testExpirationDateValidation()
    {
        // Expiration Date too late
        Product camembert = new Product();
        camembert.setLabel("Camembert");
        camembert.setQuality(35);
        camembert.setInitialPrice(BigDecimal.valueOf(5.00));
        camembert.setFoodType(foodTypeKaese);
        camembert.setExpirationDate(LocalDate.now().plusDays(105));

        Throwable brieException = assertThrows(EntityValidationException.class, () -> productService.validateEntity(camembert));
        assertEquals("Camembert: Das Verfallsdatum liegt zu spät in der Zukunft für die Lebensmittelart!", brieException.getMessage());


        // Expiration Date too early
        Product emmentaler = new Product();
        emmentaler.setLabel("Emmentaler");
        emmentaler.setQuality(35);
        emmentaler.setInitialPrice(BigDecimal.valueOf(5.00));
        emmentaler.setFoodType(foodTypeKaese);
        emmentaler.setExpirationDate(LocalDate.now().plusDays(20));

        Throwable emmentalerException = assertThrows(EntityValidationException.class, () -> productService.validateEntity(emmentaler));
        assertEquals("Emmentaler: Das Verfallsdatum liegt zu nah in der Zukunft für die Lebensmittelart!", emmentalerException.getMessage());

        // Has expiration date
        Product rotling = new Product();
        rotling.setLabel("Rotling");
        rotling.setQuality(15);
        rotling.setInitialPrice(BigDecimal.valueOf(3.00));
        rotling.setFoodType(foodTypeWein);
        rotling.setExpirationDate(LocalDate.now().plusDays(20));

        Throwable rotlingException = assertThrows(EntityValidationException.class, () -> productService.validateEntity(rotling));
        assertEquals("Rotling: Lebensmittelart darf kein Verfallsdatum haben!", rotlingException.getMessage());
    }


}
