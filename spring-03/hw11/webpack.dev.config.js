const HtmlWebpackPlugin = require('html-webpack-plugin')
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const path = require('path');

module.exports = {
    entry: './src/ui/main.js',
    devtool: 'inline-source-map',
    mode: 'development',
    output: {
        path: path.resolve(__dirname),
        filename: 'bundle.js',
        libraryTarget: 'umd'
    },

    devServer: {
        static: path.resolve(__dirname) + '/src/ui',
        compress: true,
        port: 9000,
        host: 'localhost',
        open: true,
        proxy: {
            '*': {
                target: 'http://localhost:8080',
                secure: false,
                changeOrigin: true
            }
        }
    },

    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /(node_modules|bower_components|build)/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ["@babel/preset-env", '@babel/preset-react']
                    }
                }
            },
            {
                test: /\.css$/,
                use: [
                    MiniCssExtractPlugin.loader,
                    {loader: 'css-loader'}
                ]
            },
            {
                test: /\.less$/,
                use: [
                    MiniCssExtractPlugin.loader,
                    {loader: 'css-loader'},
                    {loader: 'less-loader'}
                ]
            }
        ]
    },
    plugins: [
        new MiniCssExtractPlugin({
            filename: 'styles.min.css'
        }),
        new HtmlWebpackPlugin({
            filename: 'index.html',
            template: 'src/ui/public/index.html'
        })
    ]
}
