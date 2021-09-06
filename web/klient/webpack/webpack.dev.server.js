var webpack = require('webpack');
var WebpackDevServer = require('webpack-dev-server');
var config = require('./webpack.dev');

if (process.argv.includes('--no-fix')) {
  console.warn("Setting eslint-loader option 'fix' to false");
  config.module.rules.find(rules => rules.loader === 'eslint-loader').options.fix = false;
}

const options = {
  static: {
    directory: 'src/client',
    watch: true,
  },
  proxy: {
    '**/(sprak|api)/**': {
      target: 'http://localhost:8071',
      secure: false,
    },
  },
  historyApiFallback: true,
  devMiddleware: {
    publicPath: config.output.publicPath,
    stats: {
      children: false,
      colors: true,
    },
  },
  port: 9100,
};

const wds = new WebpackDevServer(webpack(config), options);

(async () => {
  try {
    await wds.start();
  } catch (error) {
    return console.log(err); // NOSONAR
  }

  console.log('Listening at http://localhost:9100/');
  return undefined;
})();
