package com.davidnyn.superdupermarkt;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({FoodTypeUnitTest.class, ProductValidationExceptionUnitTest.class})
public class AllUnitTest
{

}
