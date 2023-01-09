const Handlebars = require("handlebars");
const typeConvert = require("./typeConvert");

const createReturnStatement = (responseSchema) => {
  const responseType = typeConvert(responseSchema);
  let returnStatement;
  switch (responseType) {
    case "bool":
    case "int":
    case "long":
    case "float":
    case "double":
    case "DateTime":
      returnStatement = `${responseType}.Parse(responseBody)`;
      break;
    case "string":
      returnStatement = "responseBody";
      break;
    default:
      returnStatement = `JsonSerializer.Deserialize<${responseType}>(responseBody)`;
  }

  return new Handlebars.SafeString(`return ${returnStatement};`);
};

module.exports = createReturnStatement;
