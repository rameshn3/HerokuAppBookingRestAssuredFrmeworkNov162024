package com.apitesting.tests;

import com.apitesting.pojos.Booking;
import com.apitesting.pojos.BookingDates;
import com.apitesting.pojos.CreateBookingResponse;
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

public class PostCreateBookingUsingJsonFileTest {

    @Test(dataProvider = "getTestData")
    public void createBooking(Map<String, Object> bookingData) throws JsonProcessingException {
        if (bookingData == null || bookingData.isEmpty()) {
            throw new RuntimeException("Test data is null or empty.");
        }

        // Extract fields
        String firstname = (String) bookingData.get("firstname");
        String lastname = (String) bookingData.get("lastname");
        String additionalneeds = (String) bookingData.get("additionalneeds");
        int totalprice = (int) bookingData.get("totalprice");
        boolean depositpaid = (boolean) bookingData.get("depositpaid");

        // Extract bookingdates
        @SuppressWarnings("unchecked")
        Map<String, String> bookingdates = (Map<String, String>) bookingData.get("bookingdates");
        String checkin = bookingdates != null ? bookingdates.get("checkin") : null;
        String checkout = bookingdates != null ? bookingdates.get("checkout") : null;

        // Validate required fields
        if (firstname == null || lastname == null || checkin == null || checkout == null) {
            throw new RuntimeException("Missing required fields in test data.");
        }

        // Log extracted data
        System.out.printf("Firstname: %s, Lastname: %s, Total Price: %d, Deposit Paid: %b%n",
                firstname, lastname, totalprice, depositpaid);
        System.out.printf("Checkin Date: %s, Checkout Date: %s%n", checkin, checkout);

        // Prepare request body
        BookingDates bookingDates = new BookingDates(checkin, checkout);
        Booking bookingRequest = new Booking(firstname, lastname, additionalneeds, totalprice, depositpaid, bookingDates);

        // Serialize request body to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bookingRequest);

        // Send POST request
        Response createResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .baseUri("https://restful-booker.herokuapp.com/booking")
                .post()
                .then()
                .statusCode(200)
                .body("booking.firstname", Matchers.equalTo(firstname))
                .extract()
                .response();

        System.out.println("Create Response:");
        createResponse.prettyPrint();

        // Extract and validate booking ID
        int bookingId = createResponse.jsonPath().getInt("bookingid");
        System.out.printf("Booking ID: %d%n", bookingId);

        // Deserialize response to POJO
        CreateBookingResponse createBookingResponse = createResponse.as(CreateBookingResponse.class);
        Booking bookingResponse = createBookingResponse.getBooking();
        System.out.printf("Response Firstname: %s, Response Checkin Date: %s%n",
                bookingResponse.getFirstname(), bookingResponse.getBookingdates().getCheckin());

        // Send GET request to validate
        Response getResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("bookingID", bookingId)
                .baseUri("https://restful-booker.herokuapp.com/booking")
                .get("/{bookingID}")
                .then()
                .statusCode(200)
                .body("firstname", Matchers.equalTo(firstname))
                .extract()
                .response();

        System.out.println("Get Response:");
        getResponse.prettyPrint();
    }

    @DataProvider(name = "getTestData")
    public Object[] getTestData() {
        try {
            String jsonTestData = FileUtils.readFileToString(
                    new File(System.getProperty("user.dir") + "/src/test/java/com/apitesting/resources/data.json"),
                    "UTF-8"
            );
            JSONArray jsonArray = JsonPath.read(jsonTestData, "$");
            return jsonArray.toArray();
        } catch (IOException e) {
            throw new RuntimeException("Error reading test data from JSON file.", e);
        }
    }
}
