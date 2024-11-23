package com.apitesting.tests;

import com.apitesting.pojos.Booking;
import com.apitesting.pojos.BookingDates;
import com.apitesting.pojos.CreateBookingResponse;
import com.apitesting.utils.JsonPathValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class PutUpdateBookingUsingJsonFileTest{

    @Test
    public void createBooking() throws JsonProcessingException {
        try{
            String postApiRequestBody=FileUtils.readFileToString(new File(System.getProperty("user.dir")+"/src/test/java/com/apitesting/resources/singleBooking.json"),"UTF-8");

            String tokenApiRequestBody=FileUtils.readFileToString(new File(System.getProperty("user.dir")+"/src/test/java/com/apitesting/resources/tokenData.json"),"UTF-8");

            String putApiRequestBody=FileUtils.readFileToString(new File(System.getProperty("user.dir")+"/src/test/java/com/apitesting/resources/putUpdateBooking.json"),"UTF-8");

            //1. Send POST request
            Response createResponse=RestAssured.given()
                                                  .contentType(ContentType.JSON)
                                                   .body(postApiRequestBody)
                                                   .baseUri("https://restful-booker.herokuapp.com/booking")
                                             .when()
                                                    .post()
                                             .then()
                                                    .statusCode(200)
                                                    .extract().response();

            System.out.println("Create Response:");
            createResponse.prettyPrint();

            // Extract and validate booking ID
            int bookingId=createResponse.jsonPath().getInt("bookingid");
            System.out.printf("Booking ID: %d%n",bookingId);

            //Step2: Send GET request to validate
            Response getResponse=RestAssured.given()
                                              .contentType(ContentType.JSON)
                                               .pathParam("bookingID",bookingId)
                                               .baseUri("https://restful-booker.herokuapp.com/booking")
                                            .when()
                                                 .get("/{bookingID}")
                                            .then()
                                            .statusCode(200)
                                            .extract().response();

            System.out.println("Get Response:");
            getResponse.prettyPrint();

            //Step3: Token Generation
           Response toeknResponse =  RestAssured.given()
                             .contentType(ContentType.JSON)
                             .body(tokenApiRequestBody)
                             .baseUri("https://restful-booker.herokuapp.com/auth")
                    .when()
                             .post()
                    .then()
                            .assertThat()
                            .statusCode(200)
                            .extract()
                             .response();
        String token = JsonPathValidator.read(toeknResponse,"token");

        //Step4: Update with PUT api call
            Response putResponse =  RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(putApiRequestBody)
                    .header("Cookie","token="+token)
                    .baseUri("https://restful-booker.herokuapp.com/booking")
                    .when()
                    .put("/{bookingId}",bookingId)
                    .then()
                    .assertThat()
                    .statusCode(200)
                    .body("firstname",Matchers.equalTo("Jim"))
                    .body("lastname",Matchers.equalTo("john"))
                    .extract()
                    .response();

        }catch(Exception ex){

        }
    }


}
