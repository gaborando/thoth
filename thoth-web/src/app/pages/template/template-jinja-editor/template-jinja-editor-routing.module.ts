import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { TemplateJinjaEditorPage } from './template-jinja-editor.page';

const routes: Routes = [
  {
    path: '',
    component: TemplateJinjaEditorPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TemplateJinjaEditorPageRoutingModule {}
