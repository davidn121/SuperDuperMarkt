package com.davidnyn.superdupermarkt.application;

import com.davidnyn.superdupermarkt.service.StateService;

import lombok.AllArgsConstructor;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.List;


@ShellComponent
@AllArgsConstructor
public class InteractiveShell {

   private final StateService stateService;


    @ShellMethod(key = "import-code-products", value = "Import products from code")
    public String importFromCode()
    {
        List<String> validationMessages = stateService.importExampleProducts();

        if (validationMessages.isEmpty())
            return "Successful Import";

        StringBuilder sb = new StringBuilder();

        sb.append("Folgende Produkte konnten nicht aufgenommen werden:\n\n");

        for (String msg : validationMessages)
        {
            sb.append(msg + "\n");
        }

        return sb.toString();
    }

    @ShellMethod(key = "import-csv-products", value = "Import products from csv with file name, e.g. 'import-csv-products productImport.csv'")
    public String importFromCsv(String fileName)
    {
        List<String> validationMessages = stateService.importFromCsv(fileName);

        if (validationMessages.isEmpty())
            return "Successful Import";

        StringBuilder sb = new StringBuilder();

        sb.append("Folgende Produkte konnten nicht aufgenommen werden:\n\n");

        for (String msg : validationMessages)
        {
            sb.append(msg + "\n");
        }

        return sb.toString();
    }

    @ShellMethod(key = "import-db-products", value = "Import products from database")
    public String importFromDb()
    {
        List<String> validationMessages = stateService.importFromDb();

        if (validationMessages.isEmpty())
            return "Successful Import";

        StringBuilder sb = new StringBuilder();

        sb.append("Folgende Produkte konnten nicht aufgenommen werden:\n\n");

        for (String msg : validationMessages)
        {
            sb.append(msg + "\n");
        }

        return sb.toString();
    }

    @ShellMethod(key = "print-initial-stock", value = "Print all products in stock")
    public String printInitialStock()
    {
        return stateService.printInitialStock();
    }

    @ShellMethod(key = "print-stock-overview", value = "Print stock overview for a range between two days from now, e.g. for day 0 - day 10: 'print-stock-overview 0 10'")
    public String printStockOverviewForDayRange(int from, int to)
    {
        return stateService.printStockOverviewForDayRange(from, to);
    }
}
