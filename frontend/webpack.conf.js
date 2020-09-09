/**
 * для компиляции выполнить команду
 * webpack --config webpack.conf.js
 * версия webpack 4.5.0
 * версия webpack-cli 2.0.14
 * @author Ivan Marshalkin
 * @date 2018-04-09
 */

module.exports = {
    // devtool: 'source-map',                    // https://webpack.js.org/configuration/devtool/

    entry: {
        'unidatabootstrap.js': [
            './appmicro/iziToast.js',
            './appmicro/Microloader.js',
            './appmicro/Micrologin.js',
            './appmicro/unidatabootstrap.js'
        ]
    },

    output: {
        path: __dirname,
        filename: '[name]'
    },

    mode: 'production'                          // development || production
};

