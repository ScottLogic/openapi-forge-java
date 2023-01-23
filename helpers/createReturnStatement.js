const Handlebars = require("handlebars");
const typeConvert = require("./typeConvert");
const toParamName = require("./toParamName");

const mapperListHandle = (responseType) => {
  if (responseType.includes(`<`)) {
    return `new TypeReference<${responseType}>(){}`;
  } else {
    return `${responseType}.class`;
  }
}

const createReturnStatement = (responseSchema) => {
  const responseType = typeConvert(responseSchema);
  let mapperStatements = "";
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
      mapperStatements = `ObjectMapper deserMapper = new ObjectMapper();\n`;
      mapperStatements += `${responseType} ${toParamName(responseType)} = deserMapper.readValue(responseBodyString, ${mapperListHandle(responseType)});\n`
      returnStatement = toParamName(responseType);
    // returnStatement = `JsonSerializer.Deserialize<${responseType}>(responseBody)`;
  }

  return new Handlebars.SafeString(mapperStatements + `return ${returnStatement};`);
};

module.exports = createReturnStatement;
