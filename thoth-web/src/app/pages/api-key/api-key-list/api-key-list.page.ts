import {Component, OnInit} from '@angular/core';
import {AlertController, IonModal, ToastController} from "@ionic/angular";
import {ScreenMessageService} from "../../../services/screen-message.service";
import {ApiKeyService} from "../../../services/api/api-key.service";
import {ApiKey} from "../../../common/types/api-key";
import {ListPage} from "../../../common/utils/ui-patterns/list-page";
import {text} from "ionicons/icons";

@Component({
  selector: 'app-api-key-list',
  templateUrl: './api-key-list.page.html',
  styleUrls: ['./api-key-list.page.scss'],
})
export class ApiKeyListPage extends ListPage<ApiKey> implements OnInit {


  constructor(
    private screenMessageService: ScreenMessageService,
    private apiKeyService: ApiKeyService,
    private alertController: AlertController,
    private toastController: ToastController
  ) {
    super(apiKeyService)
  }


  ngOnInit() {
  }

  createApiKey(modal: IonModal, name: any, expiry: any) {
    const date = new Date(expiry);
    const millis = date ? date.getTime() / 1000 : null;
    return this.screenMessageService.loadingWrapper(async () => {
      const key = await this.apiKeyService.create(name, millis);
      this.elements?.push(key);
      const alert = await this.alertController.create({
        header: "ApiKey",
        message: key.apiKey,
        buttons: [
          {
            text: 'OK',
            role: "cancel",
            handler: () => {
              return modal.dismiss()
            }
          },
          {
            text: "Copy",
            role: "copy",
            handler: async () => {
              await navigator.clipboard.writeText(key.apiKey);
              const toast = await this.toastController.create({
                header: "ApiKey copied in clipboard!",
                duration: 2000,
                color: "success"
              });
              await toast.present();
              return modal.dismiss()
            }
          }
        ]
      })
      return alert.present();
    })
  }

  delete(key: ApiKey) {
    return this.screenMessageService.showDeleteAlert(async () => {
      await this.apiKeyService.delete(key.id);
      this.elements = this.elements?.filter(k => k.id !== key.id);
    })
  }
}
