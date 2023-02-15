import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { RendererDetailPage } from './renderer-detail.page';

const routes: Routes = [
  {
    path: '',
    component: RendererDetailPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RendererDetailPageRoutingModule {}
