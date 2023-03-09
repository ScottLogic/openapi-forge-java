const move_files = require("./postProcessMoveFiles");

module.exports = (folder, _, options) => {
  move_files(folder, options);
};
