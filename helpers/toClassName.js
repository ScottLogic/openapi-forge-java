const toClassName = (name) => {
  name = name.replace(/[^a-z0-9_]/gi, "");
  return name.charAt(0).toUpperCase() + name.substr(1);
};

module.exports = toClassName;
