const Handlebars = require("handlebars");
const typeConvert = require("./typeConvert");

const safeTypeConvert = (prop) => new Handlebars.SafeString(typeConvert(prop));

module.exports = safeTypeConvert;
