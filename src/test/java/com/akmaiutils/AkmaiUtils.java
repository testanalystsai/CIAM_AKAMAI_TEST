package com.akmaiutils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.models.DataModel;
import com.playwrightfactory.PlaywrightFactory;
import io.qameta.allure.Step;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.annotations.TestInstance;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AkmaiUtils {
    SoftAssert softAssert=new SoftAssert();
    Map<String ,String >errorID=new HashMap<>();
    @Step("Process the Data {model}")
public void processID(DataModel model) throws IOException, ParseException, InterruptedException {

        String url= PlaywrightFactory.getProperty("app_url");
        for(Map.Entry<String,String> ids:model.getClientID().entrySet()){
            processEachID(model.getOdiurl().trim(),url,ids);
        }
        System.out.println(errorID);
    }
    @Step("Process the ODI URL : {odiUrl} and ID : {data}")
    private void processEachID(String odiUrl, String url, Map.Entry<String,String>data) throws InterruptedException {
        Page page=PlaywrightFactory.getPage();
        page.navigate(url);
        page.locator("#oidc-url").type(odiUrl);
        page.locator("//span[text()='Next']").click();
        page.locator("#oidc-client-id").type(data.getKey());
        System.out.println(data.getKey());
        page.locator("//span[text()='Next']").click();
        page.locator("//span[text()='Next']").click();
        page.locator("(//span[text()='Finish'])[1]").click();
        page.locator("//span[text()='Start']").click();
        //Locator locator=page.locator("#capture_signIn_emailOrMobileNumberMultiIdentifierField");
        Thread.sleep(3000);
        List<Boolean> status=checkUI(page);
        //softAssert.assertEquals(true,locator.isVisible());
        if(status.size()==0){
            errorID.put(data.getKey(), data.getValue());
        }

        PlaywrightFactory.closePage();
    }


    public List<Boolean> checkUI(Page page){
        List<Boolean> status=new ArrayList<>();
        //try for DSTV
        try{
            if(page.locator("#content-container").isVisible()){
                status.add(true);
            }
        }catch (Exception e){

        }

        try{
            if(page.locator("#logo").isVisible()){
                status.add(true);
            }
        }catch (Exception e){

        }

        try{
            if(page.locator("//*[text()='Welcome to SuperSport']").isVisible()){
                status.add(true);
            }
        }catch (Exception e){

        }
        return status;

    }
}
