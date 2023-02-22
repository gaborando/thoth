import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { TemplateDetailPage } from './template-detail.page';

const routes: Routes = [
  {
    path: '',
    component: TemplateDetailPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TemplateDetailPageRoutingModule {}
