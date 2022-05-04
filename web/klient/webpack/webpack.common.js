const path = require('path');
const CircularDependencyPlugin = require('circular-dependency-plugin');
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const ESLintPlugin = require('eslint-webpack-plugin');

const CORE_DIR = path.resolve(__dirname, '../node_modules');
const ROOT_DIR = path.resolve(__dirname, '../src/client');
const APP_DIR = path.join(ROOT_DIR, 'app');
const STYLE_DIR = path.join(ROOT_DIR, 'styles');
const STORYBOOK_DIR = path.join(ROOT_DIR, 'storybookUtils');

const isDevelopment = JSON.stringify(process.env.NODE_ENV) === '"development"';

const config = {
  module: {
    rules: [{
      test: /\.(tsx?|ts?)$/,
      loader: 'babel-loader',
      options: {
        cacheDirectory: true,
      },
      include: APP_DIR,
    }, {
      test: /\.(less|css)?$/,
      use: [
        {
          loader: MiniCssExtractPlugin.loader,
          options: {
            publicPath: isDevelopment ? './' : '',
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
        },
      ],
      include: [APP_DIR],
    }, {
      test: /\.(less|css)?$/,
      use: [
        {
          loader: MiniCssExtractPlugin.loader,
          options: {
            publicPath: isDevelopment ? './' : '',
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
        },
      ],
      include: [STYLE_DIR, CORE_DIR],
    }, {
      test: /\.svg/,
      type: 'asset/resource',
    }],
  },

  plugins: [
    new ESLintPlugin({
      context: APP_DIR,
      extensions: ['tsx', 'ts'],
      failOnWarning: false,
      failOnError: !isDevelopment,
      fix: isDevelopment,
      overrideConfigFile: isDevelopment ? './eslint/eslintrc.dev.js' : './eslint/eslintrc.prod.js',
      lintDirtyModulesOnly: isDevelopment,
    }),
    new MiniCssExtractPlugin({
      filename: isDevelopment ? 'style[name].css' : 'style[name]_[contenthash].css',
      ignoreOrder: true,
    }),
    new CircularDependencyPlugin({
      exclude: /node_modules/,
      failOnError: true,
    }),
  ],

  resolve: {
    alias: {
      styles: path.join(ROOT_DIR, 'styles'),
      images: path.join(ROOT_DIR, 'images'),
      testHelpers: path.join(ROOT_DIR, 'testHelpers'),
      app: path.join(APP_DIR, 'app'),
      navAnsatt: path.join(APP_DIR, 'navAnsatt'),
      saksbehandler: path.join(APP_DIR, 'saksbehandler'),
      avdelingsleder: path.join(APP_DIR, 'avdelingsleder'),
      data: path.join(APP_DIR, 'data'),
      kodeverk: path.join(APP_DIR, 'kodeverk'),
      types: path.join(APP_DIR, 'types'),
      storybookUtils: STORYBOOK_DIR,
    },
    extensions: ['.js', '.jsx', '.tsx', '.ts', '.less'],
  },

  externals: {
    cheerio: 'window',
    'react/addons': 'react',
    'react/lib/ExecutionEnvironment': 'react',
    'react/lib/ReactContext': 'react',
  },
};

module.exports = config;
