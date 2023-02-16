import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DatasourceDetailPage } from './datasource-detail.page';

const routes: Routes = [
  {
    path: '',
    component: DatasourceDetailPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DatasourceDetailPageRoutingModule {}
