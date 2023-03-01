package com.example.springboot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Components {
  private MethodResponse latestResponse;
  private String latestResponseType;
  private MethodCallHandler methodCallHandler = new MethodCallHandler();

  @When("calling the method {word} and the server responds with")
  public void calling_method_server_responds(String method, String response) {
    // Prepare for later steps:
    latestResponse = methodCallHandler.callMethod(method, new ArrayList<>(), response);
  }

  @Then("the response should be of type {word}")
  public void response_of_type(String type) {
    latestResponseType = latestResponse.getResultOfMethodCall().getClass().getSimpleName();
    assertEquals(type, latestResponse.getResultOfMethodCall().getClass().getSimpleName());
  }

  @And("the response should have a property {word} with value {word}")
  public void response_should_have_property(String propName, String propValue) {
    String actualProp =
        methodCallHandler.getPropertyOnObject(
            propName, latestResponse.getResultOfMethodCall(), latestResponseType);
    assertEquals(propValue, actualProp);
  }

  @When("calling the method {word} with parameters {string}")
  public void calling_the_method_with(String method, String rawParameters) {
    System.err.println(method);
    System.err.println(rawParameters);
    List<String> parameters = Arrays.stream(rawParameters.split(",")).toList();
    latestResponse =
        methodCallHandler.callMethod(
            method,
            parameters,
            "null"); // TODO putting null without quotes here is incorrect, so make it clearer!
  }

  @Then("the requested URL should be {word}")
  public void the_requested_url_should_be(String expectedUrl) {
    assertEquals(expectedUrl, latestResponse.getUrlRequested());
  }

  @After
  public void after() {
    latestResponse = null;
    latestResponseType = null;
  }
}
