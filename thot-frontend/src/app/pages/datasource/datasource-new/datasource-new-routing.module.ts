import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DatasourceNewPage } from './datasource-new.page';

const routes: Routes = [
  {
    path: '',
    component: DatasourceNewPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DatasourceNewPageRoutingModule {}
