import log from 'json-log';
import config from '../config';
import createApp from './create-app';

const { environment, serverPort } = config;

if(!environment) {
    process.exit();
}

export default createApp().then(app => {
    app.listen(serverPort);
    log.info(`Server listening on port ${serverPort} in ${environment} mode`);

    return app;
})