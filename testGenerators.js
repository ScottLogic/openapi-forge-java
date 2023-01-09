// the Xunit.Gherkin.Quick testing framework doesn't support the cucumber message format. This
// file is basically a massive hack - we parse the test output and create the subset of messages
// the generator needs for automation
const shell = require("shelljs");

function secondsSinceEpoch() {
  return Math.round(Date.now() / 1000);
}

const start = secondsSinceEpoch();
const result = shell.exec(`npm test`, {}).stdout.split("\n");
const end = secondsSinceEpoch();

const resultMatch = result[result.length - 2].match(
  /Failed:\s+(\d+),\sPassed:\s+(\d+),\sSkipped:\s+(\d+),\sTotal:\s+(\d+),\sDuration:\s+(.*)\s/
);

if (resultMatch) {
  const scenarios = parseInt(resultMatch[4]);
  const failed = parseInt(resultMatch[1]);

  console.log(
    JSON.stringify({ testRunStarted: { timestamp: { seconds: start } } })
  );
  for (let i = 0; i < scenarios; i++) {
    console.log(JSON.stringify({ testCaseStarted: {} }));
    if (i < failed) {
      console.log(
        JSON.stringify({
          testStepFinished: { testStepResult: { status: "FAILED" } },
        })
      );
    }
    console.log(JSON.stringify({ testCaseFinished: {} }));
  }
  console.log(
    JSON.stringify({ testRunFinished: { timestamp: { seconds: end } } })
  );
} else {
  console.error(`Could not parse the results of the Java testing.`);
}
