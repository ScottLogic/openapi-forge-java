const toSafeName = require("./toClassName");

const fromFormat = (propFormat, shouldBox) => {
  switch (propFormat) {
    case "int32":
      return shouldBox ? "Integer" : "int";
    case "int64":
      return shouldBox ? "Long" : "long";
    case "float":
      return shouldBox ? "Float" : "float";
    case "double":
      return shouldBox ? "Double" : "double";
    case "date":
    case "date-time":
      return "ZonedDateTime";
    case "byte":
    case "binary":
    case "string":
      return "String";
    default:
      return "void";
  }
};

const fromType = (propType, additionalProperties, items, shouldBox) => {
  switch (propType) {
    case "integer":
      return shouldBox ? "Integer" : "int";
    case "number":
      return shouldBox ? "Double" : "double";
    case "boolean":
      return shouldBox ? "Boolean" : "boolean";
    case "string":
      return "String";
    case "array":
      return `List<${typeConvert(items, true)}>`;
    // inline object definition
    case "object":
      if (additionalProperties) {
        return `HashMap<String,${typeConvert(additionalProperties, true)}>`;
      } else {
        return "Object";
      }
    default:
      return shouldBox ? "Void" : "void";
  }
};

const typeConvert = (prop, shouldBox = false) => {
  if (prop === null) return shouldBox ? "Void" : "void";

  if (prop === undefined) return "object";

  // resolve references
  if (prop.$ref) {
    return toSafeName(prop.$ref.split("/").pop());
  }

  const type = prop.format
    ? fromFormat(prop.format, shouldBox)
    : fromType(prop.type, prop.additionalProperties, prop.items, shouldBox);

  return type === "" ? "object" : type;
};

module.exports = typeConvert;
