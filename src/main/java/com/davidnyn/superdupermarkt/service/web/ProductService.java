package com.davidnyn.superdupermarkt.service.web;

import com.davidnyn.superdupermarkt.application.exceptions.EntityValidationException;
import com.davidnyn.superdupermarkt.data.dto.ProductCreationDto;
import com.davidnyn.superdupermarkt.data.mapper.ProductMapper;
import com.davidnyn.superdupermarkt.data.model.FoodType;
import com.davidnyn.superdupermarkt.data.model.Product;
import com.davidnyn.superdupermarkt.data.repository.ProductRepository;
import com.davidnyn.superdupermarkt.service.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService extends EntityCrudService<Product, ProductRepository> {

    private final ProductMapper   productMapper;

    @Lazy
    private final StateService    stateService;

    private final FoodTypeService foodTypeService;

    public Product toEntity(ProductCreationDto dto)
    {
        Product product = productMapper.toEntity(dto);
        // In REST Api setting we would ask the foodTypeService for this info
        product.setFoodType(stateService.getFoodTypeByName(dto.getFoodType()));
        product.setInitialPrice(dto.getInitialPrice());

        return product;
    }

    @Override
    public void validateEntity(Product product) throws EntityValidationException
    {
       FoodType foodType = product.getFoodType();

       if (foodType.getMaximumQuality() != null && product.getQuality() > foodType.getMaximumQuality())
           throw new EntityValidationException(product.getLabel() + ": Die Qualität des Produktes ist über das Maximum dieser Lebensmittelart!");

        if (foodType.getMinimumQuality() != null && product.getQuality() < foodType.getMinimumQuality())
            throw new EntityValidationException(product.getLabel() + ": Die Qualität des Produktes ist unter das Minimum dieser Lebensmittelart!");

       if (product.getExpirationDate() != null)
       {
           if (foodType.getMinimumShelfLifeInDays() == null && foodType.getMaximumShelfLifeInDays() == null)
                throw new EntityValidationException(product.getLabel() + ": Lebensmittelart darf kein Verfallsdatum haben!");

           if (foodType.getMinimumShelfLifeInDays() != null)
           {
               LocalDate minimumDate = LocalDate.now().plusDays(foodType.getMinimumShelfLifeInDays());

               if (product.getExpirationDate().isBefore(minimumDate))
                   throw new EntityValidationException(product.getLabel() + ": Das Verfallsdatum liegt zu nah in der Zukunft für die Lebensmittelart!");
           }

           // Probably not relevant in praxis because nobody would complain about the expiration date to be too late
           if (foodType.getMaximumShelfLifeInDays() != null)
           {
               LocalDate maximumDate = LocalDate.now().plusDays(foodType.getMaximumShelfLifeInDays());

               if (product.getExpirationDate().isAfter(maximumDate))
                   throw new EntityValidationException(product.getLabel() + ": Das Verfallsdatum liegt zu spät in der Zukunft für die Lebensmittelart!");
           }
       }
    }


    public List<Product> createProducts(List<ProductCreationDto> productDtos)
    {
        List<Product> result = new ArrayList<>();

        for (ProductCreationDto dto : productDtos)
        {
            Product newProduct = toEntity(dto);
            result.add(newProduct);
        }

        return result;
    }

    public String getOverviewOfProductForSpecificDay(Product product, int day)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("(Produkt: " + product.getLabel() + ", ");

        if (product.getFoodType() != null && product.getFoodType().getName() != null)
            sb.append("Lebensmittelart: " + product.getFoodType().getName() + ", ");

        int calculatedQuality = foodTypeService.calculateQualityForSpecificDay(product, day);

        sb.append("Qualitaet: " + calculatedQuality + ", ");

        BigDecimal calculatedPrice = foodTypeService.calculatePrice(calculatedQuality, product);

        sb.append("Preis in Euro: " + calculatedPrice + ", ");

        boolean isToBeDisposed = foodTypeService.checkIfProductIsToBeDisposedForSpecificDay(calculatedQuality, product, day);

        sb.append("Muss entsorgt werden: " + (isToBeDisposed ? "Ja" : "Nein") + ")");

        return sb.toString();
    }

}
