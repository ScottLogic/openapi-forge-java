Here is general flow of the tests:

1. `npm run test:generators` is called by the main project, which triggers `npm test`.
1. This runs `node startTests.js` which copies the feature files from the locally installed openapi-forge project so they are ready to be run.
1. Control is passed to Maven (using `./mvnw test`) which runs the Cucumber tests.
1. In each of the tests/scenarios:
   1. The schema snippet is extracted and put into a temporary JSON file.
   1. Through Java's `Runtime.exec` CLI runner, the forge command on the openapi-forge project is run with the given schema.
   1. The command dumps the generated files into the "src/main/java" part of the same package that the tests are in.
   1. The Java Cucumber code recompiles all the files in the package and adds them to the classpath.
   1. The core part of the test is run. Reflection is used to call the relevant methods in the tests.
   1. The Java Cucumber code cleans up the generated files from the package.
   1. The test results are shown on the console.
