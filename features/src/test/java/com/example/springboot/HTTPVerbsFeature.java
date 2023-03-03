package com.example.springboot;

import io.cucumber.java.en.When;

public class HTTPVerbsFeature {
  @When("calling the spied method {word} without params")
  public void calling_the_spied_method_without_parameters(String rawParameters) {
    //        [FeatureFile(nameof(HTTPVerbs) + Constants.FeatureFileExtension)]

    //        [When(@"calling the(?: spied)? method (\w+) without params")]

    throw new io.cucumber.java.PendingException();
  }
}
