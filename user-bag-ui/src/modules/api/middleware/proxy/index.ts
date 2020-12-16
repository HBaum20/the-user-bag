import proxy from 'koa-server-http-proxy';

import { proxyTarget } from '../../../../../config';

export default proxy('/', {
    target: proxyTarget,
    changeOrigin: true,
    secure: false,
    onProxyRes: proxyRes => {
        proxyRes.headers['status'] = [proxyRes.statusCode]
    }
});