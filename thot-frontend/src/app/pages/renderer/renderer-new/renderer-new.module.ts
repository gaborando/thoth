import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { RenderNewPageRoutingModule } from './renderer-new-routing.module';

import { RendererNewPage } from './renderer-new-page.component';
import {ComponentsModule} from "../../../components/components.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    RenderNewPageRoutingModule,
    ComponentsModule
  ],
  declarations: [RendererNewPage]
})
export class RenderNewPageModule {}
