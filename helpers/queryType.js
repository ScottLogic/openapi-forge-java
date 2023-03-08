const queryType = (key) =>
  `.method("${key}", RequestBody.create("", null))`;

module.exports = queryType;
