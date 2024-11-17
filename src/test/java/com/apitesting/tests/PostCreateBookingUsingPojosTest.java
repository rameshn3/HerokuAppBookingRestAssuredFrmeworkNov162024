package com.apitesting.tests;

import com.apitesting.pojos.Booking;
import com.apitesting.pojos.BookingDates;
import com.apitesting.pojos.CreateBookingResponse;
import com.apitesting.utils.JsonPathValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

public class PostCreateBookingUsingPojosTest{

    @Test
    public void createBooking() throws JsonProcessingException {
        // Prepare request body
        BookingDates bookingDates = new BookingDates("2024-10-30", "2024-11-01");
        Booking booking = new Booking("Kanti", "patnaik", "lunch", 2500, true, bookingDates);

        // Serialize to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String reqBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);
        System.out.println("Request Body: " + reqBody);

        // Make POST request
        Response createResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(reqBody)
                .baseUri("https://restful-booker.herokuapp.com/booking")
                .post()
                .then()
                .assertThat()
                .statusCode(200)
                .body("booking.firstname", Matchers.equalTo("Kanti"))
                .extract()
                .response();

        System.out.println("Create Response: ");
        createResponse.prettyPrint();

        // Extract booking ID
        int bookingId = createResponse.jsonPath().getInt("bookingid");
        System.out.println("Booking ID: " + bookingId);

        // Deserialize response to POJO
        CreateBookingResponse createBookingResp = createResponse.as(CreateBookingResponse.class);
        Booking bookingResp = createBookingResp.getBooking();

        System.out.println("First Name: " + bookingResp.getFirstname());
        System.out.println("Last Name: " + bookingResp.getLastname());
        System.out.println("Checkin Date: " + bookingResp.getBookingdates().getCheckin());

        // Make GET request
        Response getResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("bookingID", bookingId)
                .baseUri("https://restful-booker.herokuapp.com/booking")
                .get("{bookingID}")
                .then()
                .assertThat()
                .statusCode(200)
                .body("firstname", Matchers.equalTo("Kanti"))
                .extract()
                .response();

        System.out.println("Get Response: ");
        getResponse.prettyPrint();
    }

}
