import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { SecretListPage } from './secret-list.page';

const routes: Routes = [
  {
    path: '',
    component: SecretListPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SecretListPageRoutingModule {}
