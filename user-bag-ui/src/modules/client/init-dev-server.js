const { spawn } = require('child_process');
const chalk = require('chalk');

let hotReloadDevServer;
let hotReloadServer;

try {
    hotReloadDevServer = spawn('npm', ['run', 'start:hotreload'], {
        stdio: 'inherit'
    });
} catch (err) {
    console.log(chalk.red('Could not start the hot reload server'));
    console.error(err.stack);
    process.exit(-1);
}

try {
    hotReloadServer = spawn('npm', ['run', 'start:serverreload'], {
        stdio: 'inherit'
    });
} catch (err) {
    console.log(chalk.red('Could not start the server'));
    console.error(err.stack);
    process.exit(-1);
}

const cleanUp = () => {
    hotReloadDevServer.kill('SIGKILL');
    hotReloadServer.kill('SIGKILL');
    process.exit(0);
};

process.on('SIGINT', cleanUp);
process.on('SIGTERM', cleanUp);
