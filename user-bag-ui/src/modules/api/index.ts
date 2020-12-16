import Koa from 'koa';
import proxyMiddleware from './middleware/proxy';

export default () => {
    const app = new Koa();
    app.use(proxyMiddleware);
    return app;
}