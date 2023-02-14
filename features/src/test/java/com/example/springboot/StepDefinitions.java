package com.example.springboot;

import io.cucumber.java.en.Given;

public class StepDefinitions {
  @Given("an API with the following specification")
  public void an_api_with_spec(String spec) {
    System.err.println(spec);
  }
}
