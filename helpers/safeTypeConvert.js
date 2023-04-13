const Handlebars = require("handlebars");
const typeConvert = require("./typeConvert");

// If safeTypeConvert is called without the optional arguments, these arguments defaults to false
// For inFnSignature, this variant should be only called in function definitions to provide necessary
//  necessary annotations for the public api.
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
