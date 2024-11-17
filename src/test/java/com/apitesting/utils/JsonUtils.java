package com.apitesting.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

public class JsonUtils{

    private static final ObjectMapper objectMapper = new ObjectMapper();
    //generic method to deserialize the JSON response into specified class

    public static <T> T deserialize(Response response, Class<T> targetClass){
        try{
            return objectMapper.readValue(response.getBody().asString(),targetClass);
        }catch(Exception ex){
            throw new RuntimeException("Failed to deserialize response body to "+targetClass.getName());
        }
    }
}
