import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { DatasourceDetailPageRoutingModule } from './datasource-detail-routing.module';

import { DatasourceDetailPage } from './datasource-detail.page';
import {ComponentsModule} from "../../../components/components.module";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        IonicModule,
        DatasourceDetailPageRoutingModule,
        ComponentsModule
    ],
  declarations: [DatasourceDetailPage]
})
export class DatasourceDetailPageModule {}
