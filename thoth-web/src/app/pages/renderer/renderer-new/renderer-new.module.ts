import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { RenderNewPageRoutingModule } from './renderer-new-routing.module';

import { RendererNewPage } from './renderer-new-page.component';
import {ComponentsModule} from "../../../components/components.module";
import {TypeaheadSelectComponent} from "../../../components/typeahead-select/typeahead-select.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    RenderNewPageRoutingModule,
    ComponentsModule
  ],
  exports: [
  ],
  declarations: [RendererNewPage]
})
export class RenderNewPageModule {}
