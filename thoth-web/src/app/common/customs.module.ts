import {NgModule} from '@angular/core';
import {AutocompleteDirective} from "./directives/autocomplete.directive";

@NgModule({
  declarations: [AutocompleteDirective],
  exports: [AutocompleteDirective]
})
export class CustomsModule {
}
