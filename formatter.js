var exec = require('child_process').exec;

module.exports = (folder, logLevel) => {
    exec('./mvnw process-sources', {
        cwd: folder
    },
        function (error, stdout, stderr) {
            switch (logLevel) {
                case 2:
                    console.log('Formatter stdout:\n' + stdout);
                    console.log('Formatter stderr:\n' + stderr);
                    if (error !== null) {
                        console.log('Formatter exec error:\n' + error);
                    }
                case 1:
                case 0:
            }
        });
};
