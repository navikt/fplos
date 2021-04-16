const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CORE_DIR = path.resolve(__dirname, '../node_modules');
const APP_DIR = path.resolve(__dirname, '../src/client');
const IMAGE_DIR = path.join(APP_DIR, 'images');
const CSS_DIR = path.join(APP_DIR, 'styles');

module.exports = {
  core: {
    builder: "webpack5",
  },
  stories: ['../src/client/storybook/stories/**/*.stories.@(js|tsx)'],
  addons: ['@storybook/addon-docs/preset', '@storybook/addon-actions/register', '@storybook/addon-knobs/register'],
  webpackFinal: async (config, { configType }) => {
    //Fjern default svg-loader
    config.module.rules = config.module.rules.map( data => {
      if (/svg\|/.test(String(data.test))) {
        data.test = /\.(ico|jpg|jpeg|png|gif|eot|otf|webp|ttf|woff|woff2|cur|ani)(\?.*)?$/;
      }
      return data;
    });

    config.devtool = 'eval-cheap-source-map';

    // Make whatever fine-grained changes you need
    config.module.rules = config.module.rules.concat({
      test: /\.(tsx?|ts?)$/,
      enforce: 'pre',
      loader: 'eslint-loader',
      options: {
        failOnWarning: false,
        failOnError: false,
        configFile: path.resolve(__dirname, '../eslint/eslintrc.dev.js'),
        fix: true,
        cache: true,
      },
      include: [APP_DIR],
    }, {
      test: /\.(tsx?|ts?)$/,
      use: [
        { loader: 'cache-loader' },
        {
          loader: 'thread-loader',
          options: {
            workers: process.env.CIRCLE_NODE_TOTAL || require('os')
              .cpus() - 1,
            workerParallelJobs: 50,
          },
        },
        {
          loader: 'babel-loader',
          options: {
            cacheDirectory: true,
          },
        },
      ],
      include: APP_DIR,
    }, {
      test: /\.(less|css)?$/,
      use: [
        {
          loader: MiniCssExtractPlugin.loader,
          options: {
            publicPath: './',
          },
        }, {
          loader: 'css-loader',
          options: {
            importLoaders: 1,
            modules: {
              localIdentName: '[name]_[local]_[contenthash:base64:5]',
            },
          },
        }, {
          loader: 'less-loader',
          options: {
            lessOptions: {
              modules: true,
              localIdentName: '[name]_[local]_[contenthash:base64:5]',
              modifyVars: {
                nodeModulesPath: '~',
                coreModulePath: '~',
              },
            },
          },
        }],
      include: [APP_DIR],
      exclude: [CSS_DIR],
    }, {
      test: /\.(less)?$/,
      use: [
        {
          loader: MiniCssExtractPlugin.loader,
          options: {
            publicPath: './',
          },
        }, {
          loader: 'css-loader',
        }, {
          loader: 'less-loader',
          options: {
            lessOptions: {
              modifyVars: {
                nodeModulesPath: '~',
                coreModulePath: '~',
              },
            },
          },
        }],
      include: [CSS_DIR, CORE_DIR],
    }, {
      test: /\.(jpg|png|svg)$/,
      loader: 'file-loader',
      options: {
        name: '[name]_[contenthash].[ext]',
      },
      include: [IMAGE_DIR],
    });

    config.plugins.push(new MiniCssExtractPlugin({
      filename: 'style[name].css',
      ignoreOrder: true,
    }));

    config.resolve.alias =  {
      styles: path.join(APP_DIR, 'styles'),
      images: path.join(APP_DIR, 'images'),
      testHelpers: path.join(APP_DIR, 'testHelpers'),
      app: path.join(APP_DIR, 'app/app'),
      navAnsatt: path.join(APP_DIR, 'app/navAnsatt'),
      form: path.join(APP_DIR, 'app/form'),
      saksbehandler: path.join(APP_DIR, 'app/saksbehandler'),
      avdelingsleder: path.join(APP_DIR, 'app/avdelingsleder'),
      data: path.join(APP_DIR, 'app/data'),
      kodeverk: path.join(APP_DIR, 'app/kodeverk'),
      sharedComponents: path.join(APP_DIR, 'app/sharedComponents'),
      utils: path.join(APP_DIR, 'app/utils'),
    };
    
    config.resolve.extensions.push('.ts', '.tsx', '.less');

    // Return the altered config
    return config;
  },
}