package com.apitesting.tests;

import com.apitesting.utils.JsonPathValidator;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

public class PostCreateBookingTest{

    @Test

    public void createBooking(){
        //prepare request body
        JSONObject booking = new JSONObject();
        JSONObject bookingDates = new JSONObject();
        booking.put("firstname","Ramesh");
        booking.put("lastname","Ch");
        booking.put("totalprice",1500);
        booking.put("depositpaid",true);
        booking.put("additionalneeds","breakfast");
        booking.put("bookingdates",bookingDates);

        bookingDates.put("checkin","2024-10-30");
        bookingDates.put("checkout","2024-11-3");

        Response createresponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(booking.toString())
                .baseUri("https://restful-booker.herokuapp.com/booking")
                .when()
                .post()
                .then()
                .assertThat()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .body("booking.firstname",Matchers.equalTo("Ramesh"))
                .extract()
                .response();
        System.out.println("Create Response is:");
        createresponse.prettyPrint();
        //int bookingId = createresponse.path("bookingid");
        int bookingId = JsonPathValidator.read(createresponse,"bookingid");
        System.out.println("Create Response booking id is:"+bookingId);
      Response getResp =  RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("bookingID",bookingId)
                .baseUri("https://restful-booker.herokuapp.com/booking")
                .when()
                .get("{bookingID}")
                .then()
                .assertThat()
                .statusCode(200)
                .statusLine("HTTP/1.1 200 OK")
                .body("firstname",Matchers.equalTo("Ramesh"))
                .extract()
                .response();

        System.out.println("Get Response is:");
        getResp.prettyPrint();
    }
}
