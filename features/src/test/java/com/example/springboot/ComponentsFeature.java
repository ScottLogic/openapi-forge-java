package com.example.springboot;

import static org.junit.jupiter.api.Assertions.*;

import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ComponentsFeature {
  private MethodResponse latestResponse;
  private String latestResponseType;
  private int latestServerIndex = 0;
  private MethodCallHandler methodCallHandler = new MethodCallHandler(new TypeConverter());
  private JavaTypeToGenericType javaTypeToGenericType = new JavaTypeToGenericType();

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

  @Then("it should generate a model object named {word}")
  public void it_should_generate_a_model_object_named(String modelObjectName)
      throws ClassNotFoundException, MalformedURLException {
    assertTrue(methodCallHandler.doesClassExist(modelObjectName));
  }

  @And("the response should have a property {word} with value {word}")
  public void response_should_have_property(String propName, String propValue) {
    String actualProp =
        methodCallHandler.getPropertyOnObject(propName, latestResponse, latestResponseType);
    assertEquals(propValue, actualProp);
  }

  @And("{word} should have an optional property named {word} of type {word}")
  public void should_have_an_optional_property_named(
      String modelObjectName, String property, String expectedType)
      throws MalformedURLException, NoSuchFieldException, ClassNotFoundException {
    // We currently do not have a way to distinguish between optional and required properties
    should_have_a_property_named(modelObjectName, property, expectedType);
  }

  @And("{word} should have a required property named {word} of type {word}")
  public void should_have_a_required_property_named(
      String modelObjectName, String property, String expectedType)
      throws MalformedURLException, NoSuchFieldException, ClassNotFoundException {
    // We currently do not have a way to distinguish between optional and required properties
    should_have_a_property_named(modelObjectName, property, expectedType);
  }

  public void should_have_a_property_named(
      String modelObjectName, String property, String expectedType)
      throws MalformedURLException, ClassNotFoundException, NoSuchFieldException {
    assertTrue(methodCallHandler.doesClassExist(modelObjectName));
    assertTrue(methodCallHandler.classHasProperty(modelObjectName, property));
    String actualType = methodCallHandler.getTypeOfClassProperty(modelObjectName, property);
    assertEquals(expectedType, javaTypeToGenericType.convert(actualType));
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

  @When("calling the spied method {word} without params")
  public void calling_the_spied_method_without(String method) {
    latestResponse =
        methodCallHandler.callMethod(method, new ArrayList<>(), "null", latestServerIndex);
  }

  @When("calling the method {word} with object {}")
  public void calling_the_method_with_object(String method, String objectAsString) {
    List<String> parameters = Collections.singletonList(objectAsString);
    latestResponse = methodCallHandler.callMethod(method, parameters, "null", latestServerIndex);
  }

  @When("selecting the server at index {int}")
  public void selecting_the_server_at_index(int serverIndex) {
    latestServerIndex = serverIndex;
  }

  @Then("the requested URL should be {word}")
  public void the_requested_url_should_be(String expectedUrl) {
    assertEquals(expectedUrl, latestResponse.getUrlRequested());
  }

  @Then("the request method should be of type {word}")
  public void the_request_method_should_be_of_type(String expectedHttpVerb) {
    assertEquals(expectedHttpVerb, latestResponse.getRequestMethod().toLowerCase());
  }

  @Then("the request header should have a cookie property with value {word}")
  public void the_request_header_should_have_a_cookie_property_with_value(String expectedHeader) {
    assertEquals(expectedHeader, latestResponse.getHeaders().get("cookie"));
  }

  @Then("the request should have a header property with value {word}")
  public void the_request_should_have_a_header_property_with_value(String expectedHeader) {
    assertEquals(expectedHeader, latestResponse.getHeaders().get("test"));
  }

  @After
  public void after() {
    latestResponse = null;
    latestResponseType = null;
    latestServerIndex = 0;
    methodCallHandler = new MethodCallHandler(new TypeConverter());
  }
}
