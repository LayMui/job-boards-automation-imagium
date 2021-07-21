package demo.stepdefinitions;

import demo.util.Imagium;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.thucydides.core.webdriver.WebDriverFacade;
import net.serenitybdd.core.Serenity;
import demo.tasks.Add;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import demo.pageobjects.AddJobPage;
import demo.pageobjects.NavigationPage;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.actions.SelectFromOptions;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.matchers.WebElementStateMatchers;
import net.serenitybdd.screenplay.questions.WebElementQuestion;
import net.thucydides.core.annotations.Steps;

import org.json.simple.JSONObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;

import java.io.IOException;


public class AddJobStepDefinitions {


    private final Logger log = LoggerFactory.getLogger(AddJobStepDefinitions.class);

/*
**   Notice how we did not need to instantiate the Steps class NavigationPage
*    When you annotated a member variable of this class with the @Steps annotation,
*    Serenity BDD will automatically instantiate it for you.
*/
    @Steps
    NavigationPage navigationPage;


    static String uuid;

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

    public void takeScreenshot(String testName) {
       // uid = imagium.getUID(testName,  System.getenv("IMAGIUM_API_KEY"));
        String scrBase64 = "";
        scrBase64 = ((TakesScreenshot) navigationPage.getDriver()).getScreenshotAs(OutputType.BASE64);
        try {
            log.info("UUID: " + uuid);
            Imagium.postRequest(testName, uuid, scrBase64);
        } catch(Exception e) {
            log.info("Exception caught");
        }
    }

    @Before()
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
        log.info("Before UUID");
        uuid = getUID("Job Boards", "cd827532-cb61-4082-be3a-a331e0822cbd");  
        log.info("UUID: " + uuid);
    }

    @After()
    public void drawTheCurtain() {
        OnStage.drawTheCurtain();
       
    }

    @Given("^(?:.*) is at the job board$")
    public void jamesIsAtTheJobBoard() {
        theActorCalled("james").attemptsTo(Open.browserOn().the(navigationPage));
        ((JavascriptExecutor) navigationPage.getDriver()).
        executeScript("function is_gif_image(i) {\n  return /^(?!data:).*\\.gif/i.test(i.src);\n}\n\nfunction freeze_gif(i) {\n  var c = document.createElement('canvas');\n  var w = c.width = i.width;\n var h = c.height = i.height;\n  c.getContext('2d').drawImage(i, 0, 0, w, h);\n  try {\n    i.src = c.toDataURL(\"image/gif\"); // if possible, retain all css aspects\n  } catch(e) { // cross-domain -- mimic original with all its tag attributes\n    for (var j = 0, a; a = i.attributes[j]; j++)\n      c.setAttribute(a.name, a.value);\n    i.parentNode.replaceChild(c, i);\n  }\n}\n\n\n[].slice.apply(document.images).filter(is_gif_image).map(freeze_gif);");
        takeScreenshot("Jobs Listing");
       
    }
    
    @When("^(?:.*) add a new job with name \"([^\"]*)\" duration \"([^\"]*)\" and \"([^\"]*)\"")
    public void jamesAddANewJobWithNameDurationAnd(String name, String duration, String date) {
         theActorInTheSpotlight().attemptsTo(Click.on(NavigationPage.ADD_JOB));
         theActorInTheSpotlight().attemptsTo(Add.jobName(name));
         theActorInTheSpotlight().attemptsTo
         (SelectFromOptions.byVisibleText(duration).from(AddJobPage.JOB_DURATION));
         theActorInTheSpotlight().attemptsTo(Add.jobDate(date));
         takeScreenshot("Adding Job");
         theActorInTheSpotlight().attemptsTo(Click.on(AddJobPage.SUBMIT));
    }
    
    @Then("^he is able to see the new job added$")
    public void heIsAbleToSeeTheNewJobAdded() {
        ((JavascriptExecutor) navigationPage.getDriver()).
        executeScript("function is_gif_image(i) {\n  return /^(?!data:).*\\.gif/i.test(i.src);\n}\n\nfunction freeze_gif(i) {\n  var c = document.createElement('canvas');\n  var w = c.width = i.width;\n var h = c.height = i.height;\n  c.getContext('2d').drawImage(i, 0, 0, w, h);\n  try {\n    i.src = c.toDataURL(\"image/gif\"); // if possible, retain all css aspects\n  } catch(e) { // cross-domain -- mimic original with all its tag attributes\n    for (var j = 0, a; a = i.attributes[j]; j++)\n      c.setAttribute(a.name, a.value);\n    i.parentNode.replaceChild(c, i);\n  }\n}\n\n\n[].slice.apply(document.images).filter(is_gif_image).map(freeze_gif);");
        takeScreenshot("New Job board");
    
    }
}
