package com.apitesting.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.restassured.response.Response;

public class GetBookingIdsTest{

    @Test
    public void getAllBookings(){
        Response response =RestAssured.given()
                                        .contentType(ContentType.JSON)
                                         .baseUri("https://restful-booker.herokuapp.com/booking")
                .when()
                    .get()
                .then()
                .assertThat()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .extract()
                .response();

        Assert.assertTrue(response.getBody().asString().contains("bookingid"));
    }

    @Test
    public void getBookingById(){
        Response response =RestAssured.given()
                .contentType(ContentType.JSON)
                .baseUri("https://restful-booker.herokuapp.com/booking/1")
                .when()
                .get()
                .then()
                .assertThat()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .extract()
                .response();

        System.out.println("Get booking by id:");
        response.prettyPrint();

    }


}
