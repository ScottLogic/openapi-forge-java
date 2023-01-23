// If the params are given to a helper with a unfolded @key, params parameter gets unfolded as well.
// This is why this function exists seperately.

const queryTypeWithBody = (key) => {
    if (key == "delete") {
        return ".delete()";
    } else if (key == "put") {
        return ".put(httpBody)"
    } else if (key == "post") {
        return ".post(httpBody)"
    } else {
        return "";
    }
};

module.exports = queryTypeWithBody;
