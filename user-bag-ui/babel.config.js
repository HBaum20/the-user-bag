module.exports = {
    presets: ['@babel/env', '@babel/react', '@babel/typescript'],
    plugins: [
        'react-hot-loader/babel',
        '@babel/proposal-class-properties',
        '@babel/proposal-object-rest-spread',
        [
            '@babel/plugin-transform-runtime',
            {
                regenerator: true
            }
        ]
    ]
};
