import {Component, OnInit} from '@angular/core';
import {TemplateService} from "../../../services/api/template.service";
import {Template} from "../../../common/types/template";
import {AlertController, LoadingController} from "@ionic/angular";
import {ClientService} from "../../../services/api/client.service";
import {ScreenMessageService} from "../../../services/screen-message.service";
import {ListPage} from "../../../common/utils/ui-patterns/list-page";
import {GuiUtilsService} from "../../../services/gui-utils.service";
import {TemplateGuiUtilsService} from "../../../services/template-gui-utils.service";


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
              private loadingController: LoadingController,
              private guiUtils: GuiUtilsService,
              private templateGuiUtils: TemplateGuiUtilsService) {
    super(templateService);
  }

  ngOnInit() {
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
          console.log(msg);
          // Received if the editor is ready
          if (msg.event == 'init') {
            // Sends the data URI with embedded XML to editor
            this.drawIoWindow?.postMessage(JSON.stringify(
              {action: 'load', xml: template.xml, autosave: 1}), '*');
          }
          // Received if the user clicks save
          else if (msg.event == 'save' || msg.event == 'autosave') {
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

            const regex = /{{([a-zA-Z0-9\._]+)(\|[a-zA-Z0-9]+(:[a-zA-Z0-9_'-\.\\/~]+)*)*}}/g;

            let m;

            while ((m = regex.exec(template.svg)) !== null) {
              // This is necessary to avoid infinite loops with zero-width matches
              if (m.index === regex.lastIndex) {
                regex.lastIndex++;
              }

              // The result can be accessed through the `m`-variable.
              m.forEach((match, groupIndex) => {
                if(groupIndex === 1){
                  template.markers.push(match);
                }
              });
            }
            template.markers = [...new Set(template.markers)];

            if(!template.markers.includes('block**') && (template.markers.includes('_barcode') || template.markers.includes('_qrcode'))){
              this.drawIoWindow?.postMessage(JSON.stringify(
                {action: 'prompt', title: 'Specify marker for barcode', ok: 'Insert', defaultValue: 'barcode'} ), '*');
            }
          } else if (msg.event == 'prompt') {

            var xmlDoc = new DOMParser().parseFromString(template.xml || '', 'application/xml');
            var diagrams = xmlDoc.getElementsByTagName('diagram');
            const node: any = diagrams[0].children.item(0)?.outerHTML;
            // @ts-ignore
            const n = node.replaceAll('{{_barcode}}', '{{'+msg.value+'}}');
            this.drawIoWindow?.postMessage(JSON.stringify(
              {action: 'merge', xml: n} ), '*');

          }

          // Received if the user clicks exit or after export
          if (msg.event == 'exit') {
            // Closes the editor
            window.removeEventListener('message', receive);
            this.drawIoWindow?.close();
            this.drawIoWindow = null;
            this.templateService.update(template).finally();
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
    return this.templateGuiUtils.renderTemplate(t);
  }

  async printTemplate(t: Template) {
    return this.templateGuiUtils.printTemplate(t);
  }
}
