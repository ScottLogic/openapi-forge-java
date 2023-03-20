const Handlebars = require("handlebars");
const typeConvert = require("./typeConvert");

const safeTypeConvert = (prop, shouldBox = false) =>{
  if (typeof shouldBox !== "boolean") {
    shouldBox = false;
  }
  return new Handlebars.SafeString(typeConvert(prop, shouldBox));
}

module.exports = safeTypeConvert;
