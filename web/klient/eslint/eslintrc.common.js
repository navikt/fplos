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
      { 'js': 'never', 'jsx': 'never', 'ts': 'never', 'tsx': 'never' }
    ],
    'linebreak-style': OFF,
    'import/no-named-as-default': OFF,
    'max-len': [ERROR, 160],
    'react/require-default-props': OFF,
    'react/jsx-filename-extension': OFF,
    'no-undef': OFF,
    'react/static-property-placement': OFF,
    'react/state-in-constructor': OFF,

    // TODO (TOR) Skrudd av fordi den feilaktig rapporterar typescript-types som ubrukte
    'no-unused-vars': OFF,

    // TODO (TOR) Ignorert inntil videre grunnet kost/nytte
    'jsx-a11y/anchor-is-valid': OFF,
    'react/jsx-props-no-spreading': OFF,

    '@typescript-eslint/indent': OFF,

    // TODO (TOR) Midlertidig utkommenter
    'jsx-a11y/control-has-associated-label': OFF,
    '@typescript-eslint/explicit-member-accessibility': OFF,
    '@typescript-eslint/explicit-function-return-type': OFF,
    '@typescript-eslint/no-explicit-any': OFF,
    '@typescript-eslint/ban-ts-comment': OFF,
    '@typescript-eslint/explicit-module-boundary-types': OFF,
  },
  overrides: [{
    files: ['*.tsx'],
    rules: {
      'react/prop-types': OFF,
    },
  }],
};

module.exports = config;
