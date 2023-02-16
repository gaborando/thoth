import { SanitizeSvgPipe } from './sanitize-svg.pipe';

describe('SanitizeSvgPipe', () => {
  it('create an instance', () => {
    const pipe = new SanitizeSvgPipe();
    expect(pipe).toBeTruthy();
  });
});
