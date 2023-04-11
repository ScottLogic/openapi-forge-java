package com.openapi.forge;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.net.MalformedURLException;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

public class StepDefinitions {

  private MethodResponse latestResponse;
  private int latestServerIndex = 0;
  private MethodCallHandler methodCallHandler = new MethodCallHandler(
    new TypeConverter()
  );
  private final JavaTypeToGenericType javaTypeToGenericType = new JavaTypeToGenericType();
  private Object relevantPartOfResponse;

  @When("calling the method {word} and the server responds with")
  public void calling_method_server_responds(String method, String response) {
    // Prepare for later steps:
    latestResponse =
      methodCallHandler.callMethod(
        method,
        new ArrayList<>(),
        response,
        new HashMap<>(),
        latestServerIndex
      );
  }

  @When("calling the method {word} and the server responds with headers")
  public void calling_method_server_responds_with_headers(
    String method,
    String headers
  ) {
    latestResponse =
      methodCallHandler.callMethod(
        method,
        new ArrayList<>(),
        null,
        deserialiseHeaders(headers),
        latestServerIndex
      );
  }

  @Then("the response should be of type {word}")
  public void response_of_type(String type) {
    final String actualType;
    if (relevantPartOfResponse == null) {
      actualType =
        latestResponse.getResultOfMethodCall().getClass().getSimpleName();
    } else {
      actualType = relevantPartOfResponse.getClass().getSimpleName();
    }
    assertEquals(type, actualType);
  }

  @Then("it should generate a model object named {word}")
  public void it_should_generate_a_model_object_named(String modelObjectName) {
    assertTrue(methodCallHandler.doesClassExist(modelObjectName));
  }

  @Then("the response should be equal to {string}")
  public void response_should_be_equal_to(String expectedResponse) {
    assertEquals(
      expectedResponse,
      stripQuotesIfPresent(latestResponse.getResultOfMethodCall().toString())
    );
  }

  @Then("the response should be an array")
  public void theResponseShouldBeAnArray() {
    String reason =
      latestResponse.getResultOfMethodCall().getClass().getSimpleName() +
      " is not an array type";
    assertThat(
      reason,
      isListType(latestResponse.getResultOfMethodCall().getClass())
    );
  }

  @When("extracting the object at index {int}")
  public void extractingTheObjectAtIndex(int index) {
    Object list = latestResponse.getResultOfMethodCall();
    if (isListType(list.getClass())) {
      relevantPartOfResponse = ((List<?>) list).get(index);
    } else {
      throw new UnsupportedOperationException(
        "Only List types are supported in this step, not strict array types"
      );
    }
  }

  @And("the response should have a property {word} with value {word}")
  public void response_should_have_property(String propName, String propValue) {
    final Object partOfResponseToInspect;
    if (relevantPartOfResponse == null) {
      partOfResponseToInspect = latestResponse.getResultOfMethodCall();
    } else {
      partOfResponseToInspect = relevantPartOfResponse;
    }
    String actualProp = methodCallHandler.getPropertyOnObject(
      propName,
      partOfResponseToInspect,
      partOfResponseToInspect.getClass().getSimpleName(),
      latestResponse.getClassLoader()
    );
    assertEquals(propValue, actualProp);
  }

  @And("{word} should have an optional property named {word} of type {word}")
  public void should_have_an_optional_property_named(
    String modelObjectName,
    String property,
    String expectedType
  ) throws MalformedURLException, NoSuchFieldException, ClassNotFoundException {
    should_have_a_property_named(modelObjectName, property, expectedType);
    assertFalse(
      methodCallHandler.propertyIsAPrimitive(modelObjectName, property)
    );
  }

  @And("{word} should have a required property named {word} of type {word}")
  public void should_have_a_required_property_named(
    String modelObjectName,
    String property,
    String expectedType
  ) throws MalformedURLException, NoSuchFieldException, ClassNotFoundException {
    should_have_a_property_named(modelObjectName, property, expectedType);
    assertTrue(
      methodCallHandler.propertyIsAPrimitive(modelObjectName, property) ||
      // There is no primitive equivalent of a String, so we add an extra check:
      methodCallHandler
        .getTypeOfClassProperty(modelObjectName, property)
        .equals(String.class)
    );
  }

  private void should_have_a_property_named(
    String modelObjectName,
    String property,
    String expectedType
  ) throws MalformedURLException, ClassNotFoundException, NoSuchFieldException {
    assertTrue(methodCallHandler.doesClassExist(modelObjectName));
    assertTrue(methodCallHandler.classHasProperty(modelObjectName, property));
    Class<?> actualType = methodCallHandler.getTypeOfClassProperty(
      modelObjectName,
      property
    );
    assertEquals(expectedType, javaTypeToGenericType.convert(actualType));
  }

  @When("calling the method {word} with parameters {string}")
  public void calling_the_method_with(String method, String rawParameters) {
    List<String> parameters = Arrays.stream(rawParameters.split(",")).toList();
    latestResponse =
      methodCallHandler.callMethod(
        method,
        parameters,
        null,
        new HashMap<>(),
        latestServerIndex
      );
  }

  @When("calling the method {word} without params")
  public void calling_the_method_without(String method) {
    latestResponse =
      methodCallHandler.callMethod(
        method,
        new ArrayList<>(),
        null,
        new HashMap<>(),
        latestServerIndex
      );
  }

  @When("calling the spied method {word} without params")
  public void calling_the_spied_method_without(String method) {
    latestResponse =
      methodCallHandler.callMethod(
        method,
        new ArrayList<>(),
        null,
        new HashMap<>(),
        latestServerIndex
      );
  }

  @When("calling the method {word} with object {}")
  public void calling_the_method_with_object(
    String method,
    String objectAsString
  ) {
    List<String> parameters = Collections.singletonList(objectAsString);
    latestResponse =
      methodCallHandler.callMethod(
        method,
        parameters,
        null,
        new HashMap<>(),
        latestServerIndex
      );
  }

  @When("calling the method {word} with array {string}")
  public void calling_the_method_with_array(
    String method,
    String arrayAsString
  ) {
    List<String> parameters = Collections.singletonList(arrayAsString);
    latestResponse =
      methodCallHandler.callMethod(
        method,
        parameters,
        null,
        new HashMap<>(),
        latestServerIndex
      );
  }

  @When("calling the method {word} and the server provides an empty response")
  public void calling_the_method_and_the_server_provides_an_empty_response(
    String method
  ) {
    latestResponse =
      methodCallHandler.callMethod(
        method,
        new ArrayList<>(),
        null,
        new HashMap<>(),
        latestServerIndex
      );
  }

  @When("selecting the server at index {int}")
  public void selecting_the_server_at_index(int serverIndex) {
    latestServerIndex = serverIndex;
  }

  @Then("the requested URL should be {word}")
  public void the_requested_url_should_be(String expectedUrl) {
    if (!expectedUrl.contains("?")) {
      // There are no query parameters
      assertEquals(expectedUrl, latestResponse.getUrlRequested());
    } else {
      // Accept query parameters in different orders
      String[] expectedUrlParts = expectedUrl.split("\\?");
      String[] actualUrlParts = latestResponse.getUrlRequested().split("\\?");
      assertEquals(2, actualUrlParts.length);
      assertEquals(expectedUrlParts[0], actualUrlParts[0]);

      String[] expectedQueryParams = expectedUrlParts[1].split("&");
      String[] actualQueryParams = actualUrlParts[1].split("&");
      Arrays.sort(expectedQueryParams);
      Arrays.sort(actualQueryParams);
      assertArrayEquals(expectedQueryParams, actualQueryParams);
    }
  }

  @Then("the request method should be of type {word}")
  public void the_request_method_should_be_of_type(String expectedHttpVerb) {
    assertEquals(
      expectedHttpVerb,
      latestResponse.getRequestMethod().toLowerCase()
    );
  }

  @Then("the request header should have a cookie property with value {word}")
  public void the_request_header_should_have_a_cookie_property_with_value(
    String expectedHeader
  ) {
    assertEquals(
      expectedHeader,
      latestResponse.getRequestHeaders().get("cookie")
    );
  }

  @Then("the request should have a header property with value {word}")
  public void the_request_should_have_a_header_property_with_value(
    String expectedHeader
  ) {
    assertEquals(
      expectedHeader,
      latestResponse.getRequestHeaders().get("test")
    );
  }

  @Then("the response should have a header {word} with value {word}")
  public void the_request_should_have_a_header_property_with_value(
    String headerName,
    String expectedHeader
  ) {
    assertEquals(
      expectedHeader,
      latestResponse.getResponseHeaders().get(headerName)
    );
  }

  @Then("the request should have a body with value {}")
  public void the_request_should_have_a_body_with_value(
    String expectedBodyAsString
  ) {
    assertEquals(expectedBodyAsString, latestResponse.getRequestBodyAsString());
  }

  @Then("the response should be null")
  public void the_response_should_be_null() {
    assertNull(latestResponse.getResultOfMethodCall());
  }

  @Then("the api file with tag {string} exists")
  public void the_api_file_with_tag_exists(String tag) {
    assertTrue(
      methodCallHandler.doesClassExist(
        "ApiClient" + StringUtils.capitalize(tag)
      )
    );
  }

  @Then("the api file with tag {string} does not exist")
  public void the_api_file_with_tag_does_not_exist(String tag) {
    assertFalse(
      methodCallHandler.doesClassExist(
        "ApiClient" + StringUtils.capitalize(tag)
      )
    );
  }

  @And(
    "the method {string} should be present in the api file with tag {string}"
  )
  public void the_method_should_be_present_in_the_api_file_with_tag(
    String method,
    String tag
  ) {
    assertTrue(
      methodCallHandler.classHasMethod(
        "ApiClient" + StringUtils.capitalize(tag),
        method
      )
    );
  }

  @And(
    "the method {string} should not be present in the api file with tag {string}"
  )
  public void the_method_should_not_be_present_in_the_api_file_with_tag(
    String method,
    String tag
  ) {
    String className = "ApiClient" + StringUtils.capitalize(tag);
    assertTrue(methodCallHandler.doesClassExist(className));
    assertFalse(methodCallHandler.classHasMethod(className, method));
  }

  private boolean isListType(Class<?> type) {
    return type.getSimpleName().contains("List");
  }

  private String stripQuotesIfPresent(String quoted) {
    if (
      quoted.length() > 2 &&
      quoted.charAt(0) == '"' &&
      quoted.charAt(quoted.length() - 1) == '"'
    ) {
      return quoted.substring(1, quoted.length() - 1);
    }
    return quoted;
  }

  private Map<String, String> deserialiseHeaders(String headersString) {
    // headerString examples: `{"foo": "bar"}` and `{"foo": "bar", "fizz": "buzz"}`
    String headersSimple = headersString
      .replaceAll("\\s+", "")
      .replaceAll("\\{", "")
      .replaceAll("}", "")
      .replaceAll("\"", "");
    // From https://stackoverflow.com/a/40004122/:
    return Arrays
      .stream(headersSimple.split(","))
      .map(s -> s.split(":"))
      .collect(
        Collectors.toMap(
          a -> a[0], // key
          a -> a[1] // value
        )
      );
  }

  @After
  public void after() {
    latestResponse = null;
    latestServerIndex = 0;
    relevantPartOfResponse = null;
    methodCallHandler = new MethodCallHandler(new TypeConverter());
  }
}
