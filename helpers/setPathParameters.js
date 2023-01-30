const Handlebars = require("handlebars");
const toParamName = require("./toParamName");
const getParametersByType = require("./getParametersByType");

const setPathParameters = (path, sortedParams) => {
  const pathParams = getParametersByType(sortedParams, "path");
  if (pathParams.length === 0) {
    return path;
  }

  return new Handlebars.SafeString(
    path.replace(/{(.*?)}/g, (match, captureGroup) => {
      var pathParam = pathParams.find((p) => p.name === captureGroup);

      if (pathParam === undefined) {
        throw `helper setPathParameters: cannot find PATH parameter named '${captureGroup}' in available path parameters: ${pathParams.map(
          (p) => `'${p.name}'`
        )}`;
      }

      const safeParamName = toParamName(captureGroup);
      switch (pathParam.schema.type) {
        case "array":
          return `{string.Join("%2C", ${safeParamName})}`;
        case "object": {
          let serialisedObject = "";
          for (const [propName] of Object.entries(
            pathParam.schema.properties
          )) {
            serialisedObject += `${propName}%2C{${safeParamName}.${propName}}%2C`;
          }
          return serialisedObject.slice(0, -3);
        }
        default:
          return `{${safeParamName}}`;
      }
    })
  );
};

module.exports = setPathParameters;
