const toSafeName = require("./toClassName");

const fromFormat = (propFormat, shouldBox, inDeclaration) => {
  let required_object_prefix = !shouldBox && inDeclaration ? "@NonNull " : "";
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
      return required_object_prefix + "LocalDate";
    case "date-time":
      return required_object_prefix + "ZonedDateTime";
    case "byte":
    case "binary":
    case "string":
      return required_object_prefix + "String";
    default:
      return shouldBox ? "Void" : "void";
  }
};

const fromType = (
  propType,
  additionalProperties,
  items,
  shouldBox,
  inDeclaration
) => {
  let required_object_prefix = !shouldBox && inDeclaration ? "@NonNull " : "";
  switch (propType) {
    case "integer":
      return shouldBox ? "Integer" : "int";
    case "number":
      return shouldBox ? "Double" : "double";
    case "boolean":
      return shouldBox ? "Boolean" : "boolean";
    case "string":
      return required_object_prefix + "String";
    case "array":
      return `List<${typeConvert(items, true)}>`;
    // inline object definition
    case "object":
      if (additionalProperties) {
        return `HashMap<String,${typeConvert(additionalProperties, true)}>`;
      } else {
        return required_object_prefix + "Object";
      }
    default:
      return shouldBox ? "Void" : "void";
  }
};

const typeConvert = (prop, shouldBox = false, inDeclaration = false) => {
  if (prop === null) return shouldBox ? "Void" : "void";

  if (prop === undefined) return "Object";

  // resolve references
  if (prop.$ref) {
    let required_object_prefix = !shouldBox && inDeclaration ? "@NonNull " : "";
    return required_object_prefix + toSafeName(prop.$ref.split("/").pop());
  }

  const type = prop.format
    ? fromFormat(prop.format, shouldBox, inDeclaration)
    : fromType(
        prop.type,
        prop.additionalProperties,
        prop.items,
        shouldBox,
        inDeclaration
      );

  return type === "" ? "object" : type;
};

module.exports = typeConvert;
