const toParamName = (name) => {
  name = name.replace(/[^a-z0-9_]/gi, "");
  return name.charAt(0).toLowerCase() + name.substr(1);
};

module.exports = toParamName;
