package com.davidnyn.superdupermarkt.service;

import com.davidnyn.superdupermarkt.service.web.ProductService;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.sqm.sql.ConversionException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvImportService {

    private final ProductService productService;

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static <T> List<T> parseCsv(Class<T> clazz, InputStream inputStream) throws CsvException, IOException, ReflectiveOperationException
    {
        Reader reader = new InputStreamReader(inputStream);
        CSVReader csvReader = new CSVReaderBuilder(reader).build();

        List<String> fieldNames = Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).collect(Collectors.toList());
        String[] columnNames = csvReader.readNext();
        Map<String, Integer> columnMap = findCorrespondingColumnIndex(fieldNames, columnNames);


        String[] line;
        List<T> result = new ArrayList<>();
        Constructor<T> clazzConstructor = clazz.getDeclaredConstructor();

        while ((line = csvReader.readNext()) != null)
        {
            T entity = clazzConstructor.newInstance();

            for (String fieldName : fieldNames)
            {
                Integer index = columnMap.get(fieldName);
                String value = line[index];

                if (value == null || value.isBlank())
                    continue;

                Field field = entity.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(entity, transformType(value, field.getType()));
            }
            result.add(entity);
        }

        return result;
    }

    private static <T> T transformType(String value, Class<T> targetClass)
    {

        if (targetClass.equals(String.class))
            return (T) value;
        else if (targetClass.equals(Integer.class))
            return (T) Integer.valueOf(value);
        else if (targetClass.equals(BigDecimal.class))
            return (T) BigDecimal.valueOf(Double.valueOf(value));
        else if (targetClass.equals(LocalDate.class))
            return (T) LocalDate.parse(value, dateFormatter);
        else
            throw new ConversionException("No matching conversion found!");
    }

    private static Map<String, Integer> findCorrespondingColumnIndex(List<String> fieldNames, String[] columnNames)
    {
        Map<String, Integer> map = new HashMap<>();
        for (String fieldName : fieldNames)
        {
            int columnIndex = Arrays.asList(columnNames).indexOf(fieldName);
            if (columnIndex != -1)
                map.put(fieldName, columnIndex);
        }
        return map;
    }
}
