const quoteIfString = (prop) => (typeof prop === "string" ? `"${prop}"` : prop);

module.exports = quoteIfString;
