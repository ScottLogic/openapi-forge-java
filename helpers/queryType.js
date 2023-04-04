const queryType = (httpVerb) => {
  // For GET and HEAD requests return `null` body since okttp doesn't like empty requests.
  let body = "null";
  if (httpVerb != "get" && httpVerb != "head") {
    // For other requests such as PUT or POST if the body is empty `null` is not a valid body.
    body = `RequestBody.create("", null)`;
  }
  return `.method("${httpVerb.toUpperCase()}", ${body})`;
};

module.exports = queryType;
