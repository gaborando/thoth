import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { DatasourceNewPageRoutingModule } from './datasource-new-routing.module';

import { DatasourceNewPage } from './datasource-new.page';
import {ComponentsModule} from "../../../components/components.module";

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        IonicModule,
        DatasourceNewPageRoutingModule,
        ComponentsModule
    ],
  declarations: [DatasourceNewPage]
})
export class DatasourceNewPageModule {}
