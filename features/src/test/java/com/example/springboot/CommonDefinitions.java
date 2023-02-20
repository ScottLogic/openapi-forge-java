package com.example.springboot;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class CommonDefinitions {
  //  private final String javaGeneratorPath = "../";
  private final String tempSchemaPath = "schema.json";
  private final String cucumberTestsDirectory = "features/";

  boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
  }

  String npm = isWindows() ? "npm.cmd" : "npm";

  @Given("an API with the following specification")
  public void an_api_with_spec(String spec) throws IOException {
    generateApi(spec);
  }

  @Then("the requested URL should be {word}")
  public void requested_url(String url) {
    System.err.println(url);
    throw new io.cucumber.java.PendingException();
  }

  private void generateApi(String spec) throws IOException {
    writeToJsonFile(spec);
    forgeApi();
    getApiClientTypes();
  }

  private void writeToJsonFile(String spec) throws IOException {
    List<String> lines = Collections.singletonList(spec);
    Path file = Paths.get(tempSchemaPath);
    Files.write(file, lines, StandardCharsets.UTF_8);
  }

  private void forgeApi() throws IOException {
    Runtime runtime = Runtime.getRuntime();
    String npxCommand;
    if (isWindows()) {
      npxCommand = "npx.cmd";
    } else {
      npxCommand = "npx";
    }
    String[] openApiForgeCommand =
        new String[] {
          npxCommand,
          "openapi-forge",
          "forge",
          tempSchemaPath,
          "..",
          "--output",
          "../features",
          "--exclude",
          "pom.xml",
          // In current version of openapi-forge, can only exclude one item
          //            ".mvn/**",
          //            ".gitignore",
          //            "mvnw*",
          //            "*.md"
        };
    runtime.exec(openApiForgeCommand);
  }

  private void getApiClientTypes() {}
}
