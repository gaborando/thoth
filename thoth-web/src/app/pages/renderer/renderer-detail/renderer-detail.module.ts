import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { RendererDetailPageRoutingModule } from './renderer-detail-routing.module';

import { RendererDetailPage } from './renderer-detail.page';
import {ComponentsModule} from "../../../components/components.module";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        IonicModule,
        RendererDetailPageRoutingModule,
        ComponentsModule
    ],
  declarations: [RendererDetailPage]
})
export class RendererDetailPageModule {}
