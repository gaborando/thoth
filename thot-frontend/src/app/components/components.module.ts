import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  JdbcDatasourceParametersComponent
} from "./renderer/datasource-parameters/jdbc-datasource-parameters/jdbc-datasource-parameters.component";
import {IonicModule} from "@ionic/angular";
import {FormsModule} from "@angular/forms";
import {CodemirrorModule} from "@ctrl/ngx-codemirror";



@NgModule({
  declarations: [
    JdbcDatasourceParametersComponent
  ],
  exports: [
    JdbcDatasourceParametersComponent
  ],
    imports: [
        CommonModule,
        IonicModule,
        FormsModule,
        CodemirrorModule
    ]
})
export class ComponentsModule { }
