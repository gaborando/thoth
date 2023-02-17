import {Component, OnInit} from '@angular/core';
import {TemplateService} from "../../../services/api/template.service";
import {Template} from "../../../common/types/template";
import {AlertController, LoadingController} from "@ionic/angular";
import {DomSanitizer} from "@angular/platform-browser";
import {environment} from "../../../../environments/environment";
import {ClientService} from "../../../services/api/client.service";
import {ScreenMessageService} from "../../../services/screen-message.service";
import {ListPage} from "../../../common/utils/ui-patterns/list-page";

@Component({
  selector: 'app-template-list',
  templateUrl: './template-list.page.html',
  styleUrls: ['./template-list.page.scss'],
})
export class TemplateListPage extends ListPage<Template> implements OnInit {

  private drawIoWindow: Window | null = null;

  constructor(private templateService: TemplateService,
              private alertController: AlertController,
              private clientService: ClientService,
              private screenMessageService: ScreenMessageService,
              private loadingController: LoadingController) {
    super(templateService);
  }

  ngOnInit() {
    return super.loadPageData();
  }

  async createTemplate() {
    const alert = await this.alertController.create({
      header: "Create a Template",
      inputs: [
        {
          id: 'name',
          label: 'Name',
          name: 'name',
          placeholder: 'Name'
        }
      ],
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
      const t = await this.templateService.create(resp.data.values.name);
      this.elements?.push(t);
      return this.openEditor(t);
    }
  }

  public openEditor(template: Template) {
    var url = 'https://embed.diagrams.net/?embed=1&ui=atlas&spin=1&modified=unsavedChanges&proto=json&hide-pages=1';

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
          // Received if the user clicks save
          else if (msg.event == 'save') {
            // Sends a request to export the diagram as XML with embedded PNG
            template.xml = msg.xml;
            this.drawIoWindow?.postMessage(JSON.stringify(
              {action: 'export', format: 'svg', spinKey: 'saving', embedImages: false}), '*');
          }
          // Received if the export request was processed
          else if (msg.event == 'export') {
            // Updates the data URI of the image
            template.img = msg.data;
            template.svg = atob(msg.data.replace('data:image/svg+xml;base64,', ''));
            template.markers = [];

            const pattern = new RegExp(/{{([a-zA-Z0-9\\._]+)}}/g);
            let z;
            while (null != (z = pattern.exec(template.svg))) {
              template.markers.push(z[1]);
            }
            template.markers = [...new Set(template.markers)]
          }

          // Received if the user clicks exit or after export
          if (msg.event == 'exit' || msg.event == 'export') {
            // Closes the editor
            window.removeEventListener('message', receive);
            this.drawIoWindow?.close();
            this.drawIoWindow = null;
            this.templateService.update(template).finally();
            console.log(template);
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

  removeTemplate(template: Template) {
    return this.screenMessageService.showDeleteAlert(async () => {
      await this.templateService.delete(template.id);
      this.elements = this.elements?.filter(t => t.id !== template.id)
    });
  }

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
      window.open(environment.apiUrl + '/template/' + t.id + '/render/pdf?' + query.toString())
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

      const params = resp.data.values;
      inputs = [];
      const clients = (await this.clientService.findAll()).content;
      for (const c of clients){
        inputs.push({
          label: c.name,
          type: 'radio',
          value: c
        })
      }
      const alert = await this.alertController.create({
        header: "Select a Client",
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
      const r2 = await alert.onDidDismiss();
      if (!r2.role) {
        const client = r2.data.values;

        inputs = [];
        for (const s of client.printServices){
          inputs.push({
            label: s,
            type: 'radio',
            value: s
          })
        }
        const alert = await this.alertController.create({
          header: "Select a Print Service",
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
        const r3 = await alert.onDidDismiss();
        if (!r3.role) {
          const s = r3.data.values;

          const loading =await this.loadingController.create();
          await loading.present();
          try {
            await this.templateService.print(t.id, params, client.identifier, s);
            await this.screenMessageService.showDone();
          }finally {
            await loading.dismiss();
          }
        }
      }

    }
  }


  async printRenderer(template: Template) {
    let inputs: any[] = [];
    template.markers.forEach(m => {
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
      const params = resp.data.values;
      inputs = [];
      const clients = (await this.clientService.findAll()).content;
      for (const c of clients){
        inputs.push({
          label: c.name,
          type: 'radio',
          value: c
        })
      }
      const alert = await this.alertController.create({
        header: "Select a Client",
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
      const r2 = await alert.onDidDismiss();
      if (!r2.role) {
        const client = r2.data.values;

        inputs = [];
        for (const s of client.printServices){
          inputs.push({
            label: s,
            type: 'radio',
            value: s
          })
        }
        const alert = await this.alertController.create({
          header: "Select a Print Service",
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
        const r3 = await alert.onDidDismiss();
        if (!r3.role) {
          const s = r3.data.values;


          const loading =await this.loadingController.create();
          await loading.present();
          try {
            await this.templateService.print(template.id, params, client.identifier, s);
            await this.screenMessageService.showDone();
          }finally {
            await loading.dismiss();
          }
        }
      }
    }
  }
}
