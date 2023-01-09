const toSafeName = require("./toClassName");

const fromFormat = (propFormat) => {
  switch (propFormat) {
    case "int32":
      return "int";
    case "int64":
      return "long";
    case "float":
      return "float";
    case "double":
      return "double";
    case "date":
    case "date-time":
      return "DateTime";
    case "byte":
    case "binary":
    case "string":
      return "string";
    default:
      return "";
  }
};

const fromType = (propType, additionalProperties, items) => {
  switch (propType) {
    case "integer":
      return "int";
    case "number":
      return "double";
    case "boolean":
      return "bool";
    case "string":
      return "string";
    case "array":
      return `${typeConvert(items)}[]`;
    // inline object definition
    case "object":
      if (additionalProperties) {
        return `Dictionary<string,${typeConvert(additionalProperties)}>`;
      } else {
        return "object";
      }
    default:
      return "";
  }
};

const typeConvert = (prop) => {
  if (prop === undefined) return "object";

  // resolve references
  if (prop.$ref) {
    return toSafeName(prop.$ref.split("/").pop());
  }

  const type = prop.format
    ? fromFormat(prop.format)
    : fromType(prop.type, prop.additionalProperties, prop.items);

  return type === "" ? "object" : type;
};

module.exports = typeConvert;
