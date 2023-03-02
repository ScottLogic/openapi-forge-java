const fs = require("fs");
const { parse } = require("java-parser");

// Temporary fix
module.exports = (folder) => {
  model_file = folder + "/ApiModel.java";
  try {
    const data = fs.readFileSync(model_file, "utf8");
    // parse java file
    const cst = parse(data);

    class_prefix = "";

    // extract package statement
    package_declaration =
      cst.children.ordinaryCompilationUnit[0].children.packageDeclaration[0];
    package_start = package_declaration.location.startOffset;
    package_end = package_declaration.location.endOffset;

    class_prefix += data.substring(package_start, package_end + 1) + "\n";

    // extract import statements
    import_declaration =
      cst.children.ordinaryCompilationUnit[0].children.importDeclaration;
    import_start = import_declaration[0].location.startOffset;
    import_end =
      import_declaration[import_declaration.length - 1].location.endOffset;

    class_prefix += data.substring(import_start, import_end + 1) + "\n";

    // process each class individually
    cst.children.ordinaryCompilationUnit[0].children.typeDeclaration.forEach(
      (cls) => {
        class_name =
          cls.children.classDeclaration[0].children.normalClassDeclaration[0]
            .children.typeIdentifier[0].children.Identifier[0].image;
        class_start = cls.location.startOffset;
        class_end = cls.location.endOffset;
        class_txt =
          class_prefix + data.substring(class_start, class_end + 1) + "\n";
        fs.writeFileSync(folder + "/" + class_name + ".java", class_txt);
      }
    );
    fs.rmSync(model_file);
  } catch (err) {
    console.log(err);
  }
};
