import { Injectable } from '@angular/core';
import {Template} from "../common/types/template";
import {environment} from "../../environments/environment";
import {TemplateService} from "./api/template.service";
import {AlertController, LoadingController} from "@ionic/angular";
import {ClientService} from "./api/client.service";
import {ScreenMessageService} from "./screen-message.service";
import {GuiUtilsService} from "./gui-utils.service";

@Injectable({
  providedIn: 'root'
})
export class TemplateGuiUtilsService {

  constructor(private templateService: TemplateService,
              private alertController: AlertController,
              private clientService: ClientService,
              private screenMessageService: ScreenMessageService,
              private loadingController: LoadingController,
              private guiUtils: GuiUtilsService) { }

  async renderTemplate(t: Template) {
    const inputs: any[] = [];
    t.markers.forEach(m => {
      inputs.push({
        id: m,
        label: m,
        name: m,
        placeholder: m,
      })
    })
    const alert = await this.alertController.create({
      header: "Render a Template",
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

      var query = new URLSearchParams();
      for (const key of Object.keys(resp.data.values || {})) {
        query.append(key, resp.data.values[key]);
      }
      const accessToken = localStorage.getItem("access_token");
      if (accessToken) {
        query.append("access_token", accessToken);
      }
      window.open((await environment()).apiUrl + '/template/' + t.id + '/render/pdf?' + query.toString())
    }
  }

  async printTemplate(t: Template) {
    let inputs: any[] = [];
    t.markers.forEach(m => {
      inputs.push({
        id: m,
        label: m,
        name: m,
        placeholder: m,
      })
    })
    const alert = await this.alertController.create({
      header: "Print a Template",
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
      const params = resp.data.values || {};
      const {data, role} = await this.guiUtils.printRequestModal();
      if(role === 'confirm'){

        const loading =await this.loadingController.create();
        await loading.present();
        try {
          await this.templateService.print(t.id, params, data.client.identifier, data.printService, data.copies);
          await this.screenMessageService.showDone();
        }finally {
          await loading.dismiss();
        }
      }

    }
  }
}
