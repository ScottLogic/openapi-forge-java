const toSafeName = require("./toClassName");

const fromFormat = (propFormat, shouldBox) => {
  switch (propFormat) {
    case "int32":
      if (shouldBox) {
        return "Integer";
      } else {
        return "int";
      }
    case "int64":
      if (shouldBox) {
        return "Long";
      } else {
        return "long";
      }
    case "float":
      if (shouldBox) {
        return "Float";
      } else {
        return "float";
      }
    case "double":
      if (shouldBox) {
        return "Double";
      } else {
        return "double";
      }
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

const fromType = (propType, additionalProperties, items, shouldBox) => {
  switch (propType) {
    case "integer":
      if (shouldBox) {
        return "Integer";
      } else {
        return "int";
      }
    case "number":
      if (shouldBox) {
        return "Double";
      } else {
        return "double";
      }
    case "boolean":
      if (shouldBox) {
        return "Boolean";
      } else {
        return "boolean";
      }
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
      return "void";
  }
};

const typeConvert = (prop, shouldBox = false) => {
  if (prop === null) return "void";

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
