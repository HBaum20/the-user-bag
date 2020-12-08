import fs from 'fs';
import path from 'path';

const cache = {
    filesResolved: false,
    cssBundle: null,
    jsBundle: 'http://localhost:8080/bundle.js'
};

export default (cacheObj = cache) => {
    if(cacheObj.filesResolved) {
        return cacheObj;
    }

    return cacheObj;
}