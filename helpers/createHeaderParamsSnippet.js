const Handlebars = require("handlebars");
const toParamName = require("./toParamName");
const getParametersByType = require("./getParametersByType");

const createHeaderParamsSnippet = (sortedParams) => {
  let headerSnippet = "";

  //Add cookie parameters
  let cookieParams = getParametersByType(sortedParams, "cookie");
  if (cookieParams.length !== 0) {
    let safeParamName = toParamName(cookieParams[0].name);
    headerSnippet += `request.Headers.Add("cookie", $"${cookieParams[0].name}={${safeParamName}}`;
    cookieParams = cookieParams.slice(1);
    for (const cookieParam of cookieParams) {
      safeParamName = toParamName(cookieParam.name);
      headerSnippet += `;${cookieParam.name}={${safeParamName}}`;
    }
    headerSnippet += '");\n';
  }

  const headerParams = getParametersByType(sortedParams, "header");
  if (headerParams.length === 0) {
    return new Handlebars.SafeString(headerSnippet);
  }

  for (const headerParam of headerParams) {
    // only supports default serialization: style: simple & explode: false
    if (headerParam.content) {
      continue;
    }
    const safeParamName = toParamName(headerParam.name);
    switch (headerParam.schema.type) {
      case "array":
        headerSnippet +=
          `request.Headers.Add("${headerParam.name}", string.Join(",", ${safeParamName}))` +
          ";\n";
        break;
      case "object": {
        let serialisedObject = "";
        for (const [propName] of Object.entries(
          headerParam.schema.properties
        )) {
          serialisedObject += `${propName},${safeParamName}.${propName}`;
        }
        headerSnippet +=
          `request.Headers.Add("${headerParam.name}", ${serialisedObject})` +
          ";\n";
        break;
      }
      default:
        headerSnippet +=
          `request.Headers.Add("${headerParam.name}", ${safeParamName})` +
          ";\n";
    }
  }

  return new Handlebars.SafeString(headerSnippet);
};

module.exports = createHeaderParamsSnippet;
