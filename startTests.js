const shell = require("shelljs");

// Copy the feature files to where maven can recognise them
// This requires the openapi-forge to have been installed locally:
// |
// | openapi-forge
// | - features/
// | openapi-forge-java
// | - features/
shell.exec("cp ../openapi-forge/features/*.feature features/src/test/resources/com/openapi/forge");

// Go into the test folder
shell.cd("features/");

// Pass over control to maven test
shell.exec("sh ./mvnw test");

// Return to original location
shell.cd("..");

// Clean the resources folder
shell.exec("rm features/src/test/resources/com/openapi/forge/*.feature")