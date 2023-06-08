import {Component, OnInit} from '@angular/core';
import {TemplateService} from "../../../services/api/template.service";
import {Template} from "../../../common/types/template";
import {AlertController, LoadingController} from "@ionic/angular";
import {ClientService} from "../../../services/api/client.service";
import {ScreenMessageService} from "../../../services/screen-message.service";
import {ListPage} from "../../../common/utils/ui-patterns/list-page";
import {GuiUtilsService} from "../../../services/gui-utils.service";
import {TemplateGuiUtilsService} from "../../../services/template-gui-utils.service";
import {Editor} from "../editor";


@Component({
  selector: 'app-template-list',
  templateUrl: './template-list.page.html',
  styleUrls: ['./template-list.page.scss'],
})
export class TemplateListPage extends ListPage<Template> implements OnInit {

  private editor = new Editor();

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
    this.editor.openEditor(template, this.templateService);

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
