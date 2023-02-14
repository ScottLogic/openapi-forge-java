package com.example.springboot;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Components {
  @When("calling the method {word} and the server responds with")
  public void calling_method_server_responds(String method, String response) {
    System.err.println(method);
    System.err.println(response);
    throw new io.cucumber.java.PendingException();
  }

  @Then("the response should be of type {word}")
  public void response_of_type(String type) {
    System.err.println(type);
    throw new io.cucumber.java.PendingException();
  }

  @And("the response should have a property {word} with value {word}}")
  public void response_should_have_property(String propName, String propValue) {
    System.err.println(propName);
    System.err.println(propValue);
    throw new io.cucumber.java.PendingException();
  }

  @When("calling the method {word} with")
  public void calling_the_method_with(String method, String rawParameters) {
    System.err.println(method);
    System.err.println(rawParameters);
    throw new io.cucumber.java.PendingException();
  }
}
