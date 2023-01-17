const bodyParameterExists = (params) => {
  return Array.isArray(params) && params.some((p) => p.in === "body");
};

module.exports = bodyParameterExists;
