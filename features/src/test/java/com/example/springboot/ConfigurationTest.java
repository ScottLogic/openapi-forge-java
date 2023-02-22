package com.example.springboot;

import io.cucumber.java.en.When;

public class ConfigurationTest {
  @When("calling the method {word} without params)")
  public void calling_the_method_without(String method) {
    System.err.println(method);
    throw new io.cucumber.java.PendingException();
  }

  @When("selecting the server at index {int}")
  public void selecting_the_server(int serverIndex) {
    System.err.println(serverIndex);
    throw new io.cucumber.java.PendingException();
  }
}
