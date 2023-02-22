import {Component, OnInit} from '@angular/core';
import {RendererService} from "../../../services/api/renderer.service";
import {Renderer} from "../../../common/types/renderer";
import {ListPage} from "../../../common/utils/ui-patterns/list-page";

@Component({
  selector: 'app-renderer-list',
  templateUrl: './renderer-list.page.html',
  styleUrls: ['./renderer-list.page.scss'],
})
export class RendererListPage extends ListPage<Renderer> implements OnInit {

  constructor(
    private rendererService: RendererService
  ) {
    super(rendererService);
  }
  ngOnInit() {
  }

  delete(r: Renderer) {
    this.elements = this.elements?.filter(rn => rn.id !== r.id)
    return this.rendererService.delete(r.id);
  }

}
