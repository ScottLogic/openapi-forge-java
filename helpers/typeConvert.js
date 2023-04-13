const toSafeName = require("./toClassName");

const fromFormat = (propFormat, shouldBox, inFnSignature) => {
  const notNullPrefix  = !shouldBox && inFnSignature ? "@NonNull " : "";
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
      return notNullPrefix  + "LocalDate";
    case "date-time":
      return notNullPrefix  + "ZonedDateTime";
    case "byte":
    case "binary":
    case "string":
      return notNullPrefix  + "String";
    default:
      return shouldBox ? "Void" : "void";
  }
};

const fromType = (
  propType,
  additionalProperties,
  items,
  shouldBox,
  inFnSignature
) => {
  const notNullPrefix  = !shouldBox && inFnSignature ? "@NonNull " : "";
  switch (propType) {
    case "integer":
      return shouldBox ? "Integer" : "int";
    case "number":
      return shouldBox ? "Double" : "double";
    case "boolean":
      return shouldBox ? "Boolean" : "boolean";
    case "string":
      return notNullPrefix  + "String";
    case "array":
      return `List<${typeConvert(items, true)}>`;
    // inline object definition
    case "object":
      if (additionalProperties) {
        return `HashMap<String,${typeConvert(additionalProperties, true)}>`;
      } else {
        return notNullPrefix  + "Object";
      }
    default:
      return shouldBox ? "Void" : "void";
  }
};

const typeConvert = (prop, shouldBox = false, inFnSignature = false) => {
  if (prop === null) return shouldBox ? "Void" : "void";

  if (prop === undefined) return "Object";

  // resolve references
  if (prop.$ref) {
    const notNullPrefix  = !shouldBox && inFnSignature ? "@NonNull " : "";
    return notNullPrefix  + toSafeName(prop.$ref.split("/").pop());
  }

  const type = prop.format
    ? fromFormat(prop.format, shouldBox, inFnSignature)
    : fromType(
        prop.type,
        prop.additionalProperties,
        prop.items,
        shouldBox,
        inFnSignature
      );

  return type === "" ? "object" : type;
};

module.exports = typeConvert;
