package com.example.springboot;

import io.cucumber.java.en.When;

public class ConfigurationFeature {
  //  private MethodCallHandler methodCallHandler = new MethodCallHandler(new TypeConverter());

  //  @When("calling the method {word} without params")
  //  public void calling_the_method_without(String method) {
  //    methodCallHandler.callMethod(method, new ArrayList<>());
  //  }

  @When("selecting the server at index {int}")
  public void selecting_the_server(int serverIndex) {
    System.err.println(serverIndex);
    throw new io.cucumber.java.PendingException();
  }
}
