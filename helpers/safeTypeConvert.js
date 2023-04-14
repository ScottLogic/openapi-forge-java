const Handlebars = require("handlebars");
const typeConvert = require("./typeConvert");

const safeTypeConvert = (prop, shouldBox = false, inFnSignature = false) => {
  if (typeof shouldBox !== "boolean") {
    shouldBox = false;
  }
  if (typeof inFnSignature !== "boolean") {
    inFnSignature = false;
  }
  return new Handlebars.SafeString(typeConvert(prop, shouldBox, inFnSignature));
};

module.exports = safeTypeConvert;
