const queryType = (httpVerb) =>
  `.method("${httpVerb}", RequestBody.create("", null))`;

module.exports = queryType;
