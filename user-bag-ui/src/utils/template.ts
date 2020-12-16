import getAssets from './get-assets';

export default () => {
    const { cssBundle, jsBundle } = getAssets();

    return `
    <!DOCTYPE html>
    <html id="root-page">
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
        <head>
            <title>CMS UI</title>
            ${cssBundle ? `<link rel="stylesheet" href="${cssBundle}" />` : ''}
        </head>
        <body>
            <div id="wrapper"></div>
            <script src="${jsBundle}"></script>
        </body>
    </html>`;
};