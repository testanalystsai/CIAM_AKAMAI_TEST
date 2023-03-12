package com.apiutils;

import com.playwrightfactory.PlaywrightFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.IOException;
import java.util.ArrayList;

public class RestUtils {
    public String  stimulateGet(String resource) throws IOException, ParseException {
        String environment= PlaywrightFactory.getProperty("ENV");
        String username=PlaywrightFactory.getProperty(environment+"_USER_NAME");
        String password=PlaywrightFactory.getProperty(environment+"_PASSWORD");
        String baseURL=PlaywrightFactory.getProperty(environment+"_BASEURL");


        RestAssured.baseURI=baseURL;
        Response response=RestAssured
                .given()
                .auth()
                .preemptive()
                .basic(username,password)
                .when()
                .get(resource)
                .then()
//                .log()
//                .all()
                .extract().response();
        System.out.println(response.getStatusCode());
        return  response.asString();
    }


    public JSONArray processArrayJsonOutPut(String response) throws ParseException {
        JSONParser parser=new JSONParser();
        JSONArray jsonArray=(JSONArray)parser.parse(response);
        return jsonArray;
    }
    public String  stimulatePost(String resource) throws IOException, ParseException {
        String environment= PlaywrightFactory.getProperty("ENV");
        String username=PlaywrightFactory.getProperty(environment+"_CONFIG_CLIENT_ID");
        String password=PlaywrightFactory.getProperty(environment+"_CONFIG_CLIENT_SECRET");
        String baseURL=PlaywrightFactory.getProperty(environment+"_BASEURL");


        RestAssured.baseURI=baseURL;
        String body = String.format("grant_type=%s&scope=%s", "client_credentials", "*:**");
        Response response=RestAssured
                .given()
                .with()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(body)
                .auth()
                .preemptive()
                .basic(username,password)
                .when()
                .post(resource)
                .then()
//                .log()
//                .all()
                .extract().response();
        System.out.println(response.getStatusCode());
        return  response.asString();
    }
    public String  stimulateGetWithBearer(String resource,String token) throws IOException, ParseException {
        String environment= PlaywrightFactory.getProperty("ENV");
        String baseURL=PlaywrightFactory.getProperty(environment+"_BASEURL")+resource;
        RestAssured.baseURI=baseURL;
        Response response=RestAssured
                .given()
                .header(
                        "Authorization","Bearer "+token
                )
                .when()
                .get()
                .then()
//                .log()
//                .all()
                .extract().response();
        System.out.println(response.getStatusCode());
        return  response.asString();
    }
}
