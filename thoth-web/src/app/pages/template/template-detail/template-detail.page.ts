import {Component, OnInit} from '@angular/core';
import {Template} from "../../../common/types/template";
import {TemplateService} from "../../../services/api/template.service";
import {AlertController, LoadingController, NavController} from "@ionic/angular";
import {ClientService} from "../../../services/api/client.service";
import {ScreenMessageService} from "../../../services/screen-message.service";
import {GuiUtilsService} from "../../../services/gui-utils.service";
import {TemplateGuiUtilsService} from "../../../services/template-gui-utils.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Editor} from "../editor";
import {AutocompleteProvider} from "../../../common/directives/autocomplete.directive";

@Component({
  selector: 'app-template-detail',
  templateUrl: './template-detail.page.html',
  styleUrls: ['./template-detail.page.scss'],
})
export class TemplateDetailPage implements OnInit {
  template: Template | undefined;
  private editor = new Editor(this.router);
  public folderAutocomplete: AutocompleteProvider

  constructor(private templateService: TemplateService,
              private alertController: AlertController,
              private clientService: ClientService,
              private screenMessageService: ScreenMessageService,
              private loadingController: LoadingController,
              private router: Router,
              private navController: NavController,
              private templateGuiUtils: TemplateGuiUtilsService,
              private route: ActivatedRoute) {
    this.folderAutocomplete = {
      async fetch(txt: string){
        return templateService.getFolders().then(s => s.filter(ss => ss.startsWith(txt)));
      }
    }
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
      await this.templateService.update(t);
      await this.screenMessageService.showDone();
    }, true)

  }

  openEditor() {

    if (!this.template) {
      return;
    }
    const template = this.template;

    this.editor.openEditor(template, this.templateService);
  }

  async clone() {
    if (!this.template) {
      return;
    }
    const t = await this.templateService.create(this.template.name);
    t.xml = this.template.xml;
    t.svg = this.template.svg;
    t.img = this.template.img;
    t.markers = this.template.markers;
    t.allowedOrganizationList = this.template.allowedOrganizationList;
    t.allowedUserList = this.template.allowedUserList;
    t.folder = this.template.folder
    return this.screenMessageService.loadingWrapper(async () => {
      this.templateService.update(t).finally();
      await this.screenMessageService.showDone();
      return this.navController.navigateForward('/template-detail/' + t.id);
    })
  }

  openJinjaEditor() {
    return this.navController.navigateForward('/template-jinja-editor/' + this.template?.id)
  }

  showAutocomplete($event: any) {
    console.log($event);
  }

  openViewer() {
    this.templateGuiUtils.openViewer(this.template!);
  }
}
