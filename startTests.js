const shell = require("shelljs");
const fs = require("fs");

// Copy the feature files to where maven can recognise them
// This requires the openapi-forge to have been installed locally:
// |
// | openapi-forge
// | - features/
// | openapi-forge-java
// | - features/
if (fs.existsSync("../openapi-forge/features/")) {
  shell.cp(
    "../openapi-forge/features/*.feature",
    "features/src/test/resources/com/openapi/forge"
  );

  // Go into the test folder
  shell.cd("features/");

  // Pass over control to maven test
  shell.exec(process.platform === "win32" ? ".\\mvnw.cmd test" : "./mvnw test");

  // Return to original location
  shell.cd("..");

  // Clean the resources folder
  shell.rm("features/src/test/resources/com/openapi/forge/*.feature");
} else {
  shell.echo(
    "Expecting .feature files to be found at ../openapi-forge/features/ (relative from here)."
  );
  shell.echo(
    "Have you installed the openapi forge project in the correct location?"
  );
}
