import {Component, OnInit} from '@angular/core';
import {RendererService} from "../../../services/renderer.service";
import {Renderer} from "../../../common/types/renderer";

@Component({
  selector: 'app-renderer-list',
  templateUrl: './renderer-list.page.html',
  styleUrls: ['./renderer-list.page.scss'],
})
export class RendererListPage implements OnInit {

  renderers: Renderer[] = []

  constructor(
    private rendererService: RendererService
  ) {
  }

  async ionViewWillEnter() {
    this.renderers = (await this.rendererService.findAll()).content;
  }

  ngOnInit() {
  }

  delete(r: Renderer) {
    this.renderers = this.renderers.filter(rn => rn.id !== r.id)
    return this.rendererService.findById(r.id);
  }
}
