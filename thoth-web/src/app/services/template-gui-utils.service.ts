import {Injectable} from '@angular/core';
import {Template} from "../common/types/template";
import {environment} from "../../environments/environment";
import {TemplateService} from "./api/template.service";
import {AlertController, LoadingController, ModalController} from "@ionic/angular";
import {ClientService} from "./api/client.service";
import {ScreenMessageService} from "./screen-message.service";
import {GuiUtilsService} from "./gui-utils.service";
import {ParametersFormComponent} from "../components/modals/parameters-form/parameters-form.component";
import {language} from "ionicons/icons";

@Injectable({
  providedIn: 'root'
})
export class TemplateGuiUtilsService {


  private drawIoWindow: any;

  constructor(private templateService: TemplateService,
              private alertController: AlertController,
              private clientService: ClientService,
              private screenMessageService: ScreenMessageService,
              private loadingController: LoadingController,
              private modalController: ModalController,
              private guiUtils: GuiUtilsService) {
  }

  async renderTemplate(t: Template) {
    const resp = await this.guiUtils.parametersFormModal("Render a Template", new Set<string>(t.markers), t.id);
    if (!resp.role) {
      var query = new URLSearchParams();
      query.append("json", JSON.stringify(resp.data));
      const accessToken = localStorage.getItem("access_token");
      if (accessToken) {
        query.append("access_token", accessToken);
      }
      const alert = await this.alertController.create({
        header: 'Type',
        inputs: [
          {
            label: 'PDF',
            type: 'radio',
            value: 'pdf'
          },
          {
            label: 'JPEG',
            type: 'radio',
            value: 'jpeg'
          },
          {
            label: 'SVG',
            type: 'radio',
            value: 'svg'
          }
        ],
        buttons: [
          {text: 'Cancel', role: 'cancel'},
          {
            text: 'OK', handler: async (e) =>
              window.open((await environment()).apiUrl + '/template/' + t.id + '/render/' + e + '?' + query.toString())
          }
        ]
      });
      await alert.present();
    }
  }

  async getRenderedJpegUrl(t: string, json: string) {
    const query = new URLSearchParams();
    query.append("json", json);
    const accessToken = localStorage.getItem("access_token");
    if (accessToken) {
      query.append("access_token", accessToken);
    }
    return (await environment()).apiUrl + '/template/' + t + '/render/jpeg' + '?' + query.toString()
  }

  async printTemplate(t: Template) {
    const resp = await this.guiUtils.parametersFormModal("Print a Template", new Set<string>(t.markers), t.id);
    if (!resp.role) {
      const params = resp.data || {};
      const {data, role} = await this.guiUtils.printRequestModal();
      if (role === 'confirm') {

        const loading = await this.loadingController.create();
        await loading.present();
        try {
          await this.templateService.print(t.id, params, data.client.identifier, data.printService, data.copies);
          await this.screenMessageService.showDone();
        } finally {
          await loading.dismiss();
        }
      }

    }
  }


  public openViewer(template: Template) {
    var url = 'https://viewer.diagrams.net/?embed=1&ui=atlas&spin=1&modified=unsavedChanges&proto=json&hide-pages=1';

    if (this.drawIoWindow == null || this.drawIoWindow.closed) {
      // Implements protocol for loading and exporting with embedded XML
      const receive = (evt: any) => {
        if (evt.data.length > 0 && evt.source == this.drawIoWindow) {
          var msg = JSON.parse(evt.data);

          // Received if the editor is ready
          if (msg.event == 'init') {
            // Sends the data URI with embedded XML to editor
            this.drawIoWindow?.postMessage(JSON.stringify(
              {action: 'load', xml: template.xml}), '*');
          }
        }
      };

      // Opens the editor
      window.addEventListener('message', receive);
      this.drawIoWindow = window.open(url);
    } else {
      // Shows existing editor window
      this.drawIoWindow?.focus();
    }

  }
}
