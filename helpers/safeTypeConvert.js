const Handlebars = require("handlebars");
const typeConvert = require("./typeConvert");

const safeTypeConvert = (prop, shouldBox = false) =>
  new Handlebars.SafeString(typeConvert(prop, shouldBox));

module.exports = safeTypeConvert;
