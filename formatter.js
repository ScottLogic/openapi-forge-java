var exec = require('child_process').exec;
const platform = require("process").platform;

module.exports = (folder, logLevel) => {
    cmd = "";
    // Use platform information to determine the OS and use platform specific maven wrapper.
    if (platform == "win32") {
        cmd = ".\\mvnw.cmd"
    } else {
        cmd = './mvnw'
    }
    // Use mvn process-sources phase from format goal that comes from external com.spotify.fmt
    cmd += " process-sources"
    exec(cmd, {
        cwd: folder
    },
    function (error, stdout, stderr) {
        switch (logLevel) {
            // Verbose
            case 2:
                console.log('Formatter stdout:\n' + stdout);
                console.log('Formatter stderr:\n' + stderr);
                if (error !== null) {
                    console.log('Formatter exec error:\n' + error);
                }
            // Standard
            case 1:
            // Quiet
            case 0:
        }
    });
};
