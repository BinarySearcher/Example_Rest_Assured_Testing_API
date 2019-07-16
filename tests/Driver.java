/**
 * Driver.java
 * @author Aleksandr Glavnik
 * @date 16 July 2019
 *
 * @description an exploratory mini-test suite designed to teach how the rest-assured
 * library works.
 */

import com.tngtech.java.junit.dataprovider.*;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 *  Test suite class that uses the rest-assured library. Includes static methods for Specification builders that
 *  will be used in the test methods, a static test data generator method, and an example test method
 */
@RunWith(DataProviderRunner.class)
public class Driver {

    /**
     *  static declaration of request and response specifications
     */
    private static RequestSpecification reqSpec;
    private static ResponseSpecification respSpec;

    /**
     *  static declaration of the placeName that is tested in 'firstTest'
     */
    private static String placeName;


    /**
     *  initialization of reqSpec before every time the test suite is run
     */
    @BeforeClass
    public static void createReqSpec() {
        reqSpec = new RequestSpecBuilder().setBaseUri("http://api.zippopotam.us").build();
    }

    /**
     *  initialization of respSpec before every time the test suite is run
     */
    @BeforeClass
    public static void createRespSpec() {
        //response specification checks for a status code of 200 and a json content type
        respSpec = new ResponseSpecBuilder().
                expectStatusCode(200).
                expectContentType(ContentType.JSON).
                build();
    }

    /**
     *  static method returning test data that is used by DataProviderRunner to feed into 'firstTest'
     *  hence the DataProvider and UseDataProvider annotations
     *  @return 2D array of location information that is grouped by row
     */
    @DataProvider()
    public static Object[][] zipCodesAndPlaces() {
        //data is hard-coded in for now, can pull from database, textfile, json, whatever, in the future
        return new Object[][] {
                {"us","90210","Beverly Hills"},
                {"us","12345","Schenectady"},
                {"ca","B2R","Waverley"}
        };
    }

    /**
     * @param countryCode
     * @param zipCode
     * @param expectedPlaceName
     *
     * first rest-assured test method, takes in 3 params, 2 for request, and the 3rd for validation.
     * makes series of reqeusts out to api.zippopotam.io with specific country and zip codes.
     * verifies that the cities associated with those 2 pieces of information are correctly given back
     * fails if the placeNames are not correctly identified
     */
    @Test
    @UseDataProvider("zipCodesAndPlaces")
    public void firstTest(String countryCode,String zipCode,String expectedPlaceName) {
        //result of query using rest-assured API is stored in the String field
        this.placeName =

                given().
                        //use reqSpec as specified in above build
                        //use parameters passed into test method for this rest-assured call
                                spec(reqSpec).pathParam("countryCode",countryCode).pathParam("zipCode",zipCode).
                        when().
                        //baseURI already included because of reqSpec
                                get("/{countryCode}/{zipCode}").
                        then().
                        //checks for specifications laid out in above build
                                spec(respSpec).
                        and().
                        //instead of evaluating correctness here, we extract and move check outside of rest-assured call
                        //alternative: replace extract with assertThat() and path( with body(
                                extract().
                        path("places[0].'place name'");

        //outsourced assertion. we do this because it allows for what we're checking to be stored outside
        //of the method (in this case it is placeName and it is stored in a local field).
        Assert.assertEquals(this.placeName,expectedPlaceName);
    }
}
