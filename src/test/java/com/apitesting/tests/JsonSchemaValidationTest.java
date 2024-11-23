package com.apitesting.tests;

import com.apitesting.utils.JsonPathValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.io.File;

public class JsonSchemaValidationTest{

    @Test
    public void jsonSchemaValidation() throws JsonProcessingException {
        try{
            String postApiRequestBody=FileUtils.readFileToString(new File(System.getProperty("user.dir")+"/src/test/java/com/apitesting/resources/singleBooking.json"),"UTF-8");

            String jsonSchema=FileUtils.readFileToString(new File(System.getProperty("user.dir")+"/src/test/java/com/apitesting/resources/jsonSchemaValidation.txt"),"UTF-8");

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

          //  System.out.println(jsonSchema);

            //Step2: Send GET request to validate
            Response getResponse=RestAssured.given()
                                              .contentType(ContentType.JSON)
                                               .pathParam("bookingID",bookingId)
                                               .baseUri("https://restful-booker.herokuapp.com/booking")
                                            .when()
                                                 .get("/{bookingID}")
                                            .then()
                                            .statusCode(200)
                                             .body(JsonSchemaValidator.matchesJsonSchema(jsonSchema))
                                            .extract().response();

            System.out.println("Get Response:");
            getResponse.prettyPrint();


        }catch(Exception ex){

        }
    }


}
