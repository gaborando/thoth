import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { RendererDetailPageRoutingModule } from './renderer-detail-routing.module';

import { RendererDetailPage } from './renderer-detail.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    RendererDetailPageRoutingModule
  ],
  declarations: [RendererDetailPage]
})
export class RendererDetailPageModule {}
