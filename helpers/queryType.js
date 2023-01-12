const queryType = (key) => {
    if (key == "delete") {
        return ".delete()";
    } else if (key == "put") {
        return ".put(httpBody)";
    } else if (key == "post") {
        return ".post()"
    } else {
        return "";
    }
};
  
  module.exports = queryType;
  