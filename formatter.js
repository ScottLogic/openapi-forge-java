const cli = require("prettier/cli.js");

const logLevels = [
    /* quiet */
    "silent",
    /* standard */
    "warn",
    /* verbose */
    "debug",
  ];
  
  module.exports = (folder, logLevel) => {
    return cli.run(["--write", folder, "--loglevel", logLevels[logLevel]]);
  };
