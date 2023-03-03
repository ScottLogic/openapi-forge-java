package com.example.springboot;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CookieParamsFeature {
  @When("calling the method cookieParameters with parameters {string}}")
  public void calling_the_method_with_cookie_parameters(String rawParameters) {
    // TODO: Will this always be "parameters" and "cookieParameters"?
    // C# one:
    // [When(@"calling the method (\w+) with (object|array|parameters) ""(.+)""")]
    // TODO use a custom param type?
    // https://github.com/cucumber/cucumber-expressions#custom-parameter-types
    //    System.err.println(method);
    System.err.println(rawParameters);
    throw new io.cucumber.java.PendingException();
  }

  @Then("the request header should have a cookie property with value {word}")
  public void the_request_header_cookie_property(String rawValue) {
    System.err.println(rawValue);
    throw new io.cucumber.java.PendingException();
  }
}
