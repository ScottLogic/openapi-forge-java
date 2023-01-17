// If the params are given to a helper with a unfolded @key, params parameter gets unfolded as well.
// This is why this function exists seperately.

const queryTypeWithBody = (key) => {
    if (key == "delete") {
        return ".delete()";
    } else if (key == "put") {
        return ".put(httpbody)"
    } else if (key == "post") {
        return ".post(httpbody)"
    } else {
        return "";
    }
};

module.exports = queryTypeWithBody;
