package com.apitesting.utils;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.testng.Assert;

public class SchemaValidator{

    public static void validateSchema(Response response, String schemaFileName){
        try{
            response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaFileName));
            System.out.println("Schema validation Passed..");
        }catch(AssertionError e){
            System.err.println("Schema validation failed:"+e.getMessage());
            Assert.assertTrue(false,"Schema validation failed: "+e.getMessage());
        }
    }
}
