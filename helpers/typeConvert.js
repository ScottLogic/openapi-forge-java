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
      return "Date";
    case "byte":
    case "binary":
    case "string":
      return "String";
    default:
      return "void"; // that's a change from c# to java
  }
};

const fromType = (propType, additionalProperties, items) => {
  switch (propType) {
    case "integer":
      return "int";
    case "number":
      return "double";
    case "boolean":
      return "boolean";
    case "string":
      return "String";
    case "array":
      return `List<${typeConvert(items)}>`;
    // inline object definition
    case "object":
      if (additionalProperties) {
        return `HashMap<String,${typeConvert(additionalProperties)}>`;
      } else {
        return "Object";
      }
    default:
      return "void";
  }
};

const typeConvert = (prop) => {
  if (prop === null) return "void";

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
