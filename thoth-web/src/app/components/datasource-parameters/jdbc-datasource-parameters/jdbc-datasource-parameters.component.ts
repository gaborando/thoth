import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DataSourceService} from "../../../services/api/data-source.service";
import {AlertController} from "@ionic/angular";
import {DatasourceParametersComponent} from "../datasource-parameters-component";
import {ScreenMessageService} from "../../../services/screen-message.service";
import {CodemirrorComponent} from "@ctrl/ngx-codemirror";

@Component({
  selector: 'app-jdbc-datasource-parameters',
  templateUrl: './jdbc-datasource-parameters.component.html',
  styleUrls: ['./jdbc-datasource-parameters.component.scss'],
  inputs: ['properties', 'parameters', 'definition'],
  outputs: ['onInit', 'isValid']
})
export class JdbcDatasourceParametersComponent extends DatasourceParametersComponent implements OnInit {


  constructor(
    dataSourceService: DataSourceService,
    alertController: AlertController,
    screenMessageService: ScreenMessageService
  ) {
    super(dataSourceService, alertController, screenMessageService);
  }

  override getType(): string {
    return 'jdbc';
  }

  ngOnInit() {
    this.onInit.emit();
  }


  codeLoaded(component: CodemirrorComponent) {
  }


}
