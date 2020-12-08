const HtmlWebpackPlugin = require('html-webpack-plugin');
const config = require('../../../config');
const chalk = require('chalk');
const path = require('path');
const webpack = require('webpack');

const { host, port, outputFileName, serverPort, environment } = config;

module.exports = {
    context: __dirname,
    entry: ['@babel/polyfill', 'webpack/hot/only-dev-server', './src/index.tsx'],
    output: {
        filename: outputFileName,
        publicPath: `http://${host}:${port}/`
    },
    watch: true,
    devServer: {
        hot: true,
        inline: false,
        host,
        port,
        contentBase: path.join(__dirname, 'public'),
        publicPath: `http://localhost:${port}/`,
        headers: {
            'Access-Control-Allow-Origin': '*'
        },
        proxy: {
            '/users': {
                target: `http://localhost:${serverPort}/users`,
                secure: false,
                changeOrigin: true,
                logLevel: 'debug',
                pathRewrite: {'^/users': ''}
            }
        },
        after: function(app, server, compiler) {
            console.log(chalk.bgGreen(`Client running on port ${port} in ${environment} mode`));
        }
    },
    resolve: {
        alias: {
            src: path.join(__dirname, 'src'),
            pages: path.join(__dirname, 'src/pages'),
            client: path.join(__dirname, 'src/client'),
            blueprintjs: path.join(__dirname, '../../node_modules/@blueprintjs')
        },
        extensions: ['.js', '.jsx', '.ts', '.tsx', '.css']
    },
    module: {
        rules: [
            {
                test: /\.css$/,
                use: [
                    'css-loader',
                    'style-loader'
                ]
            },
            {
                test: /\.(ts)x?$/,
                loader: 'ts-loader',
                options: {
                    // disable type checker - we will use it in fork plugin
                    transpileOnly: true,
                    configFile: 'modules/client/tsconfig.json'
                }
            },
            {
                test: /\.(ts|js)x?$/,
                exclude: /node_modules/,
                loader: 'babel-loader',
                options: {
                    cacheDirectory: true,
                    plugins: ['react-hot-loader/babel']
                }
            }
        ]
    },
    plugins: [
        new webpack.EnvironmentPlugin({
            API_ENDPOINT: `http://localhost:${serverPort}/api`,
            AUTH_AUTHORITY: process.env.AUTH_AUTHORITY || '',
            AUTH_CLIENT_ID: process.env.AUTH_CLIENT_ID || '',
            AUTH_REDIRECT_URI: process.env.AUTH_REDIRECT_URI || '',
            AUTH_LOGOUT_REDIRECT_URI: process.env.AUTH_LOGOUT_REDIRECT_URI || ''
        }),
        new HtmlWebpackPlugin({
            template: path.resolve(__dirname, '../../../public/index.html')
        })
    ]
}