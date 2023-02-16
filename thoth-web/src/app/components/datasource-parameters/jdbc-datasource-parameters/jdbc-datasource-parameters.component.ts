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
})
export class JdbcDatasourceParametersComponent implements OnInit, DatasourceParametersComponent {

  @Output()
  public onInit = new EventEmitter();

  @Output()
  public isValid = new EventEmitter();

  @Input()
  jdbcDatasourceParameters: any = {}

  @Input()
  queryParams: string[] = ['id'];
  validParameters = false;

  @Input()
  detectedProperties: string[] = []
  checkError: string | null = null;

  constructor(
    private dataSourceService: DataSourceService,
    private alertController: AlertController,
    private screenMessageService: ScreenMessageService
  ) {
  }

  ngOnInit() {
    this.onInit.emit();
  }

  isNumeric(str: any) {
    return !isNaN(str) && // use type coercion to parse the _entirety_ of the string (`parseFloat` alone does not do this)...
      !isNaN(parseFloat(str)) // ...and ensure strings of whitespace fail
  }

  async checkQuery() {
    const inputs: any[] = [];
    this.queryParams.forEach(m => {
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
      for (const key of Object.keys(resp.data.values)) {
        if (this.isNumeric(resp.data.values[key])) {
          params[key] = parseFloat(resp.data.values[key])
        } else {
          params[key] = resp.data.values[key];
        }
      }
      await this.screenMessageService.loadingWrapper(async () => {
        try {
          this.detectedProperties = await this.dataSourceService.checkParameters('jdbc', this.jdbcDatasourceParameters, params);
          this.validParameters = true;
          this.isValid.emit(this.validParameters)
          await this.screenMessageService.showDone();
        } catch (e: any) {
          this.checkError = e.message;
        }
      })
    }

  }

  detectQueryParameters() {
    this.queryParams = [];
    const markers = [];
    const pattern = new RegExp(/:([a-zA-Z0-9\\._]+)/g);
    let z;
    while (null != (z = pattern.exec(this.jdbcDatasourceParameters.query))) {
      markers.push(z[1]);
    }
    this.queryParams = [...new Set(markers)]
    this.validParameters = false;
    this.isValid.emit(this.validParameters)
    this.checkError = null;
  }

  saveParameters(name: string) {
    const request = {...this.jdbcDatasourceParameters}
    request.parameters = this.queryParams;
    request.name = name;
    request.properties = this.detectedProperties;
    return this.dataSourceService.saveParameters('jdbc', request)
  }

  async updateParameters() {
    const request = {...this.jdbcDatasourceParameters}
    request.parameters = this.queryParams;
    request.properties = this.detectedProperties;
    return this.dataSourceService.updateParameters('jdbc', request)
  }

  codeLoaded(component: CodemirrorComponent) {
  }
}
