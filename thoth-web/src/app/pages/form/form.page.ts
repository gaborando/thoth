import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {Form} from "../../common/types/form";
import {TemplateGuiUtilsService} from "../../services/template-gui-utils.service";
import {environment} from "../../../environments/environment";
import {AlertController} from "@ionic/angular";
import * as CryptoJS from "crypto-js";
import {alert} from "ionicons/icons";

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

  async ngOnInit() {
    let hasError = false;
    do {
      const alert = await this.alertController.create({
        header: 'Insert Password',
        message: 'This page is password protected.',
        inputs: [
          {
            label: 'Password',
            type: 'password',
            name: 'password',
            placeholder: 'Enter a Password'
          }
        ],
        buttons: [
          {
            text: 'Cancel',
            role: 'cancel'
          },
          {
            text: 'OK'
          }
        ]
      });
      await alert.present();
      const resp = await alert.onDidDismiss()
      const password = resp.data.values.password;
      const enc = atob(this.route.snapshot.queryParamMap.get('j') || '');
      try {
        if (password && enc) {
          const dec = CryptoJS.AES.decrypt(enc, password).toString(CryptoJS.enc.Utf8);
          this.form = JSON.parse(dec);
          const last = localStorage.getItem("last_form_" + this.form?.id);
          if (last) {
            this.data = JSON.parse(last);
          }
          hasError = false;
        }
      } catch (e) {
        console.error(e);
        hasError = true;
        const err = await this.alertController.create({
          header: 'Error',
          message: 'Invalid password or encrypted data',
          buttons: ['OK']
        })
        await err.present();
        await err.onDidDismiss();
      }
    }while (hasError);


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
