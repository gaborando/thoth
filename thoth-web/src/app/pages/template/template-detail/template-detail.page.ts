import {Component, OnInit} from '@angular/core';
import {Template} from "../../../common/types/template";
import {TemplateService} from "../../../services/api/template.service";
import {AlertController, LoadingController, NavController} from "@ionic/angular";
import {ClientService} from "../../../services/api/client.service";
import {ScreenMessageService} from "../../../services/screen-message.service";
import {GuiUtilsService} from "../../../services/gui-utils.service";
import {TemplateGuiUtilsService} from "../../../services/template-gui-utils.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-template-detail',
  templateUrl: './template-detail.page.html',
  styleUrls: ['./template-detail.page.scss'],
})
export class TemplateDetailPage implements OnInit {
  template: Template | undefined;
  private drawIoWindow: Window | null = null;

  constructor(private templateService: TemplateService,
              private alertController: AlertController,
              private clientService: ClientService,
              private screenMessageService: ScreenMessageService,
              private loadingController: LoadingController,
              private guiUtils: GuiUtilsService,
              private navController: NavController,
              private templateGuiUtils: TemplateGuiUtilsService,
              private route: ActivatedRoute) {
  }

  async ionViewWillEnter() {
    this.template = await this.templateService.findById(this.route.snapshot.paramMap.get('identifier'))
  }
  ngOnInit() {
  }

  delete(template: Template) {
    return this.screenMessageService.showDeleteAlert(async () => {
      await this.templateService.delete(template.id);
      return this.navController.navigateBack('/template-list')
    });
  }


  render() {
    if (!this.template) {
      return;
    }
    return this.templateGuiUtils.renderTemplate(this.template);
  }

  print() {
    if (!this.template) {
      return;
    }
    return this.templateGuiUtils.printTemplate(this.template);
  }

  update() {
    if (!this.template) {
      return;
    }
    const t = this.template;
    return this.screenMessageService.loadingWrapper(async () => {
      this.templateService.update(t).finally();
      await this.screenMessageService.showDone();
    })

  }

  openEditor() {

    if(!this.template){
      return;
    }
    const template = this.template;

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

  async clone() {
    if(!this.template){
      return;
    }
    const t = await this.templateService.create(this.template.name);
    t.xml = this.template.xml;
    t.svg = this.template.svg;
    t.img = this.template.img;
    t.markers = this.template.markers;
    t.allowedOrganizationList = this.template.allowedOrganizationList;
    t.allowedUserList = this.template.allowedUserList;
    return this.screenMessageService.loadingWrapper(async () => {
      this.templateService.update(t).finally();
      await this.screenMessageService.showDone();
      return this.navController.navigateForward('/template-detail/' + t.id);
    })
  }
}
