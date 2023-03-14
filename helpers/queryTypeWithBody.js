// If the params are given to a helper with a unfolded @key, params parameter gets unfolded as well.
// This is why this function exists seperately.

const queryTypeWithBody = (httpVerb) => `.method("${httpVerb}", httpBody)`;

module.exports = queryTypeWithBody;
