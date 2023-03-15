const Handlebars = require("handlebars");
const toParamName = require("./toParamName");
const getParametersByType = require("./getParametersByType");
const newLine = "\n";
// currently we don't have a working a code formatter, so we need to use this hack
const indent = "            ";

const isStringType = (typeDef) =>
  typeDef.type === "string" &&
  (typeDef.format === undefined || typeDef.format === "string");

const isStringArrayParam = (param) =>
  param.schema.type === "array" &&
  param.schema.items &&
  isStringType(param.schema.items);

const serialiseArrayParam = (param) => {
  const safeParamName = toParamName(param.name);
  const serialisedParam = `String.join("&", ${safeParamName}.stream().map(p -> "${
    param.name
  }=".concat(${
    isStringArrayParam(param)
      ? "java.net.URLEncoder.encode(p, StandardCharsets.UTF_8)"
      : "p"
  } )).collect(Collectors.toList()))`;

  return `${indent}if (${safeParamName} != null && ${safeParamName}.size() > 0)
${indent}{
    ${prefixSerialisedQueryParam(serialisedParam)}
${indent}}`;
};

const serialiseObjectParam = (param) => {
  const safeParamName = toParamName(param.name);
  let serialisedObject = "";
  for (const [propName, objProp] of Object.entries(param.schema.properties)) {
    let serialisedParam = isStringType(objProp)
      ? `${safeParamName}.${propName} == null ? "" : "${propName}=" + java.net.URLEncoder.encode(${safeParamName}.${propName}, StandardCharsets.UTF_8)`
      : `${propName}={${safeParamName}.${propName}}`;

    serialisedObject += serialisedParam + "&";
  }

  return `${indent}if (${safeParamName} != null)
${indent}{
    ${prefixSerialisedQueryParam(serialisedObject.slice(0, -1))}
${indent}}`;
};

const serialisePrimitive = (param) => {
  const safeParamName = toParamName(param.name);
  const escaped = isStringType(param.schema)
    ? `java.net.URLEncoder.encode(${safeParamName}, StandardCharsets.UTF_8)`
    : "String.valueOf(" + safeParamName + ")";

  const serialisedParam = prefixSerialisedQueryParam(
    `"${param.name}=".concat(${escaped})`
  );
  return serialisedParam;
};

const prefixSerialisedQueryParam = (serialisedQueryParam) =>
  `${indent}queryString.append((queryString.length() == 0 ? "?" : "&").concat(${serialisedQueryParam}));`;

const createQueryStringSnippet = (params) => {
  const queryParams = getParametersByType(params, "query");

  if (queryParams.length === 0) {
    return "";
  }

  let queryStringSnippet = `var queryString = new StringBuilder();`;

  for (const queryParam of queryParams) {
    let serialisedQueryParam;
    switch (queryParam.schema.type) {
      case "array":
        serialisedQueryParam = serialiseArrayParam(queryParam);
        break;
      case "object":
        serialisedQueryParam = serialiseObjectParam(queryParam);
        break;
      default:
        serialisedQueryParam = serialisePrimitive(queryParam);
        break;
    }

    queryStringSnippet += newLine + serialisedQueryParam;
  }

  return new Handlebars.SafeString(queryStringSnippet);
};

module.exports = createQueryStringSnippet;
