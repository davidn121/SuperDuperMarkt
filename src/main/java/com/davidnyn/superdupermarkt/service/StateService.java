package com.davidnyn.superdupermarkt.service;

import com.davidnyn.superdupermarkt.application.exceptions.EntityValidationException;
import com.davidnyn.superdupermarkt.data.dto.ProductCreationDto;
import com.davidnyn.superdupermarkt.data.model.FoodType;
import com.davidnyn.superdupermarkt.data.model.Product;
import com.davidnyn.superdupermarkt.service.web.FoodTypeService;
import com.davidnyn.superdupermarkt.service.web.ProductService;
import com.opencsv.exceptions.CsvException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Setter
@Getter
@Service
@RequiredArgsConstructor
@Slf4j
public class StateService {

    private final ProductService productService;

    private final FoodTypeService foodTypeService;

    private final CsvImportService csvImportService;

    private List<Product> products = new ArrayList<>();

    private List<FoodType> foodTypes = new ArrayList<>();

    @PostConstruct
    private void postConstruct()
    {
        initializeFoodTypes();
    }

    private void initializeFoodTypes()
    {
        FoodType cheese = new FoodType();
        cheese.setId(1L);
        cheese.setName("Kaese");
        cheese.setMinimumQuality(30);
        cheese.setMinimumShelfLifeInDays(50);
        cheese.setMaximumShelfLifeInDays(100);
        cheese.setDailyQualityChange(BigDecimal.valueOf(-1));
        cheese.setHasUpdatedDailyPrice(true);

        foodTypes.add(cheese);

        FoodType wine = new FoodType();
        wine.setId(2L);
        wine.setName("Wein");
        wine.setMinimumQuality(0);
        wine.setMaximumQuality(50);
        wine.setDailyQualityChange(BigDecimal.valueOf(0.1));
        wine.setHasUpdatedDailyPrice(false);

        foodTypes.add(wine);
    }

    // Mocks incoming productDtos which are going to be imported
    public List<String> importExampleProducts()
    {
        // Fulfills all criteria of cheese, has to be disposded on day 6 due to low quality
        ProductCreationDto gouda = ProductCreationDto.builder("Gouda", 35, BigDecimal.valueOf(4.00), "Kaese")
                .expirationDate(LocalDate.now().plusDays(55))
                .build();

        // Fulfills all criteria of cheese, has to de disposed on day 2 due to low quality
        ProductCreationDto feta = ProductCreationDto.builder("Feta", 31, BigDecimal.valueOf(2.5), "Kaese")
                .expirationDate(LocalDate.now().plusDays(80))
                .build();

        // Quality too low
        ProductCreationDto brie = ProductCreationDto.builder("Brie", 25, BigDecimal.valueOf(3.00), "Kaese")
                .expirationDate(LocalDate.now().plusDays(80))
                .build();

        // Expiration Date too late
        ProductCreationDto camembert = ProductCreationDto.builder("Camembert", 35, BigDecimal.valueOf(5.00), "Kaese")
                .expirationDate(LocalDate.now().plusDays(105))
                .build();

        // Expiration Date too early
        ProductCreationDto emmentaler = ProductCreationDto.builder("Emmentaler", 35, BigDecimal.valueOf(5.00), "Kaese")
                .expirationDate(LocalDate.now().plusDays(20))
                .build();

        Set<ProductCreationDto> wineProducts = new HashSet<>();

        // Fulfills all criteria of Wine
        ProductCreationDto riesling = ProductCreationDto.builder("Riesling", 22, BigDecimal.valueOf(6.00), "Wein")
                .build();

        // Fulfills all criteria of Wine, gains one quality point and reaches maximum quality on day 10
        ProductCreationDto merlot = ProductCreationDto.builder("Merlot", 49, BigDecimal.valueOf(6.50), "Wein")
                .build();

        // quality too low
        ProductCreationDto champagne = ProductCreationDto.builder("Champagne", -3, BigDecimal.valueOf(4.00), "Wein")
                .build();

        // quality too high
        ProductCreationDto malbec = ProductCreationDto.builder("Malbec", 55, BigDecimal.valueOf(5.00), "Wein")
                .build();

        // Has expiration date
        ProductCreationDto rotling = ProductCreationDto.builder("Rotling", 15, BigDecimal.valueOf(3.00), "Wein")
                .expirationDate(LocalDate.now().plusDays(20))
                .build();

        List<ProductCreationDto> productCreationDtos = Arrays.asList(gouda, brie, camembert, emmentaler, feta, riesling, champagne, malbec, rotling, merlot);

        List<Product> products = productService.createProducts(productCreationDtos);
        return this.addProducts(products);

    }

    public List<String> importFromDb()
    {
        List<Product> products = productService.findAll();
        return this.addProducts(products);
    }

    public List<String> importFromCsv(String fileName)
    {
        InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(fileName);

        List<Product> importedProducts = new ArrayList<>();
        try
        {
            List<ProductCreationDto> importedProductDtos = CsvImportService.parseCsv(ProductCreationDto.class, fileInputStream);
            fileInputStream.close();
            importedProducts = productService.createProducts(importedProductDtos);
        }
        catch (IOException | CsvException | ReflectiveOperationException e)
        {
            log.error(e.getMessage());
        }

        return this.addProducts(importedProducts);
    }

    public FoodType getFoodTypeByName(String name)
    {
        return this.foodTypes.stream()
                .filter(ft -> ft.getName() != null && ft.getName().equals(name))
                .findFirst()
                .orElse(null);
    }


    public String printInitialStock()
    {
        StringBuilder result = new StringBuilder();
        this.products.forEach(p -> {
            result.append(p.toString() + "\n");
        });

        return result.toString();
    }

    public String printStockOverviewForDayRange(int fromDay, int toDay)
    {
        if (products.isEmpty())
            return "Sortiment ist leer";

        products.sort(Comparator.comparing(p -> p.getFoodType().getName()));

        StringBuilder sb = new StringBuilder();

        sb.append("========== Startwerte ===========================\n");
        sb.append(printInitialStock() + "\n");
        sb.append("=================================================\n");

        for (int i = fromDay; i <= toDay; i++)
        {
            sb.append("=================================================\n")
                    .append("Tag " + i + ":\n\n");

            for (Product product : products)
            {
                sb.append(productService.getOverviewOfProductForSpecificDay(product, i) + "\n");
            }

            sb.append("\n\n");

        }

        return sb.toString();
    }


    public List<String> addProducts(List<Product> products)
    {
        List<String> validationMessages = new ArrayList<>();
        for (Product product : products)
        {
            try
            {
                productService.validateEntity(product);
                this.products.add(product);
            }
            catch (EntityValidationException e)
            {
                validationMessages.add(e.getMessage());
            }
        }

        return validationMessages;
    }

    public void addFoodTypes(Set<FoodType> foodTypes)
    {
        this.foodTypes.addAll(foodTypes);
    }
}
