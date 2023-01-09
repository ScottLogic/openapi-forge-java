function quoteIfString(prop) {
  // check if prop is a string
  if (typeof prop === "string") {
    return `"${prop}"`;
  }
  return prop;
}

module.exports = quoteIfString;
