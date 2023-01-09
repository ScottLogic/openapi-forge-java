const Handlebars = require("handlebars");
const typeConvert = require("./typeConvert");
const hasDefault = require("./hasDefault");

const nullableTypeConvert = (schema, optional) => {
  const schemaType = typeConvert(schema);
  if (
    !optional ||
    schema._required !== undefined ||
    hasDefault(schema.default)
  ) {
    return new Handlebars.SafeString(schemaType);
  }
  return ["bool", "datetime", "float", "double", "int", "long"].some(
    (s) => s === schemaType.toLowerCase()
  )
    ? `${schemaType}?`
    : schemaType;
};

module.exports = nullableTypeConvert;
