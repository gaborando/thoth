import { Injectable } from '@angular/core';
import {Template} from "../common/types/template";
import {environment} from "../../environments/environment";
import {TemplateService} from "./api/template.service";
import {AlertController, LoadingController, ModalController} from "@ionic/angular";
import {ClientService} from "./api/client.service";
import {ScreenMessageService} from "./screen-message.service";
import {GuiUtilsService} from "./gui-utils.service";
import {ParametersFormComponent} from "../components/modals/parameters-form/parameters-form.component";

@Injectable({
  providedIn: 'root'
})
export class TemplateGuiUtilsService {

  constructor(private templateService: TemplateService,
              private alertController: AlertController,
              private clientService: ClientService,
              private screenMessageService: ScreenMessageService,
              private loadingController: LoadingController,
              private modalController: ModalController,
              private guiUtils: GuiUtilsService) { }

  async renderTemplate(t: Template) {
    const resp = await this.guiUtils.parametersFormModal("Render a Template", new Set<string>(t.markers), t.id);
    if (!resp.role) {
      var query = new URLSearchParams();
      query.append("json", JSON.stringify(resp.data));
      const accessToken = localStorage.getItem("access_token");
      if (accessToken) {
        query.append("access_token", accessToken);
      }
      window.open((await environment()).apiUrl + '/template/' + t.id + '/render/pdf?' + query.toString())
    }
  }

  async printTemplate(t: Template) {
    const resp = await this.guiUtils.parametersFormModal("Print a Template", new Set<string>(t.markers), t.id);
    if (!resp.role) {
      const params = {"json": JSON.stringify(resp.data.values)} || {};
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
