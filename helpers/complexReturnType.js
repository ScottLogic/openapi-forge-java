const typeConvert = require("./typeConvert");

const complexReturnType = (responseSchema) => { 
    const responseType = typeConvert(responseSchema);
    switch (responseType) {
        case "boolean":
        case "int":
        case "long":
        case "float":
        case "double":
        case "String":
            return false;
        case "Date":
        default:
            return true;
    }
}

module.exports = complexReturnType;