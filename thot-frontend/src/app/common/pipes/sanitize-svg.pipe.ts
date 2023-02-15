import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'sanitizeSvg'
})
export class SanitizeSvgPipe implements PipeTransform {

  transform(value: unknown, ...args: unknown[]): unknown {
    return null;
  }

}
