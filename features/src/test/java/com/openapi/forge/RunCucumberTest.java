package com.openapi.forge;

import static io.cucumber.junit.platform.engine.Constants.FILTER_NAME_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("com/openapi/forge")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.openapi.forge")
// Skip a test scenario where a non-standard behaviour of GET request with requestBody is used.
// See discussion in https://github.com/ScottLogic/openapi-forge/issues/182.
@ConfigurationParameter(
  key = FILTER_NAME_PROPERTY_NAME,
  value = "^(?!Calling API methods with a request body).*$"
)
public class RunCucumberTest {}
