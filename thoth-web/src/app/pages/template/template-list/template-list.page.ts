import {Component, OnDestroy, OnInit} from '@angular/core';
import {TemplateService} from "../../../services/api/template.service";
import {Template} from "../../../common/types/template";
import {AlertController, LoadingController, NavController} from "@ionic/angular";
import {ClientService} from "../../../services/api/client.service";
import {ScreenMessageService} from "../../../services/screen-message.service";
import {ListPage} from "../../../common/utils/ui-patterns/list-page";
import {GuiUtilsService} from "../../../services/gui-utils.service";
import {TemplateGuiUtilsService} from "../../../services/template-gui-utils.service";
import {Editor} from "../editor";
import {Page} from "../../../common/utils/fetchUtils";
import {ActivatedRoute} from "@angular/router";
import {Subscription} from "rxjs";
import {navigate} from "ionicons/icons";


@Component({
  selector: 'app-template-list',
  templateUrl: './template-list.page.html',
  styleUrls: ['./template-list.page.scss'],
})
export class TemplateListPage extends ListPage<Template> implements OnInit, OnDestroy {

  public currentFolder: { name: string, path: string }[] = [];
  private editor = new Editor();
  public folders: { name: string, path: string }[] = [];
  private sub: Subscription | undefined;

  constructor(private templateService: TemplateService,
              private alertController: AlertController,
              private clientService: ClientService,
              private screenMessageService: ScreenMessageService,
              private loadingController: LoadingController,
              private guiUtils: GuiUtilsService,
              private templateGuiUtils: TemplateGuiUtilsService,
              private route: ActivatedRoute) {
    super(templateService);

  }


  override async ionViewWillEnter(): Promise<void> {
  }

  ngOnDestroy() {
    if (this.sub) {
      this.sub.unsubscribe();
    }

  }

  ngOnInit() {
    this.sub = this.route.url.subscribe(async u => {
      let last = "/";
      const cf = [];
      for (const folder of u) {
        cf.push({
          name: folder.path,
          path: last + folder
        })
        last += folder + "/"
      }
      const current = '/' + u.map(u => u.path).join('/');
      this.currentFolder = cf;
      const folders = new Set((await this.templateService.getFolders()).filter(s => s.startsWith(current))
        .map(s => s.replace(current, '')
          .split('/'))
        .map(s => s[0] || s[1])
        .filter(s => s))
      const a = [];
      for (const folder of folders) {
        a.push({
          name: folder,
          path: ['/template-list'].concat(u.map(u => u.path)).join('/') + '/' + folder
        })
      }
      this.folders = a;
      return this.loadPageData();
    })
  }

  override composeSearchFilter(): string {
    let filter = 'folder==/' + window.location.pathname.substring(15);
    if(this.search){
      filter = 'name==*'+this.search+'*'
    }
    return filter
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
      this.elements?.unshift(t);
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
