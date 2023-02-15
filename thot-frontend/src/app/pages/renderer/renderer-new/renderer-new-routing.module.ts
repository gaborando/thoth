import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { RendererNewPage } from './renderer-new-page.component';

const routes: Routes = [
  {
    path: '',
    component: RendererNewPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RenderNewPageRoutingModule {}
