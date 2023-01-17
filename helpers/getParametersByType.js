const getParametersByType = (params, type) => {
  return Array.isArray(params) ? params.filter((p) => p.in === type) : [];
};

module.exports = getParametersByType;
