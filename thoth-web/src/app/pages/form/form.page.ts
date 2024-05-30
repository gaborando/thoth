import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {Form} from "../../common/types/form";
import {TemplateGuiUtilsService} from "../../services/template-gui-utils.service";
import {environment} from "../../../environments/environment";
import {AlertController} from "@ionic/angular";

@Component({
  selector: 'app-form',
  templateUrl: './form.page.html',
  styleUrls: ['./form.page.scss'],
})
export class FormPage implements OnInit {

  form?: Form;
  data: any = {}

  constructor(private route: ActivatedRoute,
              private templateGuiUtils: TemplateGuiUtilsService,
              private alertController: AlertController) {
  }

  ngOnInit() {
    this.form = JSON.parse(this.route.snapshot.queryParamMap.get('j') || '{}')
    const last = localStorage.getItem("last_form_" + this.form?.id);
    if (last) {
      this.data = JSON.parse(last);
    }

  }

  async render() {
    if (this.form?.type === 'template')
      this.templateGuiUtils.renderTemplateDirect(this.form!.id, this.data, 'API_KEY', this.form.token);
    else {
      var query = new URLSearchParams();
      for (const key of Object.keys(this.data || {})) {
        query.append(key, this.data[key]);
      }

      query.append("API_KEY", this.form!.token);
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
              window.open((await environment()).apiUrl + '/renderer/' + this.form?.id + '/render/' + e + '?' + query.toString())
          }
        ]
      });
      await alert.present();
    }

    localStorage.setItem("last_form_" + this.form?.id, JSON.stringify(this.data));
  }


}
