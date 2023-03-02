const shell = require("shelljs");
const model_fix = require("./postProcessModel");

module.exports = (folder, _, options) => {
  model_fix(folder);
  var package_name = options["generator.package"];
  var package_folder = package_name.replaceAll(".", "/");
  java_path = "src/main/java/" + package_folder;
  shell.cd(folder);
  shell.mkdir("-p", java_path);
  shell.mv("-f", "*.java", java_path);
};
