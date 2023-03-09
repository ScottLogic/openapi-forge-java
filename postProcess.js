const model_fix = require("./postProcessModel");
const move_files = require("./postProcessMoveFiles");

module.exports = (folder, _, options) => {
  model_fix(folder);
  move_files(folder, options);
};
