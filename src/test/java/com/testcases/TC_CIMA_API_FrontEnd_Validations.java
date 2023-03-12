package com.testcases;

import com.akmaiutils.AkmaiUtils;
import com.apiutils.RestUtils;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.models.DataModel;
import com.playwrightfactory.PlaywrightFactory;
import io.qameta.allure.Story;
import io.restassured.path.json.JsonPath;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TC_CIMA_API_FrontEnd_Validations {
    private Playwright playwright;
    private APIRequestContext request;

    @Test
    @Story("")
    public void checkAPI() throws IOException, ParseException, InterruptedException {
        RestUtils restUtils = new RestUtils();
        //get all your api clients
        String environment = PlaywrightFactory.getProperty("ENV");
        String appID = PlaywrightFactory.getProperty(environment + "_APP_ID");
        String resource = "/config/" + appID + "/clients";
        //collect any one id from json response
        String response = restUtils.stimulateGet(resource);
        JSONArray array = restUtils.processArrayJsonOutPut(response);
        JsonPath jsonPath = new JsonPath(array.get(0).toString());
        String client_id = jsonPath.get("_id").toString();

//stimulate GET API CLIENT SETTING and get ODI_URL
        resource = "/config/" + appID + "/clients/" + client_id + "/settings";
        response = restUtils.stimulateGet(resource);
        jsonPath = new JsonPath(response);
        String odi_url = jsonPath.get("_global.custom.oidc_url").toString();
//Get AuthToken
        String customer_id = PlaywrightFactory.getProperty(environment + "_CUSTOMER_ID");
        resource = "/" + customer_id + "/login/token";
        response = restUtils.stimulatePost(resource);
        jsonPath = new JsonPath(response);
        String adminToken = jsonPath.get("access_token");
        resource = "/" + customer_id + "/config/clients?=";
        //Authenticate with bearer token and get all id's
        response = restUtils.stimulateGetWithBearer(resource, adminToken);
Map<String, String> clientIDs=new HashMap<>();
        JSONParser jsonParser=new JSONParser();
        JSONObject jsonObject=(JSONObject)jsonParser.parse(response);
        jsonObject=(JSONObject)jsonParser.parse(jsonObject.get("_embedded").toString());
        JSONArray arry=(JSONArray)jsonObject.get("clients");
       for(int i=0;i<=arry.size()-1;i++){
           jsonObject=(JSONObject)jsonParser.parse(arry.get(i).toString());
           clientIDs.put(jsonObject.get("id").toString(),jsonObject.get("name").toString());
       }
        System.out.println(clientIDs);
        DataModel dataModel=new DataModel();
        dataModel.setOdiurl(odi_url);
        dataModel.setClientID(clientIDs);

        AkmaiUtils akmaiUtils=new AkmaiUtils();
        akmaiUtils.processID(dataModel);
    }

}

