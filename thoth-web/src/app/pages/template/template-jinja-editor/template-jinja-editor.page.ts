import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {TemplateService} from "../../../services/api/template.service";
import {Template} from "../../../common/types/template";
import {CodemirrorComponent} from "@ctrl/ngx-codemirror";
import {TemplateGuiUtilsService} from "../../../services/template-gui-utils.service";

@Component({
  selector: 'app-template-jinja-editor',
  templateUrl: './template-jinja-editor.page.html',
  styleUrls: ['./template-jinja-editor.page.scss'],
})
export class TemplateJinjaEditorPage implements OnInit {

  template: Template | undefined;
  jsonData = "{}";
  renderUrl: string | undefined;
  id: string | null = null;
  html: string | undefined = "<h1></h1>";

  constructor(private route: ActivatedRoute, private templateService: TemplateService,
              private templateGuiUtils: TemplateGuiUtilsService) {

  }

  async ionViewWillEnter() {
    this.id = this.route.snapshot.paramMap.get('identifier');
    if (this.id) {
      this.template = await this.templateService.findById(this.id)
      console.log(this.template);
      this.html = this.template?.svg;
      const last = localStorage.getItem("last_form_" + this.id) || "{}";
      const data = JSON.parse(last);
      this.jsonData = JSON.stringify(data, null, 4);
      return this.render()
    }
  }

  ngOnInit() {
  }

  codeLoaded($event: CodemirrorComponent) {

  }

  jsonChanged($event: KeyboardEvent) {

  }

  async render() {
    if (this.id) {
      const json = JSON.stringify(JSON.parse(this.jsonData));
      localStorage.setItem("last_form_" + this.id, json);
      this.renderUrl = await this.templateGuiUtils.getRenderedJpegUrl(this.id, json);
    }
  }

  htmlChanged($event: KeyboardEvent) {

  }

  format(cm: any ) {
    cm?.codeMirror?.autoFormat()
  }
}
