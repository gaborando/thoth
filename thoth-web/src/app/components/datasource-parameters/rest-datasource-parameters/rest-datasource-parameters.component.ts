import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {DatasourceParametersComponent} from "../datasource-parameters-component";
import {Datasource} from "../../../common/types/datasource";
import {DataSourceService} from "../../../services/api/data-source.service";
import {registerHelper} from "codemirror";
import {AlertController} from "@ionic/angular";
import {ScreenMessageService} from "../../../services/screen-message.service";

@Component({
  selector: 'app-rest-datasource-parameters',
  templateUrl: './rest-datasource-parameters.component.html',
  styleUrls: ['./rest-datasource-parameters.component.scss'],
  inputs: ['properties', 'parameters', 'definition'],
  outputs: ['onInit', 'isValid']
})
export class RestDatasourceParametersComponent extends DatasourceParametersComponent implements OnInit {

  obj = Object;

  constructor(dataSourceService: DataSourceService,
              alertController: AlertController,
              screenMessageService: ScreenMessageService) {
    super(dataSourceService, alertController, screenMessageService)
    this.definition  ={
      url: 'http://localhost:8080/template/',
      method: 'GET',
      queryParameters: {
        page: '0',
      },
      headers: {
        'Accept': 'application/json'
      },
      body: {},
      jsonQuery: ''
    };
  }

  override getType(): string {
    return 'rest';
  }

  ngOnInit() {
    this.onInit.emit(this);
    console.log(this.properties);
  }

  addQueryParam(key: any, value: any) {
    this.definition.queryParameters[key] = value;
    this.detectParameters();
  }

  removeQueryParam(key: string) {
    delete this.definition.queryParameters[key];
    this.detectParameters();
  }

  addHeader(header: any, value: any) {
    this.definition.headers[header] = value;
    this.detectParameters();
  }

  removeHeader(h: string) {
    delete this.definition.headers[h];
    this.detectParameters();
  }

  addBody(key: any, value: any) {
    if (this.isNumeric(value)) {
      this.definition.body[key] = parseFloat(value)
    } else {
      this.definition.body[key] = value;
    }
    this.detectParameters();
  }

  removeBodyItem(b: string) {
    delete this.definition.body[b];
    this.detectParameters();
  }


}
