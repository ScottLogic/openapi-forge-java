const indent = (text) => {
  // add two slashes before each newline and the start of the string
  text = text.replace(/^/gm, "// ");
  return text;
};

module.exports = indent;
