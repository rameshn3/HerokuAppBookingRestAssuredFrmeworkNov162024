package com.apitesting.utils;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import io.restassured.response.Response;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JsonPathValidator{


    private static String getJsonResponseAsString(Response response){
        return response.getBody().asString();
    }


    public static <T> T read(Response response, String jsonPath){
        String jsonResponse = getJsonResponseAsString(response);
        try{
            return JsonPath.read(jsonResponse,jsonPath);
        }catch(PathNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> readList(Response response,String jsonPath){
        String jsonResponse = getJsonResponseAsString(response);
        try{
            return JsonPath.read(jsonResponse,jsonPath);
        }catch(PathNotFoundException e){
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static <T> List<Map<String,T>> readListOfMaps(Response response,String jsonPath){
        String jsonResponse = getJsonResponseAsString(response);
        try{
            return JsonPath.read(jsonResponse,jsonPath);
        }catch(PathNotFoundException e){
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
