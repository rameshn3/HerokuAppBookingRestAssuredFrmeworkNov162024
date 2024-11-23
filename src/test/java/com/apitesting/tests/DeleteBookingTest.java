package com.apitesting.tests;

import com.apitesting.utils.JsonPathValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.io.File;

public class DeleteBookingTest{

    @Test
    public void deleteBooking() throws JsonProcessingException {
        try{
            String postApiRequestBody=FileUtils.readFileToString(new File(System.getProperty("user.dir")+"/src/test/java/com/apitesting/resources/singleBooking.json"),"UTF-8");

            String tokenApiRequestBody=FileUtils.readFileToString(new File(System.getProperty("user.dir")+"/src/test/java/com/apitesting/resources/tokenData.json"),"UTF-8");

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

        //Step4: Delete booking api call
            Response deleteResponse =  RestAssured.given()
                    .contentType(ContentType.JSON)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                    .header("Cookie","token="+token)
                    .baseUri("https://restful-booker.herokuapp.com/booking")
                    .when()
                    .delete("/{bookingId}",bookingId)
                    .then()
                    .assertThat()
                    .statusCode(201)
                    .extract()
                    .response();
            System.out.println("DELETE Response:");
            deleteResponse.prettyPrint();
        }catch(Exception ex){

        }
    }


}
