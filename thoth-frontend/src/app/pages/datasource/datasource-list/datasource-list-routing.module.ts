import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { DatasourceListPage } from './datasource-list.page';

const routes: Routes = [
  {
    path: '',
    component: DatasourceListPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class DatasourceListPageRoutingModule {}
