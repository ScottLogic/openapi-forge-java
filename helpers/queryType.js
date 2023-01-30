const queryType = (key) => {
    if (key == 'delete') {
        return '.delete()';
    } else if (key == "put") {
        return '.put(RequestBody.create("", null))'
    } else if (key == "post") {
        return '.post(RequestBody.create("", null))'
    } else {
        return '';
    }
};

module.exports = queryType;
