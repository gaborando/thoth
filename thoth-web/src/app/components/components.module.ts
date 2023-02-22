import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  JdbcDatasourceParametersComponent
} from "./datasource-parameters/jdbc-datasource-parameters/jdbc-datasource-parameters.component";
import {IonicModule} from "@ionic/angular";
import {FormsModule} from "@angular/forms";
import {CodemirrorModule} from "@ctrl/ngx-codemirror";
import {
  RestDatasourceParametersComponent
} from "./datasource-parameters/rest-datasource-parameters/rest-datasource-parameters.component";
import {PrintRequestModalComponent} from "./modals/print-request-modal/print-request-modal.component";
import {SharingOptionsComponent} from "./sharing-options/sharing-options.component";



@NgModule({
  declarations: [
    JdbcDatasourceParametersComponent,
    RestDatasourceParametersComponent,
    PrintRequestModalComponent,
    SharingOptionsComponent,
  ],
  exports: [
    JdbcDatasourceParametersComponent,
    RestDatasourceParametersComponent,
    PrintRequestModalComponent,
    SharingOptionsComponent,
  ],
    imports: [
        CommonModule,
        IonicModule,
        FormsModule,
        CodemirrorModule
    ]
})
export class ComponentsModule { }
