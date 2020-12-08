import Koa from 'koa';
import mount from 'koa-mount';

import api from './modules/api';
import client from './modules/client';

export default async () => {
    const app = new Koa();

    app.use(mount('/users', await api()));
    app.use(mount('/', await client()));

    return app;
}