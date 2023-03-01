const fs = require("fs");

// Temporary hack
module.exports = (folder) => {
  model_file = folder + "/ApiModel.java";
  try {
    const data = fs.readFileSync(model_file, "utf8");
    var s = data.split("class");
    for (let i = 1; i < s.length; i++) {
      java_file_name = s[i].trim().split(" ")[0] + ".java";
      java_str = s[0] + "public class" + s[i];
      try {
        fs.writeFileSync(folder + "/" + java_file_name, java_str);
      } catch (err) {
        console.log(err);
      }
    }
    try {
      fs.rmSync(model_file);
    } catch (err) {
      console.log(err);
    }
  } catch (err) {
    console.log(err);
  }
};
