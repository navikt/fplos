import '@testing-library/jest-dom';

// Skjul warning fra echarts
const originalWarn = console.warn.bind(console.warn);
beforeAll(() => {
  console.warn = (msg) => !msg.toString().includes('Can\'t get DOM width or height.') && originalWarn(msg);
});
afterAll(() => {
  console.warn = originalWarn;
});
