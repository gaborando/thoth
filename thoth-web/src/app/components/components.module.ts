import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  JdbcDatasourceParametersComponent
} from "./datasource-parameters/jdbc-datasource-parameters/jdbc-datasource-parameters.component";
import {IonicModule} from "@ionic/angular";
import {FormsModule} from "@angular/forms";
import {CodemirrorModule} from "@ctrl/ngx-codemirror";
import {PrintRequestModalComponent} from "./modals/print-request-modal/print-request-modal.component";
import {SharingOptionsComponent} from "./sharing-options/sharing-options.component";
import {ParametersFormComponent} from "./modals/parameters-form/parameters-form.component";
import {TypeaheadSelectComponent} from "./typeahead-select/typeahead-select.component";
import {CustomsModule} from "../common/customs.module";
import {
  RestDatasourceParametersComponent
} from "./datasource-parameters/rest-datasource-parameters/rest-datasource-parameters.component";
import {BackupManagerComponent} from "./backup-manager/backup-manager.component";



@NgModule({
  declarations: [
    JdbcDatasourceParametersComponent,
    RestDatasourceParametersComponent,
    PrintRequestModalComponent,
    SharingOptionsComponent,
    ParametersFormComponent,
    TypeaheadSelectComponent,
    BackupManagerComponent
  ],
  exports: [
    JdbcDatasourceParametersComponent,
    RestDatasourceParametersComponent,
    PrintRequestModalComponent,
    SharingOptionsComponent,
    ParametersFormComponent,
    TypeaheadSelectComponent,
    BackupManagerComponent
  ],
  imports: [
    CommonModule,
    IonicModule,
    FormsModule,
    CodemirrorModule,
    CustomsModule
  ]
})
export class ComponentsModule { }
