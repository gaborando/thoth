import {Component, OnInit} from '@angular/core';
import {RendererService} from "../../../services/api/renderer.service";
import {Renderer} from "../../../common/types/renderer";
import {ListPage} from "../../../common/utils/ui-patterns/list-page";
import {Template} from "../../../common/types/template";
import {TemplateService} from "../../../services/api/template.service";
import {ScreenMessageService} from "../../../services/screen-message.service";
import {IonModal, NavController} from "@ionic/angular";

@Component({
  selector: 'app-renderer-list',
  templateUrl: './renderer-list.page.html',
  styleUrls: ['./renderer-list.page.scss'],
})
export class RendererListPage extends ListPage<Renderer> implements OnInit {

  selectedTemplate: Template | undefined;
  rendererName: string | undefined;

  constructor(
    private rendererService: RendererService,
    public templateService: TemplateService,
    private screenMessageService: ScreenMessageService,
    private navController: NavController
  ) {
    super(rendererService);
  }
  ngOnInit() {
  }

  override composeSearchFilter(): string {
    if(this.search) {
      return 'name==*' + this.search + '*'
    }else{
      return '';
    }
  }
  delete(r: Renderer) {
    this.elements = this.elements?.filter(rn => rn.id !== r.id)
    return this.rendererService.delete(r.id);
  }

  createRenderer(newModal: IonModal) {
    return this.screenMessageService.loadingWrapper(async () => {
      const r = await this.rendererService.create(this.rendererName, this.selectedTemplate, [], {});
      await this.screenMessageService.showDone();
      await newModal.dismiss();
      return this.navController.navigateForward('/renderer-detail/' + r.id);
    })
  }

}
