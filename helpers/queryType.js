const queryType = (httpVerb) => `.method("${httpVerb.toUpperCase()}", null)`;

module.exports = queryType;
