package demo.util;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.SystemEnvironmentVariables;
import java.io.IOException;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.json.simple.JSONObject;

public class Imagium {
    //Get unique Test ID for a specific project using Rest Assured
    public static String getUID(String testName, String projectKey) {
        try {
            RequestSpecification request = RestAssured.given();
            request.header("content-type", "application/json");
            JSONObject json = new JSONObject();
            json.put("TestName", testName);
            json.put("ProjectKey", projectKey);
            request.body(json.toJSONString());
            Response response = request.when().post("http://192.168.1.120:91/api/GetUID");
            int code = response.getStatusCode();
            String response_id = response.getBody().asString();
            System.out.println("TestID: " + response_id);
            return response_id;
        } catch (Exception ex) {
            return ex.toString();
        }
    }

    	//Post a request for validation
	public static void postRequest(String stepName, String uid, String imagebase64) throws IOException {
		RequestSpecification request1 = RestAssured.given();
		request1.header("content-type", "application/json");
		JSONObject jo = new JSONObject();
		jo.put("TestRunID", uid.replace("\"", ""));
		jo.put("StepName", stepName);
		jo.put("ImageBase64", imagebase64);
		System.out.println("imagebase64:" + imagebase64);
		request1.body(jo.toJSONString());
		Response response1 = request1.when().post("http://192.168.1.120:91/api/Validate");
		String response_id1 = response1.getBody().asString();
		System.out.println("Response from Imagium: " + response_id1);
	}

}
