var exec = require('child_process').exec;

module.exports = (folder, logLevel) => {
    // Use mvn process-sources phase from format goal which comes from external com.spotify.fmt
    exec('./mvnw process-sources', {
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
