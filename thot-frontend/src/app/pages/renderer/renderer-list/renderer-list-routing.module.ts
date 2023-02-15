import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { RendererListPage } from './renderer-list-page.component';

const routes: Routes = [
  {
    path: '',
    component: RendererListPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RenderListPageRoutingModule {}
