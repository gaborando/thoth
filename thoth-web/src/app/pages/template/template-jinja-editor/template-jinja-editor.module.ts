import { NgModule } from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { TemplateJinjaEditorPageRoutingModule } from './template-jinja-editor-routing.module';

import { TemplateJinjaEditorPage } from './template-jinja-editor.page';
import {CodemirrorModule} from "@ctrl/ngx-codemirror";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    TemplateJinjaEditorPageRoutingModule,
    CodemirrorModule,
    NgOptimizedImage
  ],
  declarations: [TemplateJinjaEditorPage]
})
export class TemplateJinjaEditorPageModule {}
