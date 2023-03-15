const shell = require("shelljs");

// To match the java package/folder conventions, the java files that are generated from handlebars templates 
// should be moved to src/main/java/<package_folder>. 
// The package folder name comes from the generator options and that can given as a command line argument
// by --generator.package

module.exports = (folder, options) => {
  var package_name = options["generator.package"];
  var package_folder = package_name.replaceAll(".", "/");
  java_path = "src/main/java/" + package_folder;
  shell.cd(folder);
  shell.mkdir("-p", java_path);
  shell.mv("-f", "*.java", java_path);
};
