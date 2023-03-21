package com.openapi.forge;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import org.apache.tomcat.util.http.fileupload.FileUtils;

public class SetUpTearDown {
  private final String tempSchemaPath = "schema.json";

  boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
  }

  @Given("an API with the following specification")
  public void an_api_with_spec(String spec) throws IOException, InterruptedException {
    generateApi(spec);
  }

  @When("generating an API from the following specification")
  public void generating_an_api_from_the_following_specification(String spec)
      throws IOException, InterruptedException {
    generateApi(spec);
  }

  private void generateApi(String spec) throws IOException, InterruptedException {
    writeToJsonFile(spec);
    forgeApi();
  }

  private void writeToJsonFile(String spec) throws IOException {
    List<String> lines = Collections.singletonList(spec);
    Path file = Paths.get(tempSchemaPath);
    Files.write(file, lines, StandardCharsets.UTF_8);
  }

  private void forgeApi() throws IOException, InterruptedException {
    Runtime runtime = Runtime.getRuntime();
    String npxCommand;
    if (isWindows()) {
      npxCommand = "npx.cmd";
    } else {
      npxCommand = "npx";
    }
    String[] openApiForgeCommand =
        new String[] {
          npxCommand, // TODO: Add as peer dependency npm?
          "openapi-forge",
          //          "node", // Only needed when running openapi-forge from relative path
          //          "../../openapi-forge/src/index.js",
          "forge",
          tempSchemaPath,
          "..",
          "--output",
          ".",
          "--exclude",
          "pom.xml",
          ".mvn/**",
          ".gitignore",
          "mvnw*",
          "*.md"
        };
    // TODO: Can we get the names of all files generated to be returned with the exit code?
    Process process = runtime.exec(openApiForgeCommand);
    process.waitFor();
  }

  // Although theoretically we could switch @After for @Before here for debugging purposes, it can
  // cause failures.
  // There seems to be a difference between files on the classpath at compile time and files
  // dynamically added to
  // the classpath. If there are any files in the main package at the test of `mvn test`, these will
  // not be purged fully by this teardown. There may be a fix for this, but it doesn't seem worth it
  // unless further problems show down the line...
  @After
  public void tearDownGeneratedFiles() {
    deleteDirectory("src/main/java/");
    deleteDirectory("target/");
    deleteFile(tempSchemaPath);
  }

  private void deleteDirectory(String pathRelativeToPom) {
    File directory = new File(pathRelativeToPom);
    try {
      FileUtils.deleteDirectory(directory);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void deleteFile(String pathRelativeToPom) {
    File fileOrDirectory = new File(pathRelativeToPom);
    if (!fileOrDirectory.delete()) {
      System.err.println("Failed to delete: " + pathRelativeToPom);
    }
  }
}
