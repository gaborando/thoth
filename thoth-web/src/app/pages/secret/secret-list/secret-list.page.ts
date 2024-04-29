import {Component, OnInit} from '@angular/core';
import {ScreenMessageService} from "../../../services/screen-message.service";
import {ApiKeyService} from "../../../services/api/api-key.service";
import {AlertController, IonInput, IonModal, ToastController} from "@ionic/angular";
import {ApiKey} from "../../../common/types/api-key";
import {ListPage} from "../../../common/utils/ui-patterns/list-page";
import {Secret} from "../../../common/types/secret";
import {SecretService} from "../../../services/api/secret.service";

@Component({
  selector: 'app-secret-list',
  templateUrl: './secret-list.page.html',
  styleUrls: ['./secret-list.page.scss'],
})
export class SecretListPage extends ListPage<Secret> implements OnInit {
  selectedSecret?: Secret;


  constructor(
    private screenMessageService: ScreenMessageService,
    private secretService: SecretService
  ) {
    super(secretService)
  }


  ngOnInit() {
  }

  crateSecret(modal: IonModal, name: any,
              value: any) {
    return this.screenMessageService.loadingWrapper(async () => {
      const key = await this.secretService.create(name,
        value);
      this.elements?.push(key);
      return modal.dismiss();
    }, true)
  }

  delete(secret: Secret) {
    return this.screenMessageService.showDeleteAlert(async () => {
      await this.secretService.delete(secret.name);
      this.elements = this.elements?.filter(s => s.name !== secret.name);
    })
  }


  updateSharingOptions(modal: IonModal) {
    if(!this.selectedSecret){
      return ;
    }
    const c = this.selectedSecret;
    return this.screenMessageService.loadingWrapper(async () => {
      await this.secretService.updateSecrets(c);
      await this.screenMessageService.showDone();
      return modal.dismiss();
    })
  }
}
