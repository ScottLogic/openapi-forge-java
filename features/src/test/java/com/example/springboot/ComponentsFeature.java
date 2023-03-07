package com.example.springboot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComponentsFeature {
  private MethodResponse latestResponse;
  private String latestResponseType;
  private int latestServerIndex = 0;
  private MethodCallHandler methodCallHandler = new MethodCallHandler(new TypeConverter());

  @When("calling the method {word} and the server responds with")
  public void calling_method_server_responds(String method, String response) {
    // Prepare for later steps:
    latestResponse =
        methodCallHandler.callMethod(method, new ArrayList<>(), response, latestServerIndex);
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
    List<String> parameters = Arrays.stream(rawParameters.split(",")).toList();
    latestResponse = methodCallHandler.callMethod(method, parameters, "null", latestServerIndex);
  }

  @When("calling the method {word} without params")
  public void calling_the_method_without(String method) {
    latestResponse =
        methodCallHandler.callMethod(method, new ArrayList<>(), "null", latestServerIndex);
  }

  @When("selecting the server at index {int}")
  public void selecting_the_server_at_index(int serverIndex) {
    latestServerIndex = serverIndex;
  }

  @Then("the requested URL should be {word}")
  public void the_requested_url_should_be(String expectedUrl) {
    assertEquals(expectedUrl, latestResponse.getUrlRequested());
  }

  @Then("the request header should have a cookie property with value {word}")
  public void the_request_header_should_have_a_cookie_property_with_value(String expectedCookie) {
    assertEquals(expectedCookie, latestResponse.getCookieHeader());
  }

  @After
  public void after() {
    latestResponse = null;
    latestResponseType = null;
    latestServerIndex = 0;
  }
}
