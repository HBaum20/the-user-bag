import Koa from 'koa';
import Router from 'koa-router';
import mount from 'koa-mount';
import bodyParser from 'koa-bodyparser';
import staticFiles from 'koa-static';
import path from 'path';

import template from '../../utils/template';

export default async () => {
    const app = new Koa();
    const router = new Router();

    app.use(mount('/', staticFiles(path.join(__dirname))));

    router.get('*', ctx => {
        ctx.body = template();
    });

    app.use(bodyParser({}));
    app.use(router.routes());
    app.use(router.allowedMethods());

    return app;
}