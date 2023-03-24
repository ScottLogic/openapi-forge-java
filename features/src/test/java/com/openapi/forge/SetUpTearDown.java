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

  @Given("an API with the following specification")
  public void an_api_with_spec(String spec) throws IOException {
    generateApi(spec);
  }

  @When("generating an API from the following specification")
  public void generating_an_api_from_the_following_specification(String spec)
    throws IOException {
    generateApi(spec);
  }

  private void generateApi(String spec) throws IOException {
    writeToJsonFile(spec);
    forgeApi();
  }

  private void writeToJsonFile(String spec) throws IOException {
    List<String> lines = Collections.singletonList(spec);
    Path file = Paths.get(tempSchemaPath);
    Files.write(file, lines, StandardCharsets.UTF_8);
  }

  private void forgeApi() {
    Runtime runtime = Runtime.getRuntime();
    String[] openApiForgeCommand = new String[] {
      getNpxCommand(),
      "--yes", // Accept npx install prompt if present
      "openapi-forge@latest",
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
      "*.md",
    };
    try {
      Process process = runtime.exec(openApiForgeCommand);
      process.waitFor();
    } catch (IOException e) {
      tearDownGeneratedFiles();
      throw new RuntimeException(
        "Command failed:\t\r\n" +
        String.join(" ", openApiForgeCommand) +
        "\t\r\n" +
        e.getCause()
      );
    } catch (InterruptedException e) {
      tearDownGeneratedFiles();
      throw new RuntimeException(e);
    }
  }

  // To isolate tests from each other we clean generated files after every test.
  // In particular, we want to protect against issues with compilation.
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

  private String getNpxCommand() {
    // We can't run openapi-forge on Windows because runtime.exec does not respect the PATHEXT
    // variable on Windows:
    // https://stackoverflow.com/questions/40503074/how-to-run-npm-command-in-java-using-process-builder
    // NPX is used to avoid this problem and so that the openapi-forge package does not need to be
    // installed prior to running the tests:
    // https://www.npmjs.com/package/npx
    String npxCommand;
    if (isWindows()) {
      npxCommand = "npx.cmd";
    } else {
      npxCommand = "npx";
    }
    return npxCommand;
  }

  private boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
  }
}
