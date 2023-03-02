const typeConvert = require("./typeConvert");

const complexReturnType = (responseSchema) => {
  const responseType = typeConvert(responseSchema);
  switch (responseType) {
    case "bool":
    case "int":
    case "long":
    case "float":
    case "double":
    case "string":
      return false;
    case "Date":
    default:
      return true;
  }
};

module.exports = complexReturnType;
