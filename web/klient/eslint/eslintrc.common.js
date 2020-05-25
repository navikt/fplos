const OFF = 0;
const ERROR = 2;

const config = {
  root: true,

  env: {
    es6: true,
    browser: true,
    mocha: true,
  },

  parser: '@typescript-eslint/parser',

  plugins: ['@typescript-eslint'],

  extends: [
    'airbnb',
    'plugin:@typescript-eslint/recommended',
  ],

  parserOptions: {
    sourceType: 'module',
    ecmaFeatures: {
      ecmaVersion: 8,
      jsx: true,
      impliedStrict: true,
    },
  },

  rules: {
    'import/extensions': [
      'error',
      'ignorePackages',
      {
        ts: 'never', tsx: 'never',
      },
    ],
    'linebreak-style': OFF,
    'max-len': [ERROR, 160],
    'no-undef': OFF,
    'react/require-default-props': OFF,
    'react/jsx-filename-extension': OFF,
    'react/static-property-placement': OFF,
    'react/state-in-constructor': OFF,
    'react/prop-types': OFF,

    // TODO (TOR) Ignorert inntil videre grunnet kost/nytte
    'react/jsx-props-no-spreading': OFF,
    'jsx-a11y/control-has-associated-label': OFF,
    '@typescript-eslint/no-explicit-any': OFF,
    '@typescript-eslint/ban-ts-comment': OFF,
    '@typescript-eslint/explicit-module-boundary-types': OFF,
  },
};

module.exports = config;
