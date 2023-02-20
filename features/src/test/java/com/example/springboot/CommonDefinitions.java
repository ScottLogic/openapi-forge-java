package com.example.springboot;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.io.File;
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
    Runtime rt = Runtime.getRuntime();
    // NOTE: Relative to where the tests are running, how can we get the path to the java generator
    // to pass to the openapi-forge command?
    File javaGeneratorDirectory = new File("../"); // how to get the path to the generator???
    // Default is relative to System.getProperty("user.dir") which should be the generator path
    String[] openApiForgeCommand =
        new String[] {
          "npx.cmd", // TWO OPTIONS 1) find path to npm on machine, 2) install assume
          // openapi-forge is installed locally in certain dir
          "openapi-forge",
          //          "sh",
          //          "../../openapi-forge/src/index.js",
          "forge",
          //          cucumberTestsDirectory + tempSchemaPath,
          tempSchemaPath,
          //          (new File("./").toURI()).relativize(new File("../").toURI()).toString(),
          "..",
          //          javaGeneratorDirectory.getCanonicalPath(), // NEEDS TO BE RELATIVE PATH
          // HERE!!!!!! BUT HOW???
          //          javaGeneratorDirectory.getPath(),
          "--output",
          "temp",
          //          cucumberTestsDirectory,
          "--exclude",
          "pom.xml",
          // In current version of openapi-forge, can only exclude one item
          //            ".mvn/**",
          //            ".gitignore",
          //            "mvnw*",
          //            "*.md"
        };
    //    System.out.println("Working Directory = " + System.getProperty("user.dir"));
    //    System.out.println("Java Generator Directory = " +
    // javaGeneratorDirectory.getAbsolutePath());
    System.out.println("Java Generator Directory = " + javaGeneratorDirectory.getCanonicalPath());
    System.out.println("Java Generator Directory = " + javaGeneratorDirectory.getPath());
    //    System.out.println("Java Generator Directory = " + javaGeneratorDirectory.getPath());
    System.out.println(String.join(" ", openApiForgeCommand));
    //    Process pr = rt.exec(openApiForgeCommand, new String[] {}, javaGeneratorDirectory);
    Process pr = rt.exec(openApiForgeCommand);
    //    String[] npmCommand = new String[] {"npm.cmd", "-g", "list"};
    //    String[] npmCommand = new String[] {"npx.cmd"};
    //    Process pr = rt.exec(npmCommand);
    // -o temp -e pom.xml .mvn/** .gitignore mvnw* *.md
    //    openapi-forge forge features/schema.json . -o features/ -l verbose
    //    System.err.println(pr.getOutputStream().toString());
    //    Process process = new ProcessBuilder(npm, "update")
    //            .directory(navigatePath)
    //            .start();
    //    Two options with pom.xml overwrite
    //    1) Make pom.xml into pom.xml.handlebars. Only copy the test parts of the pom if needed.
    //            The template pom will overwrite the test pom for every schema.
    //    2) Add file/folder exclude to template in the main forge project. Check with Colin that
    // this is ok to add as a feature!
  }

  private void getApiClientTypes() {}
}
