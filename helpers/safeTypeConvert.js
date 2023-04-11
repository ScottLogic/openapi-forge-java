const Handlebars = require("handlebars");
const typeConvert = require("./typeConvert");

const safeTypeConvert = (prop, shouldBox = false, inDeclaration = false) => {
  if (typeof shouldBox !== "boolean") {
    shouldBox = false;
  }
  if (typeof inDeclaration !== "boolean") {
    inDeclaration = false;
  }
  return new Handlebars.SafeString(typeConvert(prop, shouldBox, inDeclaration));
};

module.exports = safeTypeConvert;
