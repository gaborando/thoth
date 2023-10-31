import {Datasource} from "../../common/types/datasource";
import {DataSourceService} from "../../services/api/data-source.service";
import {AlertController} from "@ionic/angular";
import {ScreenMessageService} from "../../services/screen-message.service";
import {Component, EventEmitter, Injectable, Input, Output} from "@angular/core";
import {text} from "ionicons/icons";

@Component({
  template: '',
})
export abstract class DatasourceParametersComponent{

  checkError: string | null = null;
  validParameters = false;

  @Output()
  public onInit = new EventEmitter();

  @Output()
  public isValid: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Input()
  public definition: any = {}

  @Input()
  public parameters: string[] = [];

  @Input()
  properties: { name: string, helper: string }[] = []

  abstract getType(): string;


  constructor(private dataSourceService: DataSourceService,
                        private alertController: AlertController,
                        private screenMessageService: ScreenMessageService) {
  }
  isNumeric(str: any) {
    return !isNaN(str) && // use type coercion to parse the _entirety_ of the string (`parseFloat` alone does not do this)...
      !isNaN(parseFloat(str)) // ...and ensure strings of whitespace fail
  }


  detectParameters() {
    this.parameters = [];
    const markers = [];
    const pattern = new RegExp(/\{\{([a-z0-9\\._]+)}}/g);
    let z;
    while (null != (z = pattern.exec(JSON.stringify(this.definition)))) {
      markers.push(z[1]);
    }
    this.parameters = [...new Set(markers)]
    this.validParameters = false;
    this.isValid.emit(this.validParameters)
    this.checkError = null;
  }



  async checkQuery(){
    const inputs: any[] = [];
    this.parameters.forEach(m => {
      inputs.push({
        id: m,
        label: m,
        name: m,
        placeholder: m,
        type: 'text'
      })
    })
    const alert = await this.alertController.create({
      header: "Parameters",
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
          this.properties =  await this.dataSourceService.checkParameters(this.getType(), this.definition, params);
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




  async saveParameters(name: string) {
    const request: any = {...this.definition}
    request.parameters = this.parameters;
    request.name = name;
    request.properties = this.properties;
    return this.dataSourceService.saveParameters(this.getType(), request)
  }

  cloneParameters(name: string) {
    const request: any = {...this.definition}
    request.parameters = this.parameters;
    request.id = null;
    request.name = name;
    request.properties = this.properties;
    return this.dataSourceService.saveParameters(this.getType(), request)
  }


  async updateParameters() {
    const request: any = {...this.definition}
    request.parameters = this.parameters;
    request.properties = this.properties;
    return this.dataSourceService.updateParameters(this.getType(),  request)
  }

  async editProperty(p: { name: string; helper: string }) {
    const alert = await this.alertController.create({
      header: "Edit " + p.name,
      message: "Edit the helper text",
      inputs: [
        {
          name: 'helper',
          value: p.helper,
          label: 'Helper',
          type: 'text'
        }
      ],
      buttons: [
        {
          text: 'Cancel',
          role: 'cancel',
        },
        {
          text: 'OK',
          role: 'confirm',
          handler: async (resp) => {
            p.helper = resp.helper;
          },
        },
      ],
    })
    return alert.present();
  }


}
