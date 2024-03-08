import {Component, OnInit} from '@angular/core';
import {Renderer} from "../../../common/types/renderer";
import {RendererService} from "../../../services/api/renderer.service";
import {ActivatedRoute} from "@angular/router";
import {ScreenMessageService} from "../../../services/screen-message.service";
import {AlertController, IonItem, IonPopover, LoadingController, NavController} from "@ionic/angular";
import {environment} from "../../../../environments/environment";
import {Template} from "../../../common/types/template";
import {ClientService} from "../../../services/api/client.service";
import {GuiUtilsService} from "../../../services/gui-utils.service";
import {DataFetcher} from "../../../common/utils/service-patterns/data-fetcher";
import {Page} from "../../../common/utils/fetchUtils";
import {Datasource} from "../../../common/types/datasource";
import {DataSourceService} from "../../../services/api/data-source.service";
import {TemplateGuiUtilsService} from "../../../services/template-gui-utils.service";

@Component({
  selector: 'app-renderer-detail',
  templateUrl: './renderer-detail.page.html',
  styleUrls: ['./renderer-detail.page.scss'],
})
export class RendererDetailPage implements OnInit {
  renderer: Renderer | null = null;
  availableProperties: {ds: Datasource, property: {name: string, helper: string}}[] = [];
  parameters = new Set<string>();

  ref = this;

  constructor(private rendererService: RendererService,
              private screenMessageService: ScreenMessageService,
              private navController: NavController,
              private alertController: AlertController,
              private route: ActivatedRoute,
              private clientService: ClientService,
              public datasourceService: DataSourceService,
              private loadingController: LoadingController,
              private guiUtils: GuiUtilsService,
              private templateGuiUtils: TemplateGuiUtilsService) {
  }

  async ionViewWillEnter() {
    const resp = await this.rendererService.findById(this.route.snapshot.paramMap.get('identifier'))
    this.init(resp);

  }

  init(resp: Renderer){
    this.availableProperties = [];
    this.parameters = new Set<string>();
    const tmp: {ds: Datasource, property: {name: string, helper: string}}[] = [];
    for (const d of resp.datasourceProperties) {
      for (const p of d.properties) {
        tmp.push({
          ds: d,
          property: p
        })
      }
      for (const p of d.parameters) {
        this.parameters.add(p);
        if(!resp.parametersMap[p]){
          resp.parametersMap[p] = {
            type: 'parameter',
            id: null,
            property: null,
            association: null
          }
        }
      }
    }
    this.availableProperties = tmp.sort((a, b) => (a.ds.name + a.property.name).localeCompare(b.ds.name + b.property.name));
    for (const a of Object.keys(resp.associationMap)) {
      if (resp.associationMap[a].type === 'datasource') {
        resp.associationMap[a].association = this.availableProperties.find(ap => ap.ds.id === resp.associationMap[a].id && ap.property.name === resp.associationMap[a].property)
      } else {
        resp.associationMap[a].association = 'parameter'
      }
    }
    for (const a of Object.keys(resp.parametersMap)) {
      if (resp.parametersMap[a].type === 'datasource') {
        resp.parametersMap[a].association = this.availableProperties.find(ap => ap.ds.id === resp.parametersMap[a].id && ap.property.name === resp.parametersMap[a].property)
      } else {
        resp.parametersMap[a].association = 'parameter'
      }
    }
    this.renderer = resp;
  }

  ngOnInit() {
  }

  updateAvailableAssociations(event: any) {
    const tmp = [];
    this.parameters = new Set<string>();
    for (const d of event || []) {
      for (const p of d.properties) {
        tmp.push({
          ds: d,
          property: p
        })
      }
      for (const p of d.parameters) {
        this.parameters.add(p);
        if(!this.renderer?.parametersMap[p] && this.renderer){
          this.renderer.parametersMap[p] = {
            type: 'parameter',
            id: null,
            property: null,
            association: null
          }
        }
      }
    }

    this.availableProperties = tmp.sort((a, b) => (a.ds.name + a.property).localeCompare(b.ds.name + b.property));
  }

  updateAssociationMap(p: string, association: any) {
    if (!this.renderer) {
      return
    }
    if (!association) {
      delete this.renderer.associationMap[p];
    } else if (association === 'parameter') {
      this.renderer.associationMap[p] = {
        type: 'parameter',
        id: null,
        property: null,
        association
      }
    } else {
      this.renderer.associationMap[p] = {
        type: 'datasource',
        id: association.ds.id,
        property: association.property.name,
        association
      }
    }
  }

  async updateRenderer() {
    return this.screenMessageService.loadingWrapper(async () => {
      await this.rendererService.update(this.renderer);
      await this.screenMessageService.showDone();
    })
  }

  delete(renderer: Renderer) {
    return this.screenMessageService.showDeleteAlert(async () => {
      await this.rendererService.delete(renderer.id);
      return this.navController.navigateBack('/renderer-list');
    })
  }

  async playRenderer() {
    if (!this.renderer) {
      return;
    }
    const inputs = new Set<string>();
    for (const p of Object.keys(this.renderer.associationMap)) {
      if (this.renderer.associationMap[p].type === 'parameter') {
        inputs.add(p)
      }
    }
    for (const ds of this.renderer.datasourceProperties) {
      for (const p of ds.parameters) {
        inputs.add(p)
      }
    }
    const resp = await this.guiUtils.parametersFormModal("Render a Template", inputs, this.renderer.id);
    if (!resp.role) {

      var query = new URLSearchParams();
      for (const key of Object.keys(resp.data || {})) {
        query.append(key, resp.data[key]);
      }

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
              window.open((await environment()).apiUrl + '/renderer/' + this.renderer?.id + '/render/'+e+'?' + query.toString())
          }
        ]
      });
      await alert.present();


    }
  }



  async printRenderer() {
    if (!this.renderer) {
      return;
    }
    const inputs = new Set<string>();
    for (const p of Object.keys(this.renderer.associationMap)) {
      if (this.renderer.associationMap[p].type === 'parameter') {
        inputs.add(p)
      }
    }
    for (const ds of this.renderer.datasourceProperties) {
      for (const p of ds.parameters) {
        inputs.add(p)
      }
    }
    const resp = await this.guiUtils.parametersFormModal("Print a Template", inputs, this.renderer.id);
    if (!resp.role) {
      const params = resp.data;
      const {data, role} = await this.guiUtils.printRequestModal();
      if (role === 'confirm') {

        const loading = await this.loadingController.create();
        await loading.present();
        try {
          await this.rendererService.print(this.renderer.id, params, data.client.identifier, data.printService, data.copies);
          await this.screenMessageService.showDone();
        } finally {
          await loading.dismiss();
        }
      }
    }
  }

  clone() {
    return this.screenMessageService.loadingWrapper(async () => {
      const r = await this.rendererService.create(this.renderer?.name, this.renderer?.template, this.renderer?.datasourceProperties, this.renderer?.associationMap);
      await this.screenMessageService.showDone();
      return this.navController.navigateForward('/renderer-detail/' + r.id);
    })

  }

  ku($event: KeyboardEvent) {
    console.log($event);
  }

  presentPopover(popover: IonPopover, $event: MouseEvent, i: any) {
    popover.present({...$event, target: i.el});
  }

  updateParameterMap(p: string, association: any) {
    if (!this.renderer) {
      return
    }
    if (!association) {
      delete this.renderer.parametersMap[p];
    } else if (association === 'parameter') {
      this.renderer.parametersMap[p] = {
        type: 'parameter',
        id: null,
        property: null,
        association
      }
    } else {
      this.renderer.parametersMap[p] = {
        type: 'datasource',
        id: association.ds.id,
        property: association.property.name,
        association
      }
    }
  }

  openViewer(template: Template) {
    this.templateGuiUtils.openViewer(template);
  }
}
