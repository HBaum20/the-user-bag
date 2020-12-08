module.exports = Object.freeze({
    host: 'localhost',
    port: process.env.PORT || 8080,
    serverPort: process.env.SERVER_PORT || process.env.PORT || 8081,
    outputFileName: 'bundle.js',
    proxyTarget: process.env.API_ADDRESS || 'http://localhost:8080/users',
    environment: process.env.NODE_ENV || 'development',
    proxyAddress: process.env.PROXY_ADDRESS
})