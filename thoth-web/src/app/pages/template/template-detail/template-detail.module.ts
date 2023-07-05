import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { TemplateDetailPageRoutingModule } from './template-detail-routing.module';

import { TemplateDetailPage } from './template-detail.page';
import {ComponentsModule} from "../../../components/components.module";
import {CustomsModule} from "../../../common/customs.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    TemplateDetailPageRoutingModule,
    ComponentsModule,
    CustomsModule
  ],
  declarations: [TemplateDetailPage]
})
export class TemplateDetailPageModule {}
