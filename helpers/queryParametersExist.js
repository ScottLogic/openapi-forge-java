const queryParametersExist = (params) => {
  return (
    Array.isArray(params) &&
    params.some((p) => p.in === "query" && p.name !== "body")
  );
};

module.exports = queryParametersExist;
