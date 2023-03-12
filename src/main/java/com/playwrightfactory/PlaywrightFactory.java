package com.playwrightfactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.playwright.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Properties;

public class PlaywrightFactory {
    public static final ThreadLocal<Playwright> playwrightThread=new ThreadLocal<>();
    public static final ThreadLocal<BrowserType> browserTypeThread=new ThreadLocal<>();
    public static final ThreadLocal<Browser> browserThread=new ThreadLocal<>();
    public static final ThreadLocal<BrowserContext> browserContextThread=new ThreadLocal<>();
    public static final ThreadLocal<Page> pageThread=new ThreadLocal<>();
    public static final ThreadLocal<Properties> propertiesThread=new ThreadLocal<>();
    public static final ThreadLocal<JSONObject> jsonObjectThread=new ThreadLocal<>();

    public static final ThreadLocal<JsonArray> jsonArrayThread=new ThreadLocal<>();

    public static synchronized Properties getProperties() throws IOException {

        if(propertiesThread.get()==null){
            File file=new File("src/test/resources/appconfig.properties");
            FileInputStream fileInputStream=new FileInputStream(file);
            Properties properties=new Properties();
            properties.load(fileInputStream);
            propertiesThread.set(properties);
        }
        return propertiesThread.get();
    }


    public static synchronized Page getPage(){
        if(playwrightThread.get()==null){
            Playwright playwright=Playwright.create();
            playwrightThread.set(playwright);
            Page page=createPage(playwright);
            pageThread.set(page);
        }
        return pageThread.get();
    }

    public static synchronized Page createPage(Playwright playwright){
        String browserName="chrome";
        Browser browser=getBrowserType(playwright,browserName);
        Browser.NewContextOptions newContextOptions=new Browser.NewContextOptions();
        newContextOptions.acceptDownloads=true;
        BrowserContext context=browser.newContext(newContextOptions);
        browserThread.set(browser);
        browserContextThread.set(context);
        return context.newPage();
    }

    public static synchronized Browser getBrowserType(Playwright playwright,String browserName){
        switch (browserName.toLowerCase()){
            case "chromium":
                BrowserType browserType=playwright.chromium();
                browserTypeThread.set(browserType);
                return browserType.launch();
            case "chrome":
                BrowserType chrome=playwright.chromium();
                browserTypeThread.set(chrome);
                return chrome.launch(new BrowserType.LaunchOptions().setHeadless(false).setChannel("chrome"));
            case "webkit":
                BrowserType webkit=playwright.webkit();
                browserTypeThread.set(webkit);
                return webkit.launch();
            case "firefox":
                BrowserType firefox=playwright.firefox();
                browserTypeThread.set(firefox);
                return firefox.launch();
            default:
                throw new IllegalArgumentException();
        }
    }
    public static synchronized void closePage(){
        Playwright playwright=playwrightThread.get();
        Page page=pageThread.get();
        if(playwright!=null){
            page.close();
            pageThread.remove();
            playwright.close();
            playwrightThread.remove();
        }
    }
    public static String getProperty(String key) throws IOException {
        if(propertiesThread.get()==null)getProperties();
        return propertiesThread.get().getProperty(key);
    }
public static synchronized JSONObject getJsonObject(String fileName) throws IOException, ParseException {
        File file=new File(fileName);
    FileReader fileReader=new FileReader(file);
    JSONParser jsonParser=new JSONParser();
    JSONObject object=(JSONObject)jsonParser.parse(fileReader);
    jsonObjectThread.set(object);
    return jsonObjectThread.get();
}

public static synchronized JSONArray getJSONArray(String key){
        JSONArray jsonArray=(JSONArray)jsonObjectThread.get().get(key);
                return jsonArray;
}

}
