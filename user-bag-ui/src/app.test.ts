import request from 'supertest';

import App from './create-app';

beforeEach(() => {
    jest.clearAllMocks();
    jest.resetAllMocks();
});

test('/ endpoint exists and returns 200', async () => {
    const app = await App();
    const response = await request(await app.callback()).get('/');
    expect(response.status).toBe(200);
});