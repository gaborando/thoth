import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { RenderListPageRoutingModule } from './renderer-list-routing.module';

import { RendererListPage } from './renderer-list-page.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    RenderListPageRoutingModule
  ],
  declarations: [RendererListPage]
})
export class RenderListPageModule {}
