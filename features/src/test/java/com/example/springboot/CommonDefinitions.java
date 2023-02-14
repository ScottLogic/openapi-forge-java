package com.example.springboot;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class CommonDefinitions {
  @Given("an API with the following specification")
  public void an_api_with_spec(String spec) {
    System.err.println("Hello! It's a me, Mario!");
    System.err.println(spec);
    throw new io.cucumber.java.PendingException();
  }

  @Then("the requested URL should be {word}")
  public void requested_url(String url) {
    System.err.println(url);
    throw new io.cucumber.java.PendingException();
  }
}
