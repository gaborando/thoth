import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ApiKeyListPage } from './api-key-list.page';

const routes: Routes = [
  {
    path: '',
    component: ApiKeyListPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ApiKeyListPageRoutingModule {}
