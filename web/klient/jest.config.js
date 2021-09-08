module.exports = {
  projects: [
    {
      displayName: 'test',
      cacheDirectory: '<rootDir>/jest_cache/',
      coverageDirectory: '<rootDir>/coverage/',
      coverageReporters: [
        'text',
        'lcov',
        'html',
      ],
      moduleFileExtensions: ['js', 'json', 'ts', 'tsx', 'less', 'css'],
      moduleNameMapper: {
        '\\.(svg)$': '<rootDir>/_mocks/fileMock.js',
        '\\.(less|css)$': 'identity-obj-proxy',

        "^app(.*)$": "<rootDir>/src/client/app/app$1",
        "^data(.*)$": "<rootDir>/src/client/app/data$1",
        "^form(.*)$": "<rootDir>/src/client/app/form$1",
        "^kodeverk(.*)$": "<rootDir>/src/client/app/kodeverk$1",
        "^sharedComponents(.*)$": "<rootDir>/src/client/app/sharedComponents$1",
        "^avdelingsleder(.*)$": "<rootDir>/src/client/app/avdelingsleder$1",
        "^saksbehandler(.*)$": "<rootDir>/src/client/app/saksbehandler$1",
        "^types(.*)$": "<rootDir>/src/client/app/types$1",
        "^utils(.*)$": "<rootDir>/src/client/app/utils$1",
        "^testHelpers(.*)$": "<rootDir>/src/client/testHelpers$1",
        "^storybookUtils(.*)$": "<rootDir>/src/client/storybookUtils$1",
      },
      roots: [
        '<rootDir>/src/client/',
      ],
      setupFiles: [
        '<rootDir>/src/client/testHelpers/setup.js',
      ],
      setupFilesAfterEnv: [
        '<rootDir>/src/client/testHelpers/setup-test-env.ts',
      ],
      testEnvironment: 'jsdom',
      testMatch: ['**/?(*.)+(spec).+(ts|tsx)'],
      testPathIgnorePatterns: ['/node_modules/'],
      transform: {
        '^.+\\.(ts|tsx|js)?$': 'babel-jest',
        '^.+.(css|less)$': 'jest-transform-stub',
      },
      transformIgnorePatterns: ['<rootDir>.*(node_modules)(?!.*nav.*).*$'],
    },
    {
      displayName: 'lint',
      runner: 'jest-runner-eslint',
      testMatch: ['**/?(*.)+(spec).+(ts|tsx)'],
    },
  ],
  watchPlugins: ['jest-runner-eslint/watch-fix'],
};
