package demo.stepdefinitions;

import demo.util.Imagium;
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

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;


public class AddJobStepDefinitions {


    private final Logger log = LoggerFactory.getLogger(AddJobStepDefinitions.class);

/*
**   Notice how we did not need to instantiate the Steps class NavigationPage
*    When you annotated a member variable of this class with the @Steps annotation,
*    Serenity BDD will automatically instantiate it for you.
*/
    @Steps
    NavigationPage navigationPage;

    @Steps
    Imagium imagium;

    String uid = "";
    String scrBase64 = "";

    public void takeScreenshot(String testName) {
       // uid = imagium.getUID(testName,  System.getenv("IMAGIUM_API_KEY"));
        uid = imagium.getUID(testName, "cd827532-cb61-4082-be3a-a331e0822cbd");
        scrBase64 = ((TakesScreenshot) navigationPage.getDriver()).getScreenshotAs(OutputType.BASE64);
        
        try {
            imagium.postRequest(testName, uid, scrBase64);
        } catch(Exception e) {
            log.info("Exception caught");
        }
    }

    @Before()
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
       
    }

    @After()
    public void drawTheCurtain() {
        OnStage.drawTheCurtain();
       
    }

    @Given("^(?:.*) is at the job board$")
    public void jamesIsAtTheJobBoard() {
        theActorCalled("james").attemptsTo(Open.browserOn().the(navigationPage));
        takeScreenshot("Jobs Listing");
       
    }
    
    @When("^(?:.*) add a new job with name \"([^\"]*)\" duration \"([^\"]*)\" and \"([^\"]*)\"")
    public void jamesAddANewJobWithNameDurationAnd(String name, String duration, String date) {
         theActorInTheSpotlight().attemptsTo(Click.on(NavigationPage.ADD_JOB));
         takeScreenshot("Adding Job");
         theActorInTheSpotlight().attemptsTo(Add.jobName(name));
         theActorInTheSpotlight().attemptsTo
         (SelectFromOptions.byVisibleText(duration).from(AddJobPage.JOB_DURATION));
         theActorInTheSpotlight().attemptsTo(Add.jobDate(date));
         theActorInTheSpotlight().attemptsTo(Click.on(AddJobPage.SUBMIT));
    }
    
    @Then("^he is able to see the new job added$")
    public void heIsAbleToSeeTheNewJobAdded() {
        takeScreenshot("New Job Added");
    
    }
}
