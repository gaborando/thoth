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
})
export class RestDatasourceParametersComponent implements OnInit, DatasourceParametersComponent {

  @Output()
  public onInit = new EventEmitter();

  @Output()
  public isValid = new EventEmitter();

  @Input()
  restDatasourceParameters: any = {
    url: 'http://localhost:8080/template/',
    method: 'GET',
    queryParameters: {
      page: '0',
    },
    headers: {
      'Accept': 'application/json'
    },
    body: {},
    jsonQuery: '$'
  }

  @Input()
  parameters: string[] = [];
  validParameters = false;

  @Input()
  detectedProperties: string[] = []
  checkError: string | null = null;
  obj = Object;

  constructor(private dataSourceService: DataSourceService,
              private alertController: AlertController,
              private screenMessageService: ScreenMessageService) {
  }

  ngOnInit() {
    this.onInit.emit(this);
  }

  isNumeric(str: any) {
    return !isNaN(str) && // use type coercion to parse the _entirety_ of the string (`parseFloat` alone does not do this)...
      !isNaN(parseFloat(str)) // ...and ensure strings of whitespace fail
  }

  async checkQuery() {
    const inputs: any[] = [];
    this.parameters.forEach(m => {
      inputs.push({
        id: m,
        label: m,
        name: m,
        placeholder: m,
      })
    })
    const alert = await this.alertController.create({
      header: "Query Parameters",
      inputs: inputs,
      buttons: [
        {
          text: 'ok'
        },
        {
          text: 'cancel',
          role: 'cancel'
        }
      ]
    });
    await alert.present();
    const resp = await alert.onDidDismiss();
    if (!resp.role) {
      const params: any = {}
      for (const key of Object.keys(resp.data.values || {})) {
        if (this.isNumeric(resp.data.values[key])) {
          params[key] = parseFloat(resp.data.values[key])
        } else {
          params[key] = resp.data.values[key];
        }
      }
      await this.screenMessageService.loadingWrapper(async () => {
        try {
          this.checkError = null;
          this.detectedProperties = await this.dataSourceService.checkParameters('rest',
            {
              request: this.restDatasourceParameters
            }, params);
          this.validParameters = true;
          this.isValid.emit(this.validParameters)
          await this.screenMessageService.showDone();
        } catch (e: any) {
          this.checkError = e.message;
          await this.screenMessageService.showError(e);
        }
      })
    }
  }

  saveParameters(name: string) {
    const request = {...this.restDatasourceParameters}
    request.parameters = this.parameters;
    request.name = name;
    request.properties = this.detectedProperties;
    return this.dataSourceService.saveParameters('rest', request)
  }

  cloneParameters(name: string) {
    const request = {...this.restDatasourceParameters}
    request.id = null;
    request.parameters = this.parameters;
    request.name = name;
    request.properties = this.detectedProperties;
    return this.dataSourceService.saveParameters('rest', request)
  }

  async updateParameters() {
    const request = {...this.restDatasourceParameters}
    request.parameters = this.parameters;
    request.properties = this.detectedProperties;
    return this.dataSourceService.updateParameters('rest', request)
  }

  addQueryParam(key: any, value: any) {
    this.restDatasourceParameters.queryParameters[key] = value;
    this.detectParameters();
  }

  removeQueryParam(key: string) {
    delete this.restDatasourceParameters.queryParameters[key];
    this.detectParameters();
  }

  addHeader(header: any, value: any) {
    this.restDatasourceParameters.headers[header] = value;
    this.detectParameters();
  }

  removeHeader(h: string) {
    delete this.restDatasourceParameters.headers[h];
    this.detectParameters();
  }

  addBody(key: any, value: any) {
    if (this.isNumeric(value)) {
      this.restDatasourceParameters.body[key] = parseFloat(value)
    } else {
      this.restDatasourceParameters.body[key] = value;
    }
    this.detectParameters();
  }

  removeBodyItem(b: string) {
    delete this.restDatasourceParameters.body[b];
    this.detectParameters();
  }

  detectParameters() {
    /**
     *  method: 'GET',
     *     queryParameters: {
     *       ke1: 'value1',
     *       key2: 'value2'
     *     },
     *     headers: {
     *       'Accept':'application/json'
     *     },
     *     body: {}
     */
    const pattern = new RegExp(/{{([a-zA-Z0-9\\._]+)}}/g);
    const markers = [];
    let z;
    while (null != (z = pattern.exec(JSON.stringify(this.restDatasourceParameters)))) {
      markers.push(z[1]);
    }
    this.parameters = [...new Set(markers)]
  }
}
