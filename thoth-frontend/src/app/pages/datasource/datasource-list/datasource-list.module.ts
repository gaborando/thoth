import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { DatasourceListPageRoutingModule } from './datasource-list-routing.module';

import { DatasourceListPage } from './datasource-list.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    DatasourceListPageRoutingModule
  ],
  declarations: [DatasourceListPage]
})
export class DatasourceListPageModule {}
