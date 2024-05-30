import { Injectable } from '@angular/core';
import {AlertController, ModalController} from "@ionic/angular";
import {PrintRequestModalComponent} from "../components/modals/print-request-modal/print-request-modal.component";
import {ParametersFormComponent} from "../components/modals/parameters-form/parameters-form.component";
import {Template} from "../common/types/template";
import {ApiKeyService} from "./api/api-key.service";
import * as console from "console";
import {ScreenMessageService} from "./screen-message.service";

@Injectable({
  providedIn: 'root'
})
export class GuiUtilsService {


  constructor(private modalController: ModalController,
              private alertController: AlertController,
              private apiKeyService: ApiKeyService,
              private screenMessageService: ScreenMessageService) { }

  async printRequestModal(){
    const modal = await this.modalController.create({
      component: PrintRequestModalComponent
    });
    await modal.present();
    return modal.onWillDismiss()
  }

  async parametersFormModal(title: string, parameters: Set<string>, resourceId: string){
    const m = await this.modalController.create({
      component: ParametersFormComponent,
      componentProps: {
        parameters,
        title,
        resourceId
      }
    });
    await m.present();
    return await m.onDidDismiss();
  }

  async selectApiKey() {

    const alert = await this.alertController.create({
      header: 'Insert API Key',
      message: 'Insert the API Key to use. Check if the user associated to the API Key has access to this resource.',
      inputs:[
        {
          label: 'API KEY',
          type: 'text',
          name: 'apiKey',
          placeholder: 'Enter API Key'
        }
      ],
            buttons: [
              {
                text: 'Cancel',
                role: 'cancel'
              },
              {
                text: 'OK'
              }
            ]
    });
    await alert.present();
    const resp = await alert.onDidDismiss()
    return resp.data.values.apiKey
  }
}
