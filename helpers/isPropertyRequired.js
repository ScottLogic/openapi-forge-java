const isPropertyRequired = (propertyKey, requiredList = []) =>
  requiredList.includes(propertyKey);

module.exports = isPropertyRequired;
