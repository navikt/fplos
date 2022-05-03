const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const ESLintPlugin = require('eslint-webpack-plugin');
const CORE_DIR = path.resolve(__dirname, '../node_modules');
const APP_DIR = path.resolve(__dirname, '../src/client');
const CSS_DIR = path.join(APP_DIR, 'styles');
const STORYBOOK_DIR = path.join(APP_DIR, 'storybookUtils');

module.exports = {
  core: {
    builder: "webpack5",
  },
  stories: ['../src/client/app/**/*.stories.@(tsx)'],
  addons: ['@storybook/addon-docs/preset', '@storybook/addon-actions/register'],
  webpackFinal: async (config, { configType }) => {
    config.devtool = 'inline-source-map';

    // Make whatever fine-grained changes you need
    config.module.rules = config.module.rules.concat({
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
    });

    config.plugins.push(new MiniCssExtractPlugin({
      filename: 'style[name].css',
      ignoreOrder: true,
    }));
    config.plugins.push(new ESLintPlugin({
      context: APP_DIR,
      extensions: ['tsx', 'ts'],
      failOnWarning: false,
      failOnError: false,
      fix: true,
      overrideConfigFile: path.resolve(__dirname, '../eslint/eslintrc.dev.js'),
      lintDirtyModulesOnly: true,
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
      storybookUtils: STORYBOOK_DIR,
    };
    
    config.resolve.extensions.push('.ts', '.tsx', '.less');

    // Return the altered config
    return config;
  },
}