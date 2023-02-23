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
  private Object latestResponse;
  private String latestResponseType;
  private MethodCallHandler methodCallHandler = new MethodCallHandler();

  @When("calling the method {word} and the server responds with")
  public void calling_method_server_responds(String method, String response) {
    // Prepare for later steps:
    latestResponse = methodCallHandler.callMethod(method, new ArrayList<>(), response);
  }

  @Then("the response should be of type {word}")
  public void response_of_type(String type) {
    latestResponseType = type;
    assertEquals(type, latestResponse.getClass().getSimpleName());
  }

  @And("the response should have a property {word} with value {word}")
  public void response_should_have_property(String propName, String propValue) {
    String actualProp =
        methodCallHandler.getPropertyOnObject(propName, latestResponse, latestResponseType);
    assertEquals(propValue, actualProp);
  }

  @When("calling the method {word} with parameters {string}")
  public void calling_the_method_with(String method, String rawParameters) {
    System.err.println(method);
    System.err.println(rawParameters);
    List<String> parameters = Arrays.stream(rawParameters.split(",")).toList();
    methodCallHandler.callMethod(method, parameters, null);
  }

  @After
  public void after() {
    latestResponse = null;
    latestResponseType = null;
  }
}
